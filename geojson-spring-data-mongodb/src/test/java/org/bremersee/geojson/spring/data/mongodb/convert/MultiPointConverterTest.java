/*
 * Copyright 2018-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.geojson.spring.data.mongodb.convert;

import java.util.Arrays;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;

/**
 * The multi point converter test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class MultiPointConverterTest {

  private static final GeoJsonGeometryFactory factory = new GeoJsonGeometryFactory();

  /**
   * Convert.
   *
   * @param softly the softly
   */
  @Test
  void convert(SoftAssertions softly) {
    Point model0 = factory.createPoint(7., 8.);
    Point model1 = factory.createPoint(17., 18.);
    MultiPoint model = factory.createMultiPoint(Arrays.asList(model0, model1));

    MultiPointToDocumentConverter toDocumentConverter = new MultiPointToDocumentConverter();

    Document document = toDocumentConverter.convert(model);
    softly.assertThat(document)
        .isNotNull();

    DocumentToMultiPointConverter toGeometryConverter = new DocumentToMultiPointConverter();

    MultiPoint actual = toGeometryConverter.convert(document);
    softly.assertThat(GeoJsonGeometryFactory.equals(actual, model))
        .isTrue();

    DocumentToGeometryConverter converter = new DocumentToGeometryConverter();
    Geometry actualGeometry = converter.convert(document);
    softly.assertThat(GeoJsonGeometryFactory.equals(actual, actualGeometry))
        .isTrue();
  }

}
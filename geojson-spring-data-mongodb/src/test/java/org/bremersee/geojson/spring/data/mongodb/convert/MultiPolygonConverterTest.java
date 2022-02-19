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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

/**
 * The multi polygon converter test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class MultiPolygonConverterTest {

  private static final GeoJsonGeometryFactory factory = new GeoJsonGeometryFactory();

  /**
   * Convert.
   *
   * @param softly the softly
   */
  @Test
  void convert(SoftAssertions softly) {
    LinearRing ring0 = factory.createLinearRing(Arrays.asList(
        new Coordinate(2., 3.),
        new Coordinate(6., 4.),
        new Coordinate(6., 8.),
        new Coordinate(2., 3.)));
    Polygon model0 = factory.createPolygon(ring0);
    LinearRing ring1 = factory.createLinearRing(Arrays.asList(
        new Coordinate(12., 13.),
        new Coordinate(16., 14.),
        new Coordinate(16., 18.),
        new Coordinate(12., 13.)));
    Polygon model1 = factory.createPolygon(ring1);
    MultiPolygon model = factory.createMultiPolygon(Arrays.asList(model0, model1));

    MultiPolygonToDocumentConverter toDocumentConverter = new MultiPolygonToDocumentConverter();

    Document document = toDocumentConverter.convert(model);
    softly.assertThat(document)
        .isNotNull();

    DocumentToMultiPolygonConverter toGeometryConverter = new DocumentToMultiPolygonConverter();

    MultiPolygon actual = toGeometryConverter.convert(document);
    softly.assertThat(GeoJsonGeometryFactory.equals(actual, model))
        .isTrue();

    DocumentToGeometryConverter converter = new DocumentToGeometryConverter();
    Geometry actualGeometry = converter.convert(document);
    softly.assertThat(GeoJsonGeometryFactory.equals(actual, actualGeometry))
        .isTrue();
  }

}
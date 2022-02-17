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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

/**
 * The point converter test.
 *
 * @author Christian Bremer
 */
class PointConverterTest {

  private static final GeoJsonGeometryFactory factory = new GeoJsonGeometryFactory();

  /**
   * Convert.
   */
  @Test
  void convert() {
    Point model = factory.createPoint(7., 8.);

    PointToDocumentConverter toDocumentConverter = new PointToDocumentConverter();

    Document document = toDocumentConverter.convert(model);
    assertNotNull(document);

    DocumentToPointConverter toGeometryConverter = new DocumentToPointConverter();

    Point actual = toGeometryConverter.convert(document);
    assertNotNull(actual);
    assertTrue(GeoJsonGeometryFactory.equals(model, actual));

    DocumentToGeometryConverter converter = new DocumentToGeometryConverter();
    Geometry actualGeometry = converter.convert(document);
    assertNotNull(actualGeometry);
    assertTrue(actualGeometry instanceof Point);
    assertTrue(GeoJsonGeometryFactory.equals(actual, actualGeometry));
  }

}
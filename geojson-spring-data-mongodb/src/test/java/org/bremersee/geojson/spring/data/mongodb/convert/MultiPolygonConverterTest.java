/*
 * Copyright 2018-2020 the original author or authors.
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

import java.util.Arrays;
import org.bremersee.geojson.utils.GeometryUtils;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

/**
 * The multi polygon converter test.
 *
 * @author Christian Bremer
 */
class MultiPolygonConverterTest {

  /**
   * Convert.
   */
  @Test
  void convert() {
    LinearRing ring0 = GeometryUtils.createLinearRing(Arrays.asList(
        new Coordinate(2., 3.),
        new Coordinate(6., 4.),
        new Coordinate(6., 8.),
        new Coordinate(2., 3.)));
    Polygon model0 = GeometryUtils.createPolygon(ring0);
    LinearRing ring1 = GeometryUtils.createLinearRing(Arrays.asList(
        new Coordinate(12., 13.),
        new Coordinate(16., 14.),
        new Coordinate(16., 18.),
        new Coordinate(12., 13.)));
    Polygon model1 = GeometryUtils.createPolygon(ring1);
    MultiPolygon model = GeometryUtils.createMultiPolygon(Arrays.asList(model0, model1));

    MultiPolygonToDocumentConverter toDocumentConverter = new MultiPolygonToDocumentConverter();
    assertNotNull(toDocumentConverter.getConvertHelper());

    Document document = toDocumentConverter.convert(model);
    assertNotNull(document);

    DocumentToMultiPolygonConverter toGeometryConverter = new DocumentToMultiPolygonConverter();

    MultiPolygon actual = toGeometryConverter.convert(document);
    assertNotNull(actual);
    assertTrue(GeometryUtils.equals(model, actual));

    DocumentToGeometryConverter converter = new DocumentToGeometryConverter();
    Geometry actualGeometry = converter.convert(document);
    assertNotNull(actualGeometry);
    assertTrue(actualGeometry instanceof MultiPolygon);
    assertTrue(GeometryUtils.equals(actual, actualGeometry));
  }

}
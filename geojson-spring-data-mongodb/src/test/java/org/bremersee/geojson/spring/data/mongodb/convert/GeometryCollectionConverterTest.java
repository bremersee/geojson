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
import java.util.List;
import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

/**
 * The geometry collection converter test.
 *
 * @author Christian Bremer
 */
class GeometryCollectionConverterTest {

  private static final GeoJsonGeometryFactory factory = new GeoJsonGeometryFactory();

  /**
   * Convert.
   */
  @Test
  void convert() {
    Point model0 = factory.createPoint(7., 8.);
    LineString model1 = factory.createLineString(Arrays.asList(
        new Coordinate(2., 3.),
        new Coordinate(6., 7.)));
    GeometryCollection model = factory.createGeometryCollection(List.of(model0, model1));

    GeometryCollectionToDocumentConverter toDocumentConverter
        = new GeometryCollectionToDocumentConverter();

    Document document = toDocumentConverter.convert(model);
    assertNotNull(document);

    DocumentToGeometryCollectionConverter toGeometryConverter
        = new DocumentToGeometryCollectionConverter();

    GeometryCollection actual = toGeometryConverter.convert(document);
    assertNotNull(actual);
    assertTrue(GeoJsonGeometryFactory.equals(model, actual));

    DocumentToGeometryConverter converter = new DocumentToGeometryConverter();
    Geometry actualGeometry = converter.convert(document);
    assertNotNull(actualGeometry);
    assertTrue(actualGeometry instanceof GeometryCollection);
    assertTrue(GeoJsonGeometryFactory.equals(actual, actualGeometry));
  }

}
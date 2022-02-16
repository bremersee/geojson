/*
 * Copyright 2015-2020 the original author or authors.
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

package org.bremersee.geojson;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

/**
 * The GeoJSON feature collection test.
 *
 * @author Christian Bremer
 */
class GeoJsonFeatureCollectionTest {

  private static final GeoJsonGeometryFactory factory = new GeoJsonGeometryFactory();

  /**
   * Gets features.
   */
  @Test
  void getFeatures() {
    GeoJsonFeatureCollection model = new GeoJsonFeatureCollection();
    assertEquals(model, model);
    assertEquals(model, new GeoJsonFeatureCollection());
    assertEquals(model.hashCode(), new GeoJsonFeatureCollection().hashCode());
    assertEquals(model.toString(), new GeoJsonFeatureCollection().toString());

    assertNotEquals(model, null);
    assertNotEquals(model, new Object());

    Geometry g0 = factory.createLineString(Arrays.asList(
        new Coordinate(1., 1.),
        new Coordinate(5., 1.),
        new Coordinate(6., 3.)));
    GeoJsonFeature f0 = new GeoJsonFeature("f0", g0, true, Collections.singletonMap("k0", "v0"));

    Geometry g1 = factory.createLineString(Arrays.asList(
        new Coordinate(11., 21.),
        new Coordinate(15., 21.),
        new Coordinate(16., 23.)));
    GeoJsonFeature f1 = new GeoJsonFeature("f1", g1, true, Collections.singletonMap("k1", "v1"));

    model.setFeatures(Arrays.asList(f0, f1));
    assertEquals(Arrays.asList(f0, f1), model.getFeatures());
    assertEquals(model, new GeoJsonFeatureCollection(Arrays.asList(f0, f1), false));
    assertEquals(
        model.hashCode(),
        new GeoJsonFeatureCollection(Arrays.asList(f0, f1), false).hashCode());
    assertEquals(
        model.toString(),
        new GeoJsonFeatureCollection(Arrays.asList(f0, f1), false).toString());
  }

  /**
   * Gets bbox.
   */
  @Test
  void getBbox() {
    Geometry g0 = factory.createLineString(Arrays.asList(
        new Coordinate(1., 1.),
        new Coordinate(5., 1.),
        new Coordinate(6., 3.)));
    GeoJsonFeature f0 = new GeoJsonFeature("f0", g0, false, Collections.singletonMap("k0", "v0"));

    Geometry g1 = factory.createLineString(Arrays.asList(
        new Coordinate(11., 21.),
        new Coordinate(15., 21.),
        new Coordinate(16., 23.)));
    GeoJsonFeature f1 = new GeoJsonFeature("f1", g1, false, Collections.singletonMap("k1", "v1"));

    double[] bbox = GeoJsonGeometryFactory.getBoundingBox(Arrays.asList(g0, g1));
    GeoJsonFeatureCollection model = new GeoJsonFeatureCollection();
    model.setFeatures(Arrays.asList(f0, f1));
    model.setBbox(bbox);
    assertArrayEquals(bbox, model.getBbox());

    assertEquals(model, new GeoJsonFeatureCollection(Arrays.asList(f0, f1), true));
    assertEquals(
        model.hashCode(),
        new GeoJsonFeatureCollection(Arrays.asList(f0, f1), true).hashCode());
    assertEquals(
        model.toString(),
        new GeoJsonFeatureCollection(Arrays.asList(f0, f1), true).toString());
  }

}
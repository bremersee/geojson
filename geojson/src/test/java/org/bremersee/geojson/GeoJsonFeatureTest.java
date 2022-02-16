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
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

/**
 * The GeoJSON feature test.
 *
 * @author Christian Bremer
 */
class GeoJsonFeatureTest {

  private static final GeoJsonGeometryFactory factory = new GeoJsonGeometryFactory();

  /**
   * Gets id.
   *
  @Test
  void getId() {
    GeoJsonFeature model = new GeoJsonFeature();
    assertEquals(model, model);
    assertEquals(model, new GeoJsonFeature());
    assertEquals(model.hashCode(), new GeoJsonFeature().hashCode());
    assertEquals(model.toString(), new GeoJsonFeature().toString());

    assertNotEquals(model, null);
    assertNotEquals(model, new Object());

    String value = "123456789";
    model.setId(value);
    assertEquals(value, model.getId());
    assertEquals(model, new GeoJsonFeature(value, null, false, null));
    assertEquals(
        model.hashCode(),
        new GeoJsonFeature(value, null, false, null).hashCode());
    assertEquals(
        model.toString(),
        new GeoJsonFeature(value, null, false, null).toString());
  }

  /**
   * Gets geometry.
   *
  @Test
  void getGeometry() {
    Geometry geometry = factory.createLineString(Arrays.asList(
        new Coordinate(1., 1.),
        new Coordinate(5., 1.),
        new Coordinate(6., 3.)));
    GeoJsonFeature model = new GeoJsonFeature();
    model.setGeometry(geometry);
    assertEquals(geometry, model.getGeometry());
    assertEquals(model, new GeoJsonFeature(null, geometry, false, null));
    assertEquals(
        model.hashCode(),
        new GeoJsonFeature(null, geometry, false, null).hashCode());
    assertEquals(
        model.toString(),
        new GeoJsonFeature(null, geometry, false, null).toString());

    double[] bbox = GeoJsonGeometryFactory.getBoundingBox(geometry);
    model.setBbox(bbox);
    assertArrayEquals(bbox, model.getBbox());
    assertEquals(model, new GeoJsonFeature(null, geometry, true, null));
    assertEquals(
        model.hashCode(),
        new GeoJsonFeature(null, geometry, true, null).hashCode());
    assertEquals(
        model.toString(),
        new GeoJsonFeature(null, geometry, true, null).toString());
  }

  /**
   * Gets properties.
   *
  @Test
  void getProperties() {
    GeoJsonFeature model = new GeoJsonFeature();
    model.getProperties().put("key", "value");
    assertEquals("value", model.getProperties().get("key"));
    assertEquals(
        model,
        new GeoJsonFeature(null, null, false, Collections.singletonMap("key", "value")));

    Map<String, Object> properties = new LinkedHashMap<>();
    properties.put("key", "value");
    model = new GeoJsonFeature();
    model.setProperties(properties);
    assertEquals(properties, model.getProperties());
    assertEquals(model,
        new GeoJsonFeature(null, null, false, Collections.singletonMap("key", "value")));
    assertEquals(
        model.hashCode(),
        new GeoJsonFeature(null, null, false, Collections.singletonMap("key", "value")).hashCode());
    assertEquals(
        model.toString(),
        new GeoJsonFeature(null, null, false, Collections.singletonMap("key", "value")).toString());
  }
  */
}
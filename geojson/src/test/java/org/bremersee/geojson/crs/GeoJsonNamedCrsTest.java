/*
 * Copyright 2015-2022 the original author or authors.
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

package org.bremersee.geojson.crs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * The GeoJSON named crs test.
 *
 * @author Christian Bremer
 */
class GeoJsonNamedCrsTest {

  /**
   * Gets crs.
   */
  @Test
  void getCrs() {
    GeoJsonNamedCrs model = new GeoJsonNamedCrs();
    assertEquals(model, model);
    assertEquals(model, new GeoJsonNamedCrs());
    assertEquals(model.hashCode(), new GeoJsonNamedCrs().hashCode());
    assertEquals(model.toString(), new GeoJsonNamedCrs().toString());

    assertNotEquals(model, null);
    assertNotEquals(model, new Object());

    model.setCrs(null);
    assertNull(model.getCrs());

    model.setCrs(GeoJsonCrsConstants.WGS84_CRS);
    assertEquals(GeoJsonCrsConstants.WGS84_CRS, model.getCrs());
    assertEquals(model.hashCode(), new GeoJsonNamedCrs(GeoJsonCrsConstants.WGS84_CRS).hashCode());
    assertEquals(model.toString(), new GeoJsonNamedCrs(GeoJsonCrsConstants.WGS84_CRS).toString());

    Map<String, Object> properties = new LinkedHashMap<>();
    properties.put("name", GeoJsonCrsConstants.MERCATOR_CRS);
    model.setProperties(properties);
    assertEquals(properties, model.getProperties());
    assertEquals(GeoJsonCrsConstants.MERCATOR_CRS, model.getCrs());
    assertEquals(model.hashCode(),
        new GeoJsonNamedCrs(GeoJsonCrsConstants.MERCATOR_CRS).hashCode());
    assertEquals(model.toString(),
        new GeoJsonNamedCrs(GeoJsonCrsConstants.MERCATOR_CRS).toString());

    model.setProperties(null);
    assertEquals(model, new GeoJsonNamedCrs(null));
    assertEquals(model.hashCode(), new GeoJsonNamedCrs().hashCode());
    assertEquals(model.toString(), new GeoJsonNamedCrs().toString());
  }

  /**
   * Json.
   *
   * @throws Exception the exception
   */
  @Test
  void json() throws Exception {
    ObjectMapper om = new ObjectMapper();
    GeoJsonNamedCrs model = new GeoJsonNamedCrs(GeoJsonCrsConstants.MERCATOR_CRS_ALT);
    String jsonStr = om.writerWithDefaultPrettyPrinter().writeValueAsString(model);
    System.out.println(jsonStr);
    assertNotNull(jsonStr);
    assertTrue(jsonStr.contains(GeoJsonCrsConstants.MERCATOR_CRS_ALT));
    GeoJsonNamedCrs readModel = om.readValue(jsonStr, GeoJsonNamedCrs.class);
    assertEquals(model, readModel);
  }
}
/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
import org.junit.jupiter.api.Test;

/**
 * The GeoJSON linked crs test.
 *
 * @author Christian Bremer
 */
class GeoJsonLinkedCrsTest {

  /**
   * Gets href.
   */
  @Test
  void getHref() {
    GeoJsonLinkedCrs model = new GeoJsonLinkedCrs();
    assertEquals(model, model);
    assertEquals(model, new GeoJsonLinkedCrs());
    assertEquals(model, new GeoJsonLinkedCrs(null));
    assertEquals(model.hashCode(), new GeoJsonLinkedCrs().hashCode());
    assertEquals(model.toString(), new GeoJsonLinkedCrs().toString());

    assertNotEquals(model, null);
    assertNotEquals(model, new Object());

    model.setHref(null);
    assertNull(model.getHref());

    model.setHref("https://example.org");
    assertEquals("https://example.org", model.getHref());
    assertEquals(model.hashCode(), new GeoJsonLinkedCrs("https://example.org").hashCode());
    assertEquals(model.toString(), new GeoJsonLinkedCrs("https://example.org").toString());
  }

  /**
   * Gets type.
   */
  @Test
  void getType() {
    GeoJsonLinkedCrs model = new GeoJsonLinkedCrs();
    model.setType(null);
    assertNull(model.getType());

    model.setType(GeoJsonLinkedCrs.TYPE_ESRI_WKT);
    assertEquals(GeoJsonLinkedCrs.TYPE_ESRI_WKT, model.getType());

    assertEquals(model, new GeoJsonLinkedCrs(null, GeoJsonLinkedCrs.TYPE_ESRI_WKT));
    model.setHref("https://example.org/ogc");
    model.setType(GeoJsonLinkedCrs.TYPE_OGC_WKT);
    assertEquals(
        model,
        new GeoJsonLinkedCrs("https://example.org/ogc", GeoJsonLinkedCrs.TYPE_OGC_WKT));
  }

  /**
   * Json.
   *
   * @throws Exception the exception
   */
  @Test
  void json() throws Exception {
    ObjectMapper om = new ObjectMapper();
    GeoJsonLinkedCrs model = new GeoJsonLinkedCrs(
        "https://example.org",
        GeoJsonLinkedCrs.TYPE_PROJ4);
    String jsonStr = om.writerWithDefaultPrettyPrinter().writeValueAsString(model);
    System.out.println(jsonStr);
    assertNotNull(jsonStr);
    assertTrue(jsonStr.contains(GeoJsonLinkedCrs.TYPE_PROJ4));
    assertTrue(jsonStr.contains("https://example.org"));
    GeoJsonLinkedCrs readModel = om.readValue(jsonStr, GeoJsonLinkedCrs.class);
    assertEquals(model, readModel);
  }
}
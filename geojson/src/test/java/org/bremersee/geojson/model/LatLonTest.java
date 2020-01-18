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

package org.bremersee.geojson.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * The latitude longitude test.
 *
 * @author Christian Bremer
 */
class LatLonTest {

  /**
   * Gets latitude longitude.
   */
  @Test
  void getLatLon() {
    LatLon model = new LatLon(
        new BigDecimal("1.2345"),
        new BigDecimal("5.6789"));
    assertEquals(new BigDecimal("1.2345"), model.getLatitude());
    assertEquals(new BigDecimal("5.6789"), model.getLongitude());
    assertNotEquals(model, null);
    assertNotEquals(model, new Object());
    assertEquals(model, model);
    assertEquals(
        model,
        new LatLon(LatLonAware.builder()
            .latitude(new BigDecimal("1.2345"))
            .longitude(new BigDecimal("5.6789"))
            .build()));
    assertEquals(
        model,
        new LatLon(LatLonAware.builder()
            .latitude(new BigDecimal("1.2345"))
            .longitude(new BigDecimal("5.6789"))
            .build()
            .toCoordinate()));
    assertEquals(
        model,
        new LatLon(LatLonAware.builder()
            .latitude(new BigDecimal("1.2345"))
            .longitude(new BigDecimal("5.6789"))
            .build()
            .toPoint()));
    assertTrue(model.toString().contains("1.2345"));
    assertTrue(model.toString().contains("5.6789"));
  }

  /**
   * Json.
   *
   * @throws IOException the io exception
   */
  @Test
  void json() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    LatLon model = new LatLon(
        new BigDecimal("1.2345"),
        new BigDecimal("5.6789"));
    String json = objectMapper.writeValueAsString(model);
    System.out.println("JSON of lat lon = " + json);
    assertTrue(json.contains("1.2345"));
    assertTrue(json.contains("5.6789"));
    LatLon actual = objectMapper.readValue(json, LatLon.class);
    System.out.println("Read lat lon = " + actual);
    assertEquals(model, actual);
  }
}
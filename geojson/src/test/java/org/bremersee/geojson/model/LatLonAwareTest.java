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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * The lat lon aware test.
 *
 * @author Christian Bremer
 */
class LatLonAwareTest {

  /**
   * Builder.
   */
  @Test
  void builder() {
    LatLonAware model = LatLonAware.builder().build();
    assertFalse(model.hasValues());
    assertNull(model.toCoordinate());
    assertNull(model.toPoint());
    assertEquals("", model.toLatLonString());
    assertEquals("", model.toLonLatString());

    model = LatLonAware.builder()
        .longitude(1.)
        .build();
    assertFalse(model.hasValues());

    model = LatLonAware.builder()
        .latitude(1.)
        .build();
    assertFalse(model.hasValues());

    model = LatLonAware.builder()
        .from(null)
        .build();
    assertFalse(model.hasValues());

    model = LatLonAware.builder()
        .latitude(1.2345)
        .longitude(4.5678)
        .build();
    assertTrue(model.hasValues());
    assertEquals(1.2345, model.getLatitude().doubleValue(), 0.0001);
    assertEquals(4.5678, model.getLongitude().doubleValue(), 0.0001);
    assertEquals(4.5678, model.toCoordinate().x, 0.0001);
    assertEquals(1.2345, model.toCoordinate().y, 0.0001);
    assertEquals(4.5678, model.toPoint().getX(), 0.0001);
    assertEquals(1.2345, model.toPoint().getY(), 0.0001);
    assertEquals("1.2345,4.5678", model.toLatLonString());
    assertEquals("4.5678,1.2345", model.toLonLatString());

    model = LatLonAware.builder()
        .latitude(new BigDecimal("1.2345"))
        .longitude(new BigDecimal("4.5678"))
        .build();
    assertTrue(model.hasValues());
    assertEquals(new BigDecimal("1.2345"), model.getLatitude());
    assertEquals(new BigDecimal("4.5678"), model.getLongitude());
    assertEquals(4.5678, model.toCoordinate().x, 0.0001);
    assertEquals(1.2345, model.toCoordinate().y, 0.0001);
    assertEquals(4.5678, model.toPoint().getX(), 0.0001);
    assertEquals(1.2345, model.toPoint().getY(), 0.0001);
    assertEquals("1.2345,4.5678", model.toLatLonString());
    assertEquals("4.5678,1.2345", model.toLonLatString());

    model = LatLonAware.builder()
        .from(model)
        .build();
    assertTrue(model.hasValues());
    assertEquals(new BigDecimal("1.2345"), model.getLatitude());
    assertEquals(new BigDecimal("4.5678"), model.getLongitude());
    assertEquals(4.5678, model.toCoordinate().x, 0.0001);
    assertEquals(1.2345, model.toCoordinate().y, 0.0001);
    assertEquals(4.5678, model.toPoint().getX(), 0.0001);
    assertEquals(1.2345, model.toPoint().getY(), 0.0001);
    assertEquals("1.2345,4.5678", model.toLatLonString());
    assertEquals("4.5678,1.2345", model.toLonLatString());
  }

}
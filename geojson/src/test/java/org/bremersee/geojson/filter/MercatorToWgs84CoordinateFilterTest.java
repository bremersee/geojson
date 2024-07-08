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

package org.bremersee.geojson.filter;

import static org.bremersee.geojson.filter.FilterConstants.EARTH_RADIUS_METERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

/**
 * The mercator to WGS 84 coordinate filter test.
 *
 * @author Christian Bremer
 */
class MercatorToWgs84CoordinateFilterTest {

  /**
   * Filter.
   */
  @Test
  void filter() {
    MercatorToWgs84CoordinateFilter filter = new MercatorToWgs84CoordinateFilter();
    filter.setEarthRadiusInMeters(EARTH_RADIUS_METERS);
    assertEquals(EARTH_RADIUS_METERS, filter.getEarthRadiusInMeters());
    assertEquals(
        EARTH_RADIUS_METERS,
        new MercatorToWgs84CoordinateFilter(EARTH_RADIUS_METERS)
            .getEarthRadiusInMeters());

    assertEquals(filter, filter);
    assertEquals(filter, new MercatorToWgs84CoordinateFilter(EARTH_RADIUS_METERS));
    assertNotEquals(filter, null);
    assertNotEquals(filter, new Object());

    assertEquals(
        filter.hashCode(),
        new MercatorToWgs84CoordinateFilter(EARTH_RADIUS_METERS).hashCode());

    assertTrue(filter.toString().contains(String.valueOf(EARTH_RADIUS_METERS)));

    double x = 941317.6141479212;
    double y = 397333.37049736374;
    Coordinate coordinate = new Coordinate(x, y);
    filter.filter(coordinate);
    assertEquals(
        8.456,
        coordinate.getX(),
        0.001);
    assertEquals(
        3.567,
        coordinate.getY(),
        0.001);

    coordinate = new Coordinate(Double.NaN, Double.NaN);
    filter.filter(coordinate);
    assertEquals(Double.NaN, coordinate.getX());
    assertEquals(Double.NaN, coordinate.getY());
  }
}
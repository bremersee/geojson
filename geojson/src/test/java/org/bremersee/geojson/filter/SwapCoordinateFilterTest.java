/*
 * Copyright 2022 the original author or authors.
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

/**
 * The swap coordinate filter test.
 *
 * @author Christian Bremer
 */
class SwapCoordinateFilterTest {

  /**
   * Filter.
   */
  @Test
  void filter() {
    double x = 1.;
    double y = 2.;
    Coordinate coordinate = new Coordinate(x, y);
    new SwapCoordinateFilter().filter(coordinate);
    assertEquals(x, coordinate.getY());
    assertEquals(y, coordinate.getX());
  }
}
package org.bremersee.geojson.utils;

import static org.junit.jupiter.api.Assertions.*;

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
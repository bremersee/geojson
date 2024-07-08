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

import static java.util.Objects.hash;
import static java.util.Objects.isNull;
import static org.bremersee.geojson.filter.FilterConstants.EARTH_RADIUS_METERS;

import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.bremersee.geojson.crs.GeoJsonCrsConstants;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateFilter;

/**
 * A coordinate filter that transforms WGS84 coordinates into mercator coordinates.
 *
 * @author Christian Bremer
 */
@Setter
@Getter
public class Wgs84ToMercatorCoordinateFilter implements CoordinateFilter, Serializable {

  @Serial
  private static final long serialVersionUID = 2L;

  /**
   * The earth radius in meters.
   */
  private double earthRadiusInMeters = EARTH_RADIUS_METERS;

  /**
   * Default constructor.
   */
  public Wgs84ToMercatorCoordinateFilter() {
    super();
  }

  /**
   * Constructs a coordinate filter that uses the specified earth radius.
   *
   * @param earthRadiusInMeters the earth radius in meters
   */
  public Wgs84ToMercatorCoordinateFilter(double earthRadiusInMeters) {
    this.earthRadiusInMeters = earthRadiusInMeters;
  }

  @Override
  public void filter(Coordinate coord) {

    if (coord != null) {
      if (!Double.isNaN(coord.x)) {
        coord.x = coord.x * getEarthRadiusInMeters() * Math.PI / 180.;
      }
      if (!Double.isNaN(coord.y)) {
        if (coord.y > GeoJsonCrsConstants.MERCATOR_MAX_LAT) {
          coord.y = GeoJsonCrsConstants.MERCATOR_MAX_LAT;
        } else if (coord.y < GeoJsonCrsConstants.MERCATOR_MIN_LAT) {
          coord.y = GeoJsonCrsConstants.MERCATOR_MIN_LAT;
        }
        coord.y = Math.log(Math.tan(Math.PI / 4 + Math.toRadians(coord.y) / 2))
            * getEarthRadiusInMeters();
      }
    }
  }

  @Override
  public String toString() {
    return "Wgs84ToMercatorCoordinateFilter{"
        + "earthRadiusInMeters=" + earthRadiusInMeters
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (isNull(o) || getClass() != o.getClass()) {
      return false;
    }
    Wgs84ToMercatorCoordinateFilter that = (Wgs84ToMercatorCoordinateFilter) o;
    return Double.compare(that.earthRadiusInMeters, earthRadiusInMeters) == 0;
  }

  @Override
  public int hashCode() {
    return hash(earthRadiusInMeters);
  }
}

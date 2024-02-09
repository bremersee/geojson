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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.bremersee.geojson.filter.FilterConstants.EARTH_RADIUS_METERS;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateFilter;

/**
 * A coordinate filter that transforms mercator coordinates into WGS84 coordinates.
 *
 * @author Christian Bremer
 */
@Getter
@Setter
public class MercatorToWgs84CoordinateFilter implements CoordinateFilter {

  private double earthRadiusInMeters = EARTH_RADIUS_METERS;

  /**
   * Default constructor.
   */
  public MercatorToWgs84CoordinateFilter() {
    super();
  }

  /**
   * Constructs a coordinate filter that uses the specified earth radius.
   *
   * @param earthRadiusInMeters the earth radius in meters
   */
  public MercatorToWgs84CoordinateFilter(double earthRadiusInMeters) {
    this.earthRadiusInMeters = earthRadiusInMeters;
  }

  @Override
  public void filter(Coordinate coord) {

    if (nonNull(coord)) {
      if (!Double.isNaN(coord.x)) {
        coord.x = (coord.x * 180.) / (getEarthRadiusInMeters() * Math.PI);
      }
      if (!Double.isNaN(coord.y)) {
        coord.y = Math
            .toDegrees(2 * Math.atan(Math.exp(coord.y / getEarthRadiusInMeters())) - Math.PI / 2);
      }
    }
  }

  @Override
  public String toString() {
    return "MercatorToWgs84CoordinateFilter{"
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
    MercatorToWgs84CoordinateFilter that = (MercatorToWgs84CoordinateFilter) o;
    return Double.compare(that.earthRadiusInMeters, earthRadiusInMeters) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(earthRadiusInMeters);
  }
}

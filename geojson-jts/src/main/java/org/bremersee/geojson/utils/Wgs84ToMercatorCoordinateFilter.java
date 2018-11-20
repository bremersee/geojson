/*
 * Copyright 2015 the original author or authors.
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

package org.bremersee.geojson.utils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import java.io.Serializable;

/**
 * A coordinate filter that transforms WGS84 coordinates into mercator coordinates.
 *
 * @author Christian Bremer
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Wgs84ToMercatorCoordinateFilter implements CoordinateFilter, Serializable {

  private static final long serialVersionUID = 1L;

  private double earthRadiusInMeters = GeometryUtils.EARTH_RADIUS_METERS;

  private boolean removingZ = false;

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
  public Wgs84ToMercatorCoordinateFilter(final double earthRadiusInMeters) {
    this.earthRadiusInMeters = earthRadiusInMeters;
  }

  /**
   * Constructs a coordinate filter with the specified flag for removing the z value.
   *
   * @param removingZ if {@code true} the z value of the coordinate will be removed otherwise it
   *                  will be untouched
   */
  public Wgs84ToMercatorCoordinateFilter(final boolean removingZ) {
    this.removingZ = removingZ;
  }

  /**
   * Constructs a coordinate filter with the specified values.
   *
   * @param earthRadiusInMeters the earth radius in meters
   * @param removingZ           if {@code true} the z value of the coordinate will be removed
   *                            otherwise it will be untouched
   */
  public Wgs84ToMercatorCoordinateFilter(final double earthRadiusInMeters,
      final boolean removingZ) {
    this.earthRadiusInMeters = earthRadiusInMeters;
    this.removingZ = removingZ;
  }

  /**
   * Returns the earth radius that is used for the transformation.
   *
   * @return the earth radius in meters
   */
  public double getEarthRadiusInMeters() {
    return earthRadiusInMeters;
  }

  /**
   * Sets the earth radius that is used for the transformation.
   *
   * @param earthRadiusInMeters the earth radius in meters
   */
  public void setEarthRadiusInMeters(final double earthRadiusInMeters) {
    this.earthRadiusInMeters = earthRadiusInMeters;
  }

  /**
   * @return {@code true} if the z value of the coordinate will be removed otherwise {@code false}
   */
  public boolean isRemovingZ() {
    return removingZ;
  }

  /**
   * @param removingZ if {@code true} the z value of the coordinate will be removed otherwise it
   *                  will be untouched
   */
  public void setRemovingZ(final boolean removingZ) {
    this.removingZ = removingZ;
  }

  @Override
  public void filter(final Coordinate coord) {

    if (coord != null) {
      if (!Double.isNaN(coord.x)) {
        coord.x = coord.x * getEarthRadiusInMeters() * Math.PI / 180.;
      }
      if (!Double.isNaN(coord.y)) {
        if (coord.y > GeometryUtils.MERCATOR_MAX_LAT) {
          coord.y = GeometryUtils.MERCATOR_MAX_LAT;
        } else if (coord.y < GeometryUtils.MERCATOR_MIN_LAT) {
          coord.y = GeometryUtils.MERCATOR_MIN_LAT;
        }
        coord.y = Math.log(Math.tan(Math.PI / 4 + Math.toRadians(coord.y) / 2))
            * getEarthRadiusInMeters();
      }
      if (removingZ) {
        coord.z = Double.NaN;
      }
    }
  }

}
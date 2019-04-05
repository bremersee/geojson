/*
 * Copyright 2018 the original author or authors.
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

import java.math.BigDecimal;
import org.bremersee.geojson.utils.GeometryUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

/**
 * The interface lat lon aware.
 *
 * @author Christian Bremer
 */
@SuppressWarnings("unused")
public interface LatLonAware {

  /**
   * Gets latitude.
   *
   * @return the latitude
   */
  BigDecimal getLatitude();

  /**
   * Gets longitude.
   *
   * @return the longitude
   */
  BigDecimal getLongitude();

  /**
   * Determines whether latitude and longitude have values.
   *
   * @return {@code true} if latitude and longitude is not {@code null}, otherwise {@code false}
   */
  default boolean hasValues() {
    return getLatitude() != null && getLongitude() != null;
  }

  /**
   * To GeoJSON point (x is longitude, y is latitude).
   *
   * @return the point
   */
  default Point toPoint() {
    if (hasValues()) {
      return GeometryUtils.createPoint(getLongitude(), getLatitude());
    }
    return null;
  }

  /**
   * To GeoJSON coordinate (x is longitude, y is latitude).
   *
   * @return the coordinate
   */
  default Coordinate toCoordinate() {
    return GeometryUtils.createCoordinate(getLongitude(), getLatitude());
  }

  /**
   * To comma separated latitude longitude string.
   *
   * @return the string
   */
  default String toLatLonString() {
    if (hasValues()) {
      return getLatitude().toPlainString() + "," + getLongitude().toPlainString();
    }
    return "";
  }

  /**
   * To comma separated longitude latitude string.
   *
   * @return the string
   */
  default String toLonLatString() {
    if (hasValues()) {
      return getLongitude().toPlainString() + "," + getLatitude().toPlainString();
    }
    return "";
  }

  /**
   * Builder builder.
   *
   * @return the builder
   */
  static Builder builder() {
    return new BuilderImpl();
  }

  /**
   * The interface Builder.
   */
  @SuppressWarnings("unused")
  interface Builder {

    /**
     * Latitude builder.
     *
     * @param latitude the latitude
     * @return the builder
     */
    Builder latitude(BigDecimal latitude);

    /**
     * Latitude builder.
     *
     * @param latitude the latitude
     * @return the builder
     */
    Builder latitude(double latitude);

    /**
     * Longitude builder.
     *
     * @param longitude the longitude
     * @return the builder
     */
    Builder longitude(BigDecimal longitude);

    /**
     * Longitude builder.
     *
     * @param longitude the longitude
     * @return the builder
     */
    Builder longitude(double longitude);

    /**
     * Build lat lon aware.
     *
     * @return the lat lon aware
     */
    LatLonAware build();
  }

  /**
   * The builder implementation.
   */
  class BuilderImpl implements Builder {

    private BigDecimal latitude;

    private BigDecimal longitude;

    @Override
    public Builder latitude(BigDecimal latitude) {
      this.latitude = latitude;
      return this;
    }

    @Override
    public Builder latitude(double latitude) {
      this.latitude = BigDecimal.valueOf(latitude);
      return this;
    }

    @Override
    public Builder longitude(BigDecimal longitude) {
      this.longitude = longitude;
      return this;
    }

    @Override
    public Builder longitude(double longitude) {
      this.longitude = BigDecimal.valueOf(longitude);
      return this;
    }

    @Override
    public LatLonAware build() {
      return new LatLon(latitude, longitude);
    }
  }

}

/*
 * Copyright 2018-2022 the original author or authors.
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

/**
 * The interface lat lon aware.
 *
 * @author Christian Bremer
 */
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
   * To comma separated latitude longitude string.
   *
   * @return the string
   */
  default String toLatLonString() {
    return hasValues()
        ? getLatitude().toPlainString() + "," + getLongitude().toPlainString()
        : "";
  }

  /**
   * To comma separated longitude latitude string.
   *
   * @return the string
   */
  default String toLonLatString() {
    return hasValues()
        ? getLongitude().toPlainString() + "," + getLatitude().toPlainString()
        : "";
  }

  /**
   * Returns a new builder.
   *
   * @return the builder
   */
  static Builder builder() {
    return new BuilderImpl();
  }

  /**
   * The builder interface.
   */
  interface Builder {

    /**
     * Sets latitude and longitude from the given object.
     *
     * @param latLonAware the latitude and longitude aware object
     * @return the builder
     */
    Builder from(LatLonAware latLonAware);

    /**
     * Sets latitude.
     *
     * @param latitude the latitude
     * @return the builder
     */
    Builder latitude(BigDecimal latitude);

    /**
     * Sets latitude.
     *
     * @param latitude the latitude
     * @return the builder
     */
    Builder latitude(double latitude);

    /**
     * Sets longitude.
     *
     * @param longitude the longitude
     * @return the builder
     */
    Builder longitude(BigDecimal longitude);

    /**
     * Sets longitude.
     *
     * @param longitude the longitude
     * @return the builder
     */
    Builder longitude(double longitude);

    /**
     * Build latitude and longitude aware.
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
    public Builder from(LatLonAware latLonAware) {
      if (latLonAware != null) {
        latitude = latLonAware.getLatitude();
        longitude = latLonAware.getLongitude();
      }
      return this;
    }

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

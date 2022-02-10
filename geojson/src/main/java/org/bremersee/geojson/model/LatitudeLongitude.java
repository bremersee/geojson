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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

/**
 * The latitude longitude.
 *
 * @author Christian Bremer
 */
@SuppressWarnings("SameNameButDifferent")
@Schema(description = "WGS84 position with latitude and longitude.")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
@ToString
public class LatitudeLongitude implements LatLonAware, Serializable {

  private static final long serialVersionUID = 1L;

  @Schema(description = "The latitude.")
  private BigDecimal latitude;

  @Schema(description = "The longitude.")
  private BigDecimal longitude;

  /**
   * Instantiates a new latitude longitude.
   *
   * @param latitude the latitude
   * @param longitude the longitude
   */
  @JsonCreator
  public LatitudeLongitude(
      @JsonProperty("latitude") BigDecimal latitude,
      @JsonProperty("longitude") BigDecimal longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * Instantiates a new latitude longitude.
   *
   * @param latLonAware the lat lon aware
   */
  public LatitudeLongitude(LatLonAware latLonAware) {
    if (latLonAware != null) {
      this.latitude = latLonAware.getLatitude();
      this.longitude = latLonAware.getLongitude();
    }
  }

  /**
   * Instantiates a new latitude longitude (longitude is x, latitude is y).
   *
   * @param point the point
   */
  public LatitudeLongitude(Point point) {
    if (point != null) {
      this.latitude = BigDecimal.valueOf(point.getY());
      this.longitude = BigDecimal.valueOf(point.getX());
    }
  }

  /**
   * Instantiates a new latitude longitude (longitude is x, latitude is y).
   *
   * @param coordinate the coordinate
   */
  public LatitudeLongitude(Coordinate coordinate) {
    if (coordinate != null) {
      this.latitude = BigDecimal.valueOf(coordinate.getY());
      this.longitude = BigDecimal.valueOf(coordinate.getX());
    }
  }

  @JsonProperty("latitude")
  @Override
  public BigDecimal getLatitude() {
    return latitude;
  }

  @JsonProperty("longitude")
  @Override
  public BigDecimal getLongitude() {
    return longitude;
  }
}

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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The lat lon.
 *
 * @author Christian Bremer
 */
@Schema(description = "WGS84 position with lat and lon.")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
@ToString
public class LatLon implements LatLonAware, Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * The latitude.
   */
  @Schema(description = "The latitude.")
  private BigDecimal lat;

  /**
   * The longitude.
   */
  @Schema(description = "The longitude.")
  private BigDecimal lon;

  /**
   * Instantiates a new lat lon.
   *
   * @param lat the latitude
   * @param lon the longitude
   */
  @JsonCreator
  public LatLon(
      @JsonProperty("lat") BigDecimal lat,
      @JsonProperty("lon") BigDecimal lon) {
    this.lat = lat;
    this.lon = lon;
  }

  /**
   * Instantiates a new lat lon.
   *
   * @param latLonAware the lat lon aware
   */
  public LatLon(LatLonAware latLonAware) {
    if (latLonAware != null) {
      this.lat = latLonAware.getLatitude();
      this.lon = latLonAware.getLongitude();
    }
  }

  @JsonProperty("lat")
  @Override
  public BigDecimal getLatitude() {
    return lat;
  }

  @JsonProperty("lon")
  @Override
  public BigDecimal getLongitude() {
    return lon;
  }

}

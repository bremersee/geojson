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
import lombok.Getter;
import lombok.ToString;

/**
 * The latitude longitude.
 *
 * @author Christian Bremer
 */
@Schema(description = "WGS84 position with latitude and longitude.")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class LatitudeLongitude implements LatLonAware, Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * The latitude.
   */
  @Schema(description = "The latitude.")
  @JsonProperty("latitude")
  private BigDecimal latitude;

  /**
   * The longitude.
   */
  @Schema(description = "The longitude.")
  @JsonProperty("longitude")
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

}

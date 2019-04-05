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
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bremersee.plain.model.UnknownAware;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

/**
 * The latitude longitude.
 *
 * @author Christian Bremer
 */
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class LatitudeLongitude extends UnknownAware implements LatLonAware {

  private BigDecimal latitude;

  private BigDecimal longitude;

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

}

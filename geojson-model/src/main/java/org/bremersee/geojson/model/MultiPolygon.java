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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GeoJSON MultiPolygon.
 *
 * @author Christian Bremer
 */
@SuppressWarnings("SameNameButDifferent")
@Schema(description = "GeoJSON MultiPolygon.")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MultiPolygon extends Geometry implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty("coordinates")
  private List<List<List<Position>>> coordinates = null;

  /**
   * Instantiates a new multi polygon.
   */
  public MultiPolygon() {
    setType(TypeEnum.MULTIPOLYGON);
  }

  /**
   * Instantiates a new multi polygon.
   *
   * @param bbox the bbox
   * @param coordinates the coordinates
   */
  @Builder(toBuilder = true)
  public MultiPolygon(BoundingBox bbox, List<List<List<Position>>> coordinates) {
    super(bbox);
    setType(TypeEnum.MULTIPOLYGON);
    this.coordinates = coordinates;
  }

  /**
   * Get coordinates.
   *
   * @return coordinates coordinates
   */
  @Schema(description = "The coordinates.")
  public List<List<List<Position>>> getCoordinates() {
    return coordinates;
  }

  /**
   * Sets coordinates.
   *
   * @param coordinates the coordinates
   */
  public void setCoordinates(List<List<List<Position>>> coordinates) {
    this.coordinates = coordinates;
  }

}


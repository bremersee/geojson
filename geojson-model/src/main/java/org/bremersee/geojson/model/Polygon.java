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

import static org.bremersee.geojson.GeoJsonConstants.COORDINATES;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * GeoJSON Polygon.
 *
 * @author Christian Bremer
 */
@Schema(description = "GeoJSON Polygon.")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Polygon extends Geometry implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * The coordinates.
   */
  @Schema(description = "The coordinates.")
  @JsonProperty(COORDINATES)
  private List<List<Position>> coordinates = null;

  /**
   * Instantiates a new polygon.
   */
  public Polygon() {
    setType(TypeEnum.POLYGON);
  }

  /**
   * Instantiates a new polygon.
   *
   * @param bbox the bbox
   * @param coordinates the coordinates
   */
  @Builder(toBuilder = true)
  public Polygon(BoundingBox bbox, List<List<Position>> coordinates) {
    super(bbox);
    setType(TypeEnum.POLYGON);
    this.coordinates = coordinates;
  }

  @Schema(hidden = true)
  @JsonIgnore
  @Override
  Object getGeometryJsonValue() {
    return getCoordinates();
  }

}


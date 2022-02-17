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

import static org.bremersee.geojson.GeoJsonConstants.GEOMETRIES;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GeoJSON GeometryCollection.
 *
 * @author Christian Bremer
 */
@SuppressWarnings("SameNameButDifferent")
@Schema(description = "GeoJSON GeometryCollection.")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GeometryCollection extends Geometry implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty(GEOMETRIES)
  private List<Geometry> geometries;

  /**
   * Instantiates a new geometry collection.
   */
  public GeometryCollection() {
    setType(TypeEnum.GEOMETRYCOLLECTION);
  }

  /**
   * Instantiates a new geometry collection.
   *
   * @param bbox the bbox
   * @param geometries the geometries
   */
  @Builder(toBuilder = true)
  public GeometryCollection(BoundingBox bbox, List<Geometry> geometries) {
    super(bbox);
    setType(TypeEnum.GEOMETRYCOLLECTION);
    this.geometries = geometries;
  }

  /**
   * Get geometries.
   *
   * @return geometries geometries
   */
  @Schema(description = "The geometries.")
  public List<Geometry> getGeometries() {
    return geometries;
  }

  /**
   * Sets geometries.
   *
   * @param geometries the geometries
   */
  public void setGeometries(List<Geometry> geometries) {
    this.geometries = geometries;
  }

  @Schema(hidden = true)
  @JsonIgnore
  @Override
  Object getGeometryJsonValue() {
    return Optional.ofNullable(getGeometries())
        .stream()
        .flatMap(Collection::stream)
        .map(Geometry::toJson)
        .collect(Collectors.toList());
  }

}


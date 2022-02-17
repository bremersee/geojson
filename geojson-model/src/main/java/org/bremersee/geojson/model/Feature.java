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

import static org.bremersee.geojson.GeoJsonConstants.BBOX;
import static org.bremersee.geojson.GeoJsonConstants.FEATURE;
import static org.bremersee.geojson.GeoJsonConstants.GEOMETRY;
import static org.bremersee.geojson.GeoJsonConstants.ID;
import static org.bremersee.geojson.GeoJsonConstants.PROPERTIES;
import static org.bremersee.geojson.GeoJsonConstants.TYPE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * A Feature object represents a spatially bounded thing.
 *
 * @author Christian Bremer
 */
@SuppressWarnings("SameNameButDifferent")
@Schema(description = "A Feature object represents a spatially bounded thing.")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Valid
public class Feature implements Serializable {

  private static final long serialVersionUID = 2L;

  @JsonIgnore
  private String type = FEATURE;

  @JsonProperty(ID)
  private String id = null;

  @JsonProperty(BBOX)
  private BoundingBox bbox = null;

  @JsonProperty(GEOMETRY)
  private Geometry geometry = null;

  @JsonProperty(PROPERTIES)
  private Object properties = null;

  /**
   * Instantiates a new feature.
   *
   * @param id the id
   * @param bbox the bbox
   * @param geometry the geometry
   * @param properties the properties
   */
  @Builder(toBuilder = true)
  public Feature(
      String id,
      BoundingBox bbox,
      Geometry geometry,
      Object properties) {

    setId(id);
    setBbox(bbox);
    setGeometry(geometry);
    setProperties(properties);
  }

  /**
   * The feature type.
   *
   * @return type type
   */
  @Schema(description = "The feature type, must be 'Feature'.", required = true)
  @JsonProperty(value = TYPE, required = true)
  @NotNull
  public String getType() {
    return type;
  }

  /**
   * Sets type.
   *
   * @param type the type
   */
  @JsonProperty(value = TYPE, required = true)
  public void setType(String type) {
    if (!FEATURE.equals(type)) {
      throw new IllegalArgumentException("Type must be 'Feature'.");
    }
    this.type = type;
  }

  /**
   * The identifier of this feature.
   *
   * @return id id
   */
  @Schema(description = "The identifier of this feature.")
  public String getId() {
    return id;
  }

  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Get bounding box.
   *
   * @return bbox bbox
   */
  @Schema(description = "The bounding box.")
  public BoundingBox getBbox() {
    return bbox;
  }

  /**
   * Sets bounding box.
   *
   * @param bbox the bbox
   */
  public void setBbox(BoundingBox bbox) {
    this.bbox = bbox;
  }

  /**
   * Get geometry.
   *
   * @return geometry geometry
   */
  @Schema(description = "The geometry.")
  public Geometry getGeometry() {
    return geometry;
  }

  /**
   * Sets geometry.
   *
   * @param geometry the geometry
   */
  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  /**
   * The feature properties.
   *
   * @return properties properties
   */
  @Schema(description = "The feature properties.")
  public Object getProperties() {
    return properties;
  }

  /**
   * Sets properties.
   *
   * @param properties the properties
   */
  public void setProperties(Object properties) {
    this.properties = properties;
  }

}


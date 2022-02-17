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

import static org.bremersee.geojson.GeoJsonConstants.FEATURE_COLLECTION;
import static org.bremersee.geojson.GeoJsonConstants.TYPE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * A collection of features.
 *
 * @author Christian Bremer
 */
@SuppressWarnings("SameNameButDifferent")
@Schema(description = "A collection of features.")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Valid
public class FeatureCollection implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonIgnore
  private String type = FEATURE_COLLECTION;

  @JsonProperty("bbox")
  private BoundingBox bbox = null;

  @JsonProperty("features")
  private List<Feature> features = null;

  /**
   * Instantiates a new Feature collection.
   *
   * @param bbox the bbox
   * @param features the features
   */
  @Builder(toBuilder = true)
  public FeatureCollection(
      BoundingBox bbox,
      List<Feature> features) {

    setBbox(bbox);
    setFeatures(features);
  }

  /**
   * The feature collection type.
   *
   * @return type type
   */
  @Schema(
      description = "The feature collection type, must be 'FeatureCollection'.",
      required = true)
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
    if (!FEATURE_COLLECTION.equals(type)) {
      throw new IllegalArgumentException("Type must be 'FeatureCollection'.");
    }
    this.type = type;
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
   * Get features.
   *
   * @return features features
   */
  @Schema(description = "The features.")
  public List<Feature> getFeatures() {
    return features;
  }

  /**
   * Sets features.
   *
   * @param features the features
   */
  public void setFeatures(List<Feature> features) {
    this.features = features;
  }

}


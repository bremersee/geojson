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

import static org.bremersee.geojson.GeoJsonConstants.TYPE;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.geojson.GeoJsonConstants;

/**
 * A collection of features.
 *
 * @author Christian Bremer
 */
@Schema(description = "A collection of features.")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class FeatureCollection implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * The geo json type.
   */
  @Schema(
      description = "The feature collection type, must be 'FeatureCollection'.",
      requiredMode = RequiredMode.REQUIRED)
  @JsonProperty(value = TYPE, required = true)
  private TypeEnum type = TypeEnum.FEATURE_COLLECTION;

  /**
   * The bounding box.
   */
  @Setter
  @Schema(description = "The bounding box.")
  @JsonProperty("bbox")
  private BoundingBox bbox = null;

  /**
   * The features.
   */
  @Setter
  @Schema(description = "The features.")
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
   * The enum Type enum.
   */
  public enum TypeEnum {
    /**
     * Feature collection type enum.
     */
    FEATURE_COLLECTION(GeoJsonConstants.FEATURE_COLLECTION);

    private final String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return value;
    }

    /**
     * From value type enum.
     *
     * @param value the value
     * @return the type enum
     */
    @JsonCreator
    public static TypeEnum fromValue(String value) {
      if (GeoJsonConstants.FEATURE_COLLECTION.equals(value)) {
        return FEATURE_COLLECTION;
      }
      throw new IllegalArgumentException(String
          .format("Value '%s' must be '%s'.", value, GeoJsonConstants.FEATURE_COLLECTION));
    }

  }

}


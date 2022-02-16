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
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * GeoJSON Geometry.
 *
 * @author Christian Bremer
 */
@SuppressWarnings("SameNameButDifferent")
@Schema(description = "GeoJSON Geometry.")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = GeometryCollection.class, name = "GeometryCollection"),
    @JsonSubTypes.Type(value = MultiPoint.class, name = "MultiPoint"),
    @JsonSubTypes.Type(value = MultiLineString.class, name = "MultiLineString"),
    @JsonSubTypes.Type(value = LineString.class, name = "LineString"),
    @JsonSubTypes.Type(value = MultiPolygon.class, name = "MultiPolygon"),
    @JsonSubTypes.Type(value = Point.class, name = "Point"),
    @JsonSubTypes.Type(value = Polygon.class, name = "Polygon"),
})
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public abstract class Geometry implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty("type")
  TypeEnum type = null;

  @JsonProperty("bbox")
  BoundingBox bbox = null;

  /**
   * Instantiates a new geometry.
   *
   * @param bbox the bbox
   */
  public Geometry(BoundingBox bbox) {
    this.bbox = bbox;
  }

  /**
   * The geometry type.
   *
   * @return type type
   */
  @Schema(description = "The geometry type.", required = true)
  @NotNull
  public TypeEnum getType() {
    return type;
  }

  /**
   * Sets type.
   *
   * @param type the type
   */
  public void setType(TypeEnum type) {
    this.type = type;
  }

  /**
   * Get bounding box.
   *
   * @return bbox bbox
   */
  @Schema(description = "The boundling box.")
  public BoundingBox getBbox() {
    return bbox;
  }

  /**
   * Sets bbox.
   *
   * @param bbox the bbox
   */
  public void setBbox(BoundingBox bbox) {
    this.bbox = bbox;
  }

  /**
   * The geometry type.
   */
  public enum TypeEnum {

    /**
     * Point type enum.
     */
    POINT("Point"),

    /**
     * Multipoint type enum.
     */
    MULTIPOINT("MultiPoint"),

    /**
     * Linestring type enum.
     */
    LINESTRING("LineString"),

    /**
     * Multilinestring type enum.
     */
    MULTILINESTRING("MultiLineString"),

    /**
     * Polygon type enum.
     */
    POLYGON("Polygon"),

    /**
     * Multipolygon type enum.
     */
    MULTIPOLYGON("MultiPolygon"),

    /**
     * Geometrycollection type enum.
     */
    GEOMETRYCOLLECTION("GeometryCollection");

    private final String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    /**
     * From value type enum.
     *
     * @param text the text
     * @return the type enum
     */
    @JsonCreator
    public static TypeEnum fromValue(String text) {
      for (TypeEnum b : TypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
}


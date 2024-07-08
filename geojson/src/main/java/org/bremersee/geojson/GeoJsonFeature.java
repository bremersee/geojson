/*
 * Copyright 2015-2022 the original author or authors.
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

package org.bremersee.geojson;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.bremersee.geojson.GeoJsonConstants.BBOX;
import static org.bremersee.geojson.GeoJsonConstants.FEATURE;
import static org.bremersee.geojson.GeoJsonConstants.GEOMETRY;
import static org.bremersee.geojson.GeoJsonConstants.ID;
import static org.bremersee.geojson.GeoJsonConstants.PROPERTIES;
import static org.bremersee.geojson.GeoJsonConstants.TYPE;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Objects;
import org.bremersee.geojson.converter.deserialization.JacksonGeometryDeserializer;
import org.bremersee.geojson.converter.serialization.JacksonGeometrySerializer;
import org.bremersee.geojson.model.UnknownAware;
import org.locationtech.jts.geom.Geometry;

/**
 * A GeoJSON object with the type {@code Feature} (see
 * <a href="https://tools.ietf.org/html/rfc7946#section-3.2">rfc7946 section 3.2</a>).
 *
 * @param <G> the geometry type parameter
 * @param <P> the properties type parameter
 * @author Christian Bremer
 */
@Schema(description = "A GeoJSON object with type 'Feature'.")
@JsonPropertyOrder({"type", "id", "bbox", "geometry", "properties"})
public class GeoJsonFeature<G extends Geometry, P> extends UnknownAware {

  @Schema(description = "The id of the GeoJSON feature.")
  @JsonInclude(Include.NON_NULL)
  @JsonProperty(ID)
  private final String id;

  @Schema(description = "The bounding box of the GeoJSON feature.")
  @JsonInclude(Include.NON_EMPTY)
  @JsonProperty(BBOX)
  private final double[] bbox;

  @SuppressWarnings("DefaultAnnotationParam")
  @Schema(description = "GeoJSON", implementation = org.bremersee.geojson.model.Geometry.class)
  @JsonSerialize(using = JacksonGeometrySerializer.class)
  @JsonInclude(Include.ALWAYS)
  @JsonProperty(GEOMETRY)
  private final G geometry;

  @JsonIgnore
  private final P properties;

  /**
   * Instantiates a new geo json feature.
   *
   * @param type the type
   * @param id the id
   * @param bbox the bbox
   * @param geometry the geometry
   * @param properties the properties
   */
  @JsonCreator
  GeoJsonFeature(
      @JsonProperty(value = TYPE, required = true) String type,
      @JsonProperty(ID) String id,
      @JsonProperty(BBOX) double[] bbox,
      @JsonProperty(GEOMETRY)
      @JsonDeserialize(using = JacksonGeometryDeserializer.class)
      G geometry,
      @JsonProperty(PROPERTIES) P properties) {

    if (!FEATURE.equals(type)) {
      throw new IllegalArgumentException(String.format("Type must be '%s'.", FEATURE));
    }

    this.id = id;
    if (isNull(bbox) || (bbox.length == 4) || (bbox.length == 6)) {
      this.bbox = bbox;
    } else {
      throw new IllegalArgumentException(
          "Bounding box must be null or must have a length of four or six.");
    }
    this.geometry = geometry;
    this.properties = properties;
  }

  /**
   * Instantiates a new geo json feature.
   *
   * @param id the id
   * @param bbox the bbox
   * @param geometry the geometry
   * @param properties the properties
   */
  public GeoJsonFeature(
      String id,
      double[] bbox,
      G geometry,
      P properties) {
    this(FEATURE, id, bbox, geometry, properties);
  }

  /**
   * Instantiates a new geo json feature.
   *
   * @param id an optional id
   * @param geometry the geometry of the GeoJson feature
   * @param calculateBounds if {@code true} the bounding box will be calculated otherwise the
   *     bounding box will be {@code null}
   * @param properties a map with named objects that are associated with the GeoJSON feature
   */
  public GeoJsonFeature(
      String id,
      G geometry,
      boolean calculateBounds,
      P properties) {

    this(
        FEATURE,
        id,
        calculateBounds ? GeoJsonGeometryFactory.getBoundingBox(geometry) : null,
        geometry,
        properties);
  }

  /**
   * Gets type.
   *
   * @return the type
   */
  @Schema(description = "The feature type.", requiredMode = REQUIRED, example = FEATURE)
  @JsonProperty(value = TYPE, required = true)
  public final String getType() {
    return FEATURE;
  }

  /**
   * Return the id of this GeoJSON feature or {@code null} if there is no id available.
   *
   * @return the id of this GeoJSON feature
   */
  @JsonIgnore
  public String getId() {
    return id;
  }

  /**
   * Return the bounding box of the GeoJSON object or {@code null} if there is no such object (see
   * <a href="https://tools.ietf.org/html/rfc7946#section-5">Bounding Box</a>).
   *
   * @return the bounding box
   */
  @JsonIgnore
  public double[] getBbox() {
    return bbox;
  }

  /**
   * Return the geometry object of this GeoJSON feature.
   *
   * @return the geometry
   */
  @JsonIgnore
  public G getGeometry() {
    return geometry;
  }

  /**
   * Gets properties.
   *
   * @return the properties
   */
  @Schema(description = "The properties of the GeoJSON feature.")
  @JsonInclude(Include.NON_EMPTY)
  @JsonProperty(PROPERTIES)
  public P getProperties() {
    return properties;
  }

  @Override
  public String toString() {
    return "GeoJsonFeature {"
        + "id='" + getId() + '\''
        + ", bbox=" + Arrays.toString(getBbox())
        + ", geometry=" + (nonNull(getGeometry()) ? getGeometry().toText() : "null")
        + ", properties=" + getProperties()
        + ", unknown=" + unknown()
        + '}';
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(getId(), getProperties());
    result = 31 * result + Arrays.hashCode(getBbox());
    if (nonNull(getGeometry())) {
      result = 31 * result + getGeometry().toText().hashCode();
    }
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof GeoJsonFeature<?, ?> that)) {
      return false;
    }
    return Objects.equals(getId(), that.getId())
        && GeoJsonGeometryFactory.equals(getGeometry(), that.getGeometry())
        && Arrays.equals(getBbox(), that.getBbox())
        && Objects.equals(getProperties(), that.getProperties());
  }

}

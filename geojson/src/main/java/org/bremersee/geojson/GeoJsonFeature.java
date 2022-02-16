/*
 * Copyright 2015-2020 the original author or authors.
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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.bremersee.geojson.GeoJsonConstants.JSON_FEATURE_BOUNDING_BOX_ATTRIBUTE_NAME;
import static org.bremersee.geojson.GeoJsonConstants.JSON_FEATURE_GEOMETRY_ATTRIBUTE_NAME;
import static org.bremersee.geojson.GeoJsonConstants.JSON_FEATURE_ID_ATTRIBUTE_NAME;
import static org.bremersee.geojson.GeoJsonConstants.JSON_FEATURE_PROPERTIES_ATTRIBUTE_NAME;
import static org.bremersee.geojson.GeoJsonConstants.JSON_TYPE_ATTRIBUTE;
import static org.bremersee.geojson.GeoJsonConstants.JSON_TYPE_FEATURE;

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
import org.springframework.util.Assert;

/**
 * A GeoJSON object with the type {@code Feature} (see
 * <a href="https://tools.ietf.org/html/rfc7946#section-3.2">rfc7946 section 3.2</a>).
 *
 * @author Christian Bremer
 */
@Schema(description = "A GeoJSON object with type 'Feature'.")
@JsonPropertyOrder({"type", "id", "bbox", "geometry", "properties"})
public class GeoJsonFeature<G extends Geometry, P> extends UnknownAware {

  @Schema(description = "The id of the GeoJSON feature.")
  @JsonInclude(Include.NON_NULL)
  @JsonProperty(JSON_FEATURE_ID_ATTRIBUTE_NAME)
  private final String id;

  @Schema(description = "The bounding box of the GeoJSON feature.")
  @JsonInclude(Include.NON_EMPTY)
  @JsonProperty(JSON_FEATURE_BOUNDING_BOX_ATTRIBUTE_NAME)
  private final double[] bbox;

  @SuppressWarnings("DefaultAnnotationParam")
  @Schema(description = "GeoJSON", implementation = org.bremersee.geojson.model.Geometry.class)
  @JsonSerialize(using = JacksonGeometrySerializer.class)
  @JsonInclude(Include.ALWAYS)
  @JsonProperty(JSON_FEATURE_GEOMETRY_ATTRIBUTE_NAME)
  private final G geometry;

  @JsonIgnore
  private final P properties;

  /**
   * Default constructor.
   */
  @JsonCreator
  GeoJsonFeature(
      @JsonProperty(value = JSON_TYPE_ATTRIBUTE, required = true) String type,
      @JsonProperty(JSON_FEATURE_ID_ATTRIBUTE_NAME) String id,
      @JsonProperty(JSON_FEATURE_BOUNDING_BOX_ATTRIBUTE_NAME) double[] bbox,
      @JsonProperty(JSON_FEATURE_GEOMETRY_ATTRIBUTE_NAME)
      @JsonDeserialize(using = JacksonGeometryDeserializer.class)
          G geometry,
      @JsonProperty(JSON_FEATURE_PROPERTIES_ATTRIBUTE_NAME) P properties) {

    Assert.isTrue(
        JSON_TYPE_FEATURE.equals(type),
        String.format("Type must be '%s'.", JSON_TYPE_FEATURE));

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

  public GeoJsonFeature(
      String id,
      double[] bbox,
      G geometry,
      P properties) {
    this(JSON_TYPE_FEATURE, id, bbox, geometry, properties);
  }

  /**
   * Constructs a GeoJSON feature with the specified parameters.
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
        JSON_TYPE_FEATURE,
        id,
        calculateBounds ? GeoJsonGeometryFactory.getBoundingBox(geometry) : null,
        geometry,
        properties);
  }

  @Schema(description = "The feature type.", required = true, example = JSON_TYPE_FEATURE)
  @JsonProperty(value = JSON_TYPE_ATTRIBUTE, required = true)
  public final String getType() {
    return JSON_TYPE_FEATURE;
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

  @Schema(description = "The properties of the GeoJSON feature.")
  @JsonInclude(Include.NON_EMPTY)
  @JsonProperty(JSON_FEATURE_PROPERTIES_ATTRIBUTE_NAME)
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
    if (!(o instanceof GeoJsonFeature)) {
      return false;
    }
    GeoJsonFeature<?, ?> that = (GeoJsonFeature<?, ?>) o;
    return Objects.equals(getId(), that.getId())
        && GeoJsonGeometryFactory.equals(getGeometry(), that.getGeometry())
        && Arrays.equals(getBbox(), that.getBbox())
        && Objects.equals(getProperties(), that.getProperties());
  }

}

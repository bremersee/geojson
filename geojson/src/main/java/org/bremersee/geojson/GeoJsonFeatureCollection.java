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
import static org.bremersee.geojson.GeoJsonConstants.FEATURES;
import static org.bremersee.geojson.GeoJsonConstants.BBOX;
import static org.bremersee.geojson.GeoJsonConstants.TYPE;
import static org.bremersee.geojson.GeoJsonConstants.FEATURE_COLLECTION;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bremersee.geojson.model.UnknownAware;
import org.locationtech.jts.geom.Geometry;
import org.springframework.util.Assert;

/**
 * A GeoJSON object with the type {@code FeatureCollection} is a feature collection object (see
 * <a href="https://tools.ietf.org/html/rfc7946#section-3.3">rfc7946 section 3.3</a>).
 *
 * @author Christian Bremer
 */
@Schema(description = "A GeoJSON object with type 'Feature'.")
@JsonPropertyOrder({"type", "bbox", "features"})
public class GeoJsonFeatureCollection<G extends Geometry, P> extends UnknownAware {

  @Schema(description = "The bounding box of the GeoJSON feature collection.")
  @JsonInclude(Include.NON_EMPTY)
  @JsonProperty(BBOX)
  private final double[] bbox;

  @SuppressWarnings("DefaultAnnotationParam")
  @Schema(description = "The features the GeoJSON feature collection.")
  @JsonInclude(Include.ALWAYS)
  @JsonProperty(FEATURES)
  private final List<GeoJsonFeature<G, P>> features;

  @JsonCreator
  GeoJsonFeatureCollection(
      @JsonProperty(value = TYPE, required = true) String type,
      @JsonProperty(BBOX) double[] bbox,
      @JsonProperty(FEATURES) List<GeoJsonFeature<G, P>> features) {

    Assert.isTrue(
        FEATURE_COLLECTION.equals(type),
        String.format("Type must be '%s'.", FEATURE_COLLECTION));

    if (isNull(bbox) || (bbox.length == 4) || (bbox.length == 6)) {
      this.bbox = bbox;
    } else {
      throw new IllegalArgumentException(
          "Bounding box must be null or must have a length of four or six.");
    }
    this.features = features;
  }

  public GeoJsonFeatureCollection(
      double[] bbox,
      List<GeoJsonFeature<G, P>> features) {

    this(FEATURE_COLLECTION, bbox, features);
  }

  /**
   * Constructs a GeoJSON feature collection with the specified parameters.
   *
   * @param features the GeoJSON features of the collection
   * @param calculateBounds if <code>true</code> the bounding box will be calculated otherwise
   *     the bounding box will be <code>null</code>
   */
  public GeoJsonFeatureCollection(
      List<GeoJsonFeature<G, P>> features,
      boolean calculateBounds) {

    this(
        FEATURE_COLLECTION,
        calculateBounds
            ? GeoJsonGeometryFactory.getBoundingBox(getGeometries((features)))
            : null,
        features);
  }

  @Schema(
      description = "The feature collection type.",
      required = true,
      example = FEATURE_COLLECTION)
  @JsonProperty(value = TYPE, required = true)
  public final String getType() {
    return FEATURE_COLLECTION;
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

  @JsonIgnore
  public List<GeoJsonFeature<G, P>> getFeatures() {
    return isNull(features) ? List.of() : Collections.unmodifiableList(features);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof GeoJsonFeatureCollection)) {
      return false;
    }
    GeoJsonFeatureCollection<?, ?> that = (GeoJsonFeatureCollection<?, ?>) o;
    return Arrays.equals(getBbox(), that.getBbox())
        && Objects.equals(getFeatures(), that.getFeatures());
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(getFeatures());
    result = 31 * result + Arrays.hashCode(getBbox());
    return result;
  }

  @Override
  public String toString() {
    return "GeoJsonFeatureCollection {"
        + ", features=" + getFeatures()
        + ", bbox=" + Arrays.toString(getBbox())
        + ", unknown=" + unknown()
        + '}';
  }

  private static List<Geometry> getGeometries(List<?> features) {
    //noinspection unchecked
    return Optional.ofNullable(features)
        .stream()
        .flatMap(Collection::stream)
        .map(obj -> ((GeoJsonFeature<Geometry, ?>) obj).getGeometry())
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}

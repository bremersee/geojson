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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.bremersee.geojson.GeoJsonConstants.BBOX;
import static org.bremersee.geojson.GeoJsonConstants.FEATURES;
import static org.bremersee.geojson.GeoJsonConstants.FEATURE_COLLECTION;
import static org.bremersee.geojson.GeoJsonConstants.TYPE;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bremersee.geojson.model.UnknownAware;
import org.locationtech.jts.geom.Geometry;

/**
 * A GeoJSON object with the type {@code FeatureCollection} is a feature collection object (see
 * <a href="https://tools.ietf.org/html/rfc7946#section-3.3">rfc7946 section 3.3</a>).
 *
 * @param <G> the geometry type parameter
 * @param <P> the properties type parameter
 * @author Christian Bremer
 */
@Schema(description = "A GeoJSON object with type 'Feature'.")
@JsonPropertyOrder({"type", "bbox", "features"})
public class GeoJsonFeatureCollection<G extends Geometry, P> extends UnknownAware {

  @Schema(hidden = true)
  @JsonIgnore
  private final boolean withBoundingBox;

  @Schema(hidden = true)
  @JsonIgnore
  private final Comparator<GeoJsonFeature<G, P>> comparator;

  @Schema(description = "The bounding box of the GeoJSON feature collection.")
  @JsonInclude(Include.NON_EMPTY)
  @JsonProperty(BBOX)
  private double[] bbox;

  @SuppressWarnings("DefaultAnnotationParam")
  @Schema(description = "The features the GeoJSON feature collection.")
  @JsonInclude(Include.ALWAYS)
  @JsonProperty(FEATURES)
  private final List<GeoJsonFeature<G, P>> features;

  /**
   * Instantiates a new Geo json feature collection.
   *
   * @param bbox the bbox
   * @param features the features
   * @param comparator the comparator
   */
  public GeoJsonFeatureCollection(
      double[] bbox,
      Collection<? extends GeoJsonFeature<G, P>> features,
      Comparator<GeoJsonFeature<G, P>> comparator) {

    if (isNull(bbox) || (bbox.length == 4) || (bbox.length == 6)) {
      this.bbox = bbox;
    } else {
      throw new IllegalArgumentException(
          "Bounding box must be null or must have a length of four or six.");
    }
    this.withBoundingBox = nonNull(this.bbox);
    this.comparator = comparator;
    this.features = new ArrayList<>();
    addAll(features);
  }

  /**
   * Instantiates a new geo json feature collection.
   *
   * @param bbox the bbox
   * @param features the features
   */
  public GeoJsonFeatureCollection(
      double[] bbox,
      Collection<? extends GeoJsonFeature<G, P>> features) {

    this(bbox, features, null);
  }

  /**
   * Instantiates a new Geo json feature collection.
   *
   * @param features the features
   * @param calculateBounds the calculate bounds
   */
  public GeoJsonFeatureCollection(
      Collection<? extends GeoJsonFeature<G, P>> features,
      boolean calculateBounds) {

    this(
        calculateBounds
            ? GeoJsonGeometryFactory.getBoundingBox(getGeometries((features)))
            : null,
        features,
        null);
  }

  /**
   * Instantiates a new Geo json feature collection.
   *
   * @param features the features
   * @param calculateBounds the calculate bounds
   * @param comparator the comparator
   */
  public GeoJsonFeatureCollection(
      Collection<? extends GeoJsonFeature<G, P>> features,
      boolean calculateBounds, Comparator<GeoJsonFeature<G, P>> comparator) {

    this(
        calculateBounds
            ? GeoJsonGeometryFactory.getBoundingBox(getGeometries((features)))
            : null,
        features,
        comparator);
  }

  /**
   * Instantiates a new Geo json feature collection.
   *
   * @param withBoundingBox the with bounding box
   * @param comparator the comparator
   */
  public GeoJsonFeatureCollection(
      boolean withBoundingBox,
      Comparator<GeoJsonFeature<G, P>> comparator) {

    this.withBoundingBox = withBoundingBox;
    this.features = new ArrayList<>();
    this.comparator = comparator;
  }

  /**
   * Instantiates a new geo json feature collection.
   *
   * @param withBoundingBox the with bounding box
   */
  public GeoJsonFeatureCollection(boolean withBoundingBox) {
    this(withBoundingBox, null);
  }

  /**
   * Instantiates a new geo json feature collection.
   *
   * @param type the type
   * @param bbox the bbox
   * @param features the features
   */
  @JsonCreator
  GeoJsonFeatureCollection(
      @JsonProperty(value = TYPE, required = true) String type,
      @JsonProperty(BBOX) double[] bbox,
      @JsonProperty(FEATURES) Collection<? extends GeoJsonFeature<G, P>> features) {

    this(bbox, features, null);
    if (!FEATURE_COLLECTION.equals(type)) {
      throw new IllegalArgumentException(String.format("Type must be '%s'.", FEATURE_COLLECTION));
    }
  }

  /**
   * Gets type.
   *
   * @return the type
   */
  @Schema(
      description = "The feature collection type.",
      requiredMode = RequiredMode.REQUIRED,
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

  /**
   * Gets features.
   *
   * @return the features
   */
  @JsonIgnore
  public List<GeoJsonFeature<G, P>> getFeatures() {
    return isNull(features) ? List.of() : Collections.unmodifiableList(features);
  }

  /**
   * Add.
   *
   * @param feature the feature
   */
  public void add(GeoJsonFeature<G, P> feature) {
    if (nonNull(feature)) {
      addAll(List.of(feature));
    }
  }

  /**
   * Add all.
   *
   * @param features the features
   */
  public void addAll(Collection<? extends GeoJsonFeature<G, P>> features) {
    if (nonNull(features) && !features.isEmpty()) {
      this.features.addAll(features);
      if (withBoundingBox) {
        this.bbox = GeoJsonGeometryFactory.getBoundingBox(getGeometries(this.features));
      }
      if (nonNull(comparator)) {
        this.features.sort(this.comparator);
      }
    }
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

  private static List<Geometry> getGeometries(Collection<?> features) {
    //noinspection unchecked
    return Optional.ofNullable(features)
        .stream()
        .flatMap(Collection::stream)
        .map(obj -> ((GeoJsonFeature<Geometry, ?>) obj).getGeometry())
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}

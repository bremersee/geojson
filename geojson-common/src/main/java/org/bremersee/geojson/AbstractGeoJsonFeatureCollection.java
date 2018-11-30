/*
 * Copyright 2015-2018 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A GeoJSON object with the type {@code FeatureCollection} is a feature collection object (see
 * <a href="https://tools.ietf.org/html/rfc7946#section-3.3">rfc7946 section 3.3</a>).
 *
 * @param <F> the type parameter
 * @param <P> the type parameter
 * @author Christian Bremer
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonTypeName("FeatureCollection")
@JsonSubTypes({
})
public abstract class AbstractGeoJsonFeatureCollection<F extends AbstractGeoJsonFeature, P>
    extends UnknownAware {

  @JsonProperty
  private String id = null;

  @JsonProperty
  private List<F> features;

  @JsonProperty
  private double[] bbox = null;

  @JsonProperty
  private P properties = null;

  /**
   * Instantiates a new abstract geo json feature collection.
   */
  protected AbstractGeoJsonFeatureCollection() {
  }

  /**
   * Instantiates a new abstract geo json feature collection.
   *
   * @param id the id
   * @param features the features
   * @param bbox the bbox
   * @param properties the properties
   */
  protected AbstractGeoJsonFeatureCollection(
      final String id,
      final List<F> features,
      final double[] bbox,
      final P properties) {

    setId(id);
    setFeatures(features);
    setBbox(bbox);
    setProperties(properties);
  }

  /**
   * Return the id of this GeoJSON feature or {@code null} if there is no id available.
   *
   * @return the id of this GeoJSON feature
   */
  public String getId() {
    return id;
  }

  /**
   * Set the id of this GeoJSON feature.
   *
   * @param id the id of this GeoJSON feature
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Return the features of this collection.
   *
   * @return the features of this collection
   */
  public List<F> getFeatures() {
    if (features == null) {
      features = new ArrayList<>();
    }
    return features;
  }

  /**
   * Set the faetures of this collection.
   *
   * @param features the feature
   */
  public void setFeatures(List<F> features) {
    this.features = features;
  }

  /**
   * Return the bounding box of the GeoJSON object or {@code null} if there is no such object (see
   * <a href="https://tools.ietf.org/html/rfc7946#section-5">Bounding Box</a>).
   *
   * @return the bounding box
   */
  public double[] getBbox() {
    return bbox;
  }

  /**
   * Set the bounding box of the GeoJSON object.
   *
   * @param bbox the bounding box to set
   */
  public void setBbox(double[] bbox) {
    this.bbox = bbox;
  }

  /**
   * Return the properties that are associated with this GeoJSON feature collection or {@code null}
   * if there are no properties.
   *
   * @return the properties of this feature collection
   */
  public P getProperties() {
    return properties;
  }

  /**
   * Set the properties that are associated with this GeoJSON feature collection.
   *
   * <p>Be aware that each object must be serializable with the Jackson JSON processor.
   *
   * @param properties the properties of this feature collection
   */
  public void setProperties(final P properties) {
    this.properties = properties;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AbstractGeoJsonFeatureCollection)) {
      return false;
    }
    AbstractGeoJsonFeatureCollection that = (AbstractGeoJsonFeatureCollection) o;
    return Arrays.equals(bbox, that.bbox) &&
        Objects.equals(properties, that.properties) &&
        Objects.equals(id, that.id) &&
        Objects.equals(features, that.features);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(properties, id, features);
    result = 31 * result + Arrays.hashCode(bbox);
    return result;
  }

  @Override
  public String toString() {
    return "GeoJsonFeatureCollection {" +
        "id='" + id + '\'' +
        ", features=" + features +
        ", bbox=" + Arrays.toString(bbox) +
        ", properties=" + properties +
        '}';
  }
}

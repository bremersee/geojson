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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.Arrays;
import java.util.Objects;

/**
 * A GeoJSON object with the type {@code Feature} (see
 * <a href="https://tools.ietf.org/html/rfc7946#section-3.3">rfc7946 section 3.3</a>).
 *
 * @param <G> the type parameter
 * @param <P> the type parameter
 * @author Christian Bremer
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
})
@JsonTypeName("Feature")
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbstractGeoJsonFeature<G, P> {

  private String id = null;

  private G geometry;

  private double[] bbox = null;

  private P properties = null;

  /**
   * Instantiates a new abstract geo json feature.
   */
  protected AbstractGeoJsonFeature() {
  }

  /**
   * Instantiates a new abstract geo json feature.
   *
   * @param id the id
   * @param geometry the geometry
   * @param bbox the bbox
   * @param properties the properties
   */
  protected AbstractGeoJsonFeature(
      final String id,
      final G geometry,
      final double[] bbox,
      final P properties) {

    setId(id);
    setGeometry(geometry);
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
   * Return the geometry object of this GeoJSON feature.
   *
   * @return the geometry
   */
  public G getGeometry() {
    return geometry;
  }

  /**
   * Set the geometry object of this GeoJSON feature.
   *
   * @param geometry the geometry to set
   */
  public void setGeometry(final G geometry) {
    this.geometry = geometry;
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
   * Return the properties that are associated with this GeoJSON feature or {@code null} if there
   * are no properties.
   *
   * @return the properties of this feature
   */
  public P getProperties() {
    return properties;
  }

  /**
   * Set the properties that are associated with this GeoJSON feature.
   *
   * <p>Be aware that each object must be serializable with the Jackson JSON processor.
   *
   * @param properties the properties of this feature
   */
  public void setProperties(final P properties) {
    this.properties = properties;
  }

  @Override
  public String toString() {
    return "GeoJsonFeature {" +
        "id='" + id + '\'' +
        ", geometry=" + getGeometry() +
        ", bbox=" + Arrays.toString(bbox) +
        ", properties=" + properties +
        '}';
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(id, getGeometry(), properties);
    result = 31 * result + Arrays.hashCode(bbox);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AbstractGeoJsonFeature)) {
      return false;
    }
    AbstractGeoJsonFeature<?, ?> that = (AbstractGeoJsonFeature<?, ?>) o;
    return Objects.equals(id, that.id) &&
        equals(getGeometry(), that.getGeometry()) &&
        Arrays.equals(bbox, that.bbox) &&
        Objects.equals(properties, that.properties);
  }

  /**
   * Equals boolean.
   *
   * @param g1 the g 1
   * @param g2 the g 2
   * @return the boolean
   */
  abstract boolean equals(G g1, Object g2);

}

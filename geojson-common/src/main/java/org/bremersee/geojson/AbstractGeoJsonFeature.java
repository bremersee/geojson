/*
 * Copyright 2017 the original author or authors.
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
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.Objects;

/**
 * A GeoJSON object with the type {@code Feature}
 * (see <a href="https://tools.ietf.org/html/rfc7946#section-3.2">rfc7946 section 3.2</a>).
 *
 * @author Christian Bremer
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractGeoJsonFeature<G, P> {

  @JsonProperty
  private String id = null;

  @JsonProperty
  private G geometry;

  @JsonProperty
  private double[] bbox = null;

  @JsonProperty
  private P properties = null;

  protected AbstractGeoJsonFeature() {
  }

  protected AbstractGeoJsonFeature(
      final String id,
      final G geometry,
      final double[] bbox,
      final P properties
  ) {
    setId(id);
    setGeometry(geometry);
    setBbox(bbox);
    setProperties(properties);
  }

  /**
   * Return the id of this GeoJSON feature or <code>null</code> if there is no id available.
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
   * Return the bounding box of the GeoJSON object or <code>null</code> if there is no such object
   * (see
   * <a href="http://geojson.org/geojson-spec.html#bounding-boxes">http://geojson.org/geojson-spec.html#bounding-boxes</a>
   * ).
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
   * Return a map of named objects that are associated with this GeoJSON feature.
   *
   * @return a map of named objects
   */
  public P getProperties() {
    return properties;
  }

  /**
   * Set a map of named objects that are associated with this GeoJSON feature.
   * <p>
   * Be aware that each object must be serializable with the Jackson JSON processor.
   *
   * @param properties a map of named objects
   */
  public void setProperties(final P properties) {
    this.properties = properties;
  }

  @Override
  public String toString() {
    return "GeoJsonFeature {" +
        "id='" + id + '\'' +
        ", geometry=" + geometry +
        ", bbox=" + Arrays.toString(bbox) +
        ", properties=" + properties +
        '}';
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(id, geometry, properties);
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
        equals(geometry, that.geometry) &&
        Arrays.equals(bbox, that.bbox) &&
        Objects.equals(properties, that.properties);
  }

  abstract boolean equals(G g1, Object g2);

}

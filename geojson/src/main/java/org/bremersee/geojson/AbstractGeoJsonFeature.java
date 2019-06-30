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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.Arrays;
import java.util.Objects;
import org.bremersee.common.model.UnknownAware;

/**
 * A GeoJSON object with the type {@code Feature} (see
 * <a href="https://tools.ietf.org/html/rfc7946#section-3.3">rfc7946 section 3.3</a>).
 *
 * @param <G> the geometry type parameter
 * @param <P> the properties type parameter
 * @author Christian Bremer
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
})
@JsonTypeName("Feature")
@JsonPropertyOrder({"id", "bbox", "geometry", "properties"})
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbstractGeoJsonFeature<G, P> extends UnknownAware {

  @JsonIgnore
  private double[] bbox = null;

  @JsonIgnore
  private P properties = null;

  /**
   * Instantiates a new abstract geo json feature.
   */
  protected AbstractGeoJsonFeature() {
  }

  /**
   * Instantiates a new abstract geo json feature.
   *
   * @param id         the id
   * @param geometry   the geometry
   * @param bbox       the bbox
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
  @JsonInclude(Include.NON_NULL)
  @JsonProperty("id")
  public abstract String getId();

  /**
   * Set the id of this GeoJSON feature.
   *
   * @param id the id of this GeoJSON feature
   */
  @JsonProperty("id")
  public abstract void setId(String id);

  /**
   * Return the geometry object of this GeoJSON feature.
   *
   * @return the geometry
   */
  @JsonProperty("geometry")
  public abstract G getGeometry();

  /**
   * Set the geometry object of this GeoJSON feature.
   *
   * @param geometry the geometry to set
   */
  @JsonProperty("geometry")
  public abstract void setGeometry(final G geometry);

  /**
   * Return the bounding box of the GeoJSON object or {@code null} if there is no such object (see
   * <a href="https://tools.ietf.org/html/rfc7946#section-5">Bounding Box</a>).
   *
   * @return the bounding box
   */
  @JsonInclude(Include.NON_EMPTY)
  @JsonProperty("bbox")
  @SuppressWarnings("WeakerAccess")
  public double[] getBbox() {
    return bbox;
  }

  /**
   * Set the bounding box of the GeoJSON object.
   *
   * @param bbox the bounding box to set
   */
  @JsonProperty("bbox")
  public void setBbox(double[] bbox) {
    if (bbox == null || bbox.length == 4 || bbox.length == 6) {
      this.bbox = bbox;
    } else {
      throw new IllegalArgumentException(
          "Bounding box must be null or must have a length of four or six.");
    }
  }

  /**
   * Return the properties that are associated with this GeoJSON feature or {@code null} if there
   * are no properties.
   *
   * @return the properties of this feature
   */
  @JsonInclude(Include.NON_EMPTY)
  @JsonProperty("properties")
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
  @JsonProperty("properties")
  public void setProperties(final P properties) {
    this.properties = properties;
  }

  @Override
  public String toString() {
    return "GeoJsonFeature {"
        + "id='" + getId() + '\''
        + ", geometry=" + getGeometry()
        + ", bbox=" + Arrays.toString(getBbox())
        + ", properties=" + getProperties()
        + '}';
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(getId(), getGeometry(), getProperties());
    result = 31 * result + Arrays.hashCode(getBbox());
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
    return Objects.equals(getId(), that.getId())
        && equals(getGeometry(), that.getGeometry())
        && Arrays.equals(getBbox(), that.getBbox())
        && Objects.equals(getProperties(), that.getProperties());
  }

  /**
   * Equals boolean.
   *
   * @param g1 the g 1
   * @param g2 the g 2
   * @return the boolean
   */
  protected abstract boolean equals(G g1, Object g2);

}

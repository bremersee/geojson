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
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Abstract base class for the coordinate reference system (CRS) of a GeoJSON object.
 *
 * <p>The specification of coordinate reference systems has been removed, see
 * <a href="https://tools.ietf.org/html/rfc7946#appendix-B">rfc7946 appendix B</a>,
 * the "crs" member is no longer used.
 *
 * @author Christian Bremer
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@JsonTypeInfo(use = Id.NAME, property = "type", include = As.EXISTING_PROPERTY)
@JsonSubTypes({
    @Type(value = GeoJsonLinkedCrs.class, name = "link"),
    @Type(value = GeoJsonNamedCrs.class, name = "name")
})
public class AbstractGeoJsonCrs implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * The Type.
   */
  @JsonProperty("type")
  protected String type;

  @JsonProperty(value = "properties")
  private Map<String, Object> properties = new LinkedHashMap<>(); // NOSONAR

  /**
   * Instantiates a new abstract geo json crs.
   *
   * @param type the type
   */
  protected AbstractGeoJsonCrs(String type) {
    this.type = type;
  }

  /**
   * Properties that store information about the coordinate reference system (CRS).
   *
   * @return the properties that store information about the coordinate reference system (CRS)
   */
  protected final Map<String, Object> getProperties() {
    return properties;
  }

  /**
   * Set the properties the properties that store information about the coordinate reference system
   * (CRS).
   *
   * @param properties the properties that store information about the coordinate reference system
   * (CRS)
   */
  protected final void setProperties(final Map<String, Object> properties) {
    if (properties == null) {
      this.properties = new LinkedHashMap<>();
    } else {
      this.properties = properties;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AbstractGeoJsonCrs)) {
      return false;
    }
    AbstractGeoJsonCrs that = (AbstractGeoJsonCrs) o;
    if (!Objects.equals(type, that.type)) {
      return false;
    }
    if (properties == that.properties) {
      return true;
    }
    if (properties == null || that.properties == null) {
      return false;
    }
    return Objects.equals(new TreeMap<>(properties), new TreeMap<>(that.properties));
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, properties);
  }

  @Override
  public String toString() {
    return "AbstractGeoJsonCrs {" +
        "type=" + type +
        "properties=" + properties +
        '}';
  }
}

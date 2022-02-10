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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bremersee.geojson.utils.GeometryUtils;
import org.locationtech.jts.geom.Geometry;

/**
 * A GeoJSON object with the type {@code Feature} (see
 * <a href="https://tools.ietf.org/html/rfc7946#section-3.2">rfc7946 section 3.2</a>).
 *
 * @author Christian Bremer
 */
@Schema(description = "A GeoJSON object with type 'Feature'.")
public class GeoJsonFeature extends AbstractGeoJsonFeature<Geometry, Map<String, Object>>
    implements Serializable {

  private static final long serialVersionUID = 2L;

  @JsonIgnore
  private String id;

  @JsonIgnore
  private Geometry geometry;

  /**
   * Default constructor.
   */
  public GeoJsonFeature() {
    super();
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
      final String id,
      final Geometry geometry,
      final boolean calculateBounds,
      final Map<String, Object> properties) {

    if (id != null && id.trim().length() > 0) {
      setId(id);
    }
    setGeometry(geometry);
    if (calculateBounds && geometry != null) {
      setBbox(GeometryUtils.getBoundingBox(geometry));
    }
    if (properties != null) {
      getProperties().putAll(properties);
    }
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Schema(description = "GeoJSON", implementation = org.bremersee.geojson.model.Geometry.class)
  @JsonSerialize(using = GeometrySerializer.class)
  @Override
  public Geometry getGeometry() {
    return geometry;
  }

  @Schema(description = "GeoJSON", implementation = org.bremersee.geojson.model.Geometry.class)
  @JsonDeserialize(using = GeometryDeserializer.class)
  @Override
  public void setGeometry(final Geometry geometry) {
    this.geometry = geometry;
  }

  @Override
  public Map<String, Object> getProperties() {
    if (super.getProperties() == null) {
      super.setProperties(new LinkedHashMap<>());
    }
    return super.getProperties();
  }

  @Override
  protected boolean equals(final Geometry g1, final Object g2) {
    if (g1 == g2) {
      return true;
    } else if (g1 != null && g2 instanceof Geometry) {
      return GeometryUtils.equals(g1, (Geometry) g2);
    } else {
      return false;
    }
  }

}

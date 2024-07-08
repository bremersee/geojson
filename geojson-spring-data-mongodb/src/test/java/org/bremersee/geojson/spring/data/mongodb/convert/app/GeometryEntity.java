/*
 * Copyright 2022 the original author or authors.
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

package org.bremersee.geojson.spring.data.mongodb.convert.app;

import java.util.Objects;
import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The geometry entity.
 *
 * @author Christian Bremer
 */
@Document(collection = "geometry")
@SuppressWarnings("unused")
public class GeometryEntity {

  @Id
  private String id;

  @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
  private Geometry geometry;

  /**
   * Instantiates a new geometry entity.
   */
  public GeometryEntity() {
  }

  /**
   * Instantiates a new geometry entity.
   *
   * @param geometry the geometry
   */
  public GeometryEntity(Geometry geometry) {
    this.geometry = geometry;
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets geometry.
   *
   * @return the geometry
   */
  public Geometry getGeometry() {
    return geometry;
  }

  /**
   * Sets geometry.
   *
   * @param geometry the geometry
   */
  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GeometryEntity that = (GeometryEntity) o;
    return Objects.equals(id, that.id) && GeoJsonGeometryFactory.equals(geometry, that.geometry);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, geometry);
  }

  @Override
  public String toString() {
    return "GeometryEntity{"
        + "id='" + id + '\''
        + ", geometry=" + (geometry != null ? geometry.toText() : "null")
        + '}';
  }
}

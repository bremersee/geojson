/*
 * Copyright 2020 the original author or authors.
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
import org.locationtech.jts.geom.GeometryCollection;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * The geometry collection entity.
 *
 * @author Christian Bremer
 */
@Document(collection = "geometry-collection")
@SuppressWarnings("unused")
public class GeometryCollectionEntity {

  @Id
  private String id;

  @Field(name = "geometryCollection")
  private GeometryCollection geometry;

  /**
   * Instantiates a new geometry collection entity.
   */
  public GeometryCollectionEntity() {
  }

  /**
   * Instantiates a new geometry collection entity.
   *
   * @param geometry the geometry
   */
  public GeometryCollectionEntity(GeometryCollection geometry) {
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
  public GeometryCollection getGeometry() {
    return geometry;
  }

  /**
   * Sets geometry.
   *
   * @param geometry the geometry
   */
  public void setGeometry(GeometryCollection geometry) {
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
    GeometryCollectionEntity that = (GeometryCollectionEntity) o;
    return Objects.equals(id, that.id) && GeoJsonGeometryFactory.equals(geometry, that.geometry);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, geometry);
  }

  @Override
  public String toString() {
    return "GeometryCollectionEntity{"
        + "id='" + id + '\''
        + ", geometry=" + (geometry != null ? geometry.toText() : "null")
        + '}';
  }
}

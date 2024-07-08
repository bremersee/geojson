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

package org.bremersee.geojson;

/**
 * The type Geo json constants.
 *
 * @author Christian Bremer
 */
public abstract class GeoJsonConstants {

  /**
   * The constant POINT.
   */
  public static final String POINT = "Point";

  /**
   * The constant LINESTRING.
   */
  public static final String LINESTRING = "LineString";

  /**
   * The constant POLYGON.
   */
  public static final String POLYGON = "Polygon";

  /**
   * The constant MULTI_POINT.
   */
  public static final String MULTI_POINT = "MultiPoint";

  /**
   * The constant MULTI_LINESTRING.
   */
  public static final String MULTI_LINESTRING = "MultiLineString";

  /**
   * The constant MULTI_POLYGON.
   */
  public static final String MULTI_POLYGON = "MultiPolygon";

  /**
   * The constant GEOMETRY_COLLECTION.
   */
  public static final String GEOMETRY_COLLECTION = "GeometryCollection";

  /**
   * The constant FEATURE.
   */
  public static final String FEATURE = "Feature";

  /**
   * The constant FEATURE_COLLECTION.
   */
  public static final String FEATURE_COLLECTION = "FeatureCollection";


  /**
   * The constant TYPE.
   */
  public static final String TYPE = "type";

  /**
   * The constant ID.
   */
  public static final String ID = "id";

  /**
   * The constant BBOX.
   */
  public static final String BBOX = "bbox";

  /**
   * The constant COORDINATES.
   */
  public static final String COORDINATES = "coordinates";

  /**
   * The constant GEOMETRIES.
   */
  public static final String GEOMETRIES = "geometries";

  /**
   * The constant GEOMETRY.
   */
  public static final String GEOMETRY = "geometry";

  /**
   * The constant FEATURES.
   */
  public static final String FEATURES = "features";

  /**
   * The constant PROPERTIES.
   */
  public static final String PROPERTIES = "properties";


  private GeoJsonConstants() {
  }

}

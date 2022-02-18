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

package org.bremersee.geojson.crs;

/**
 * The geo json crs constants.
 *
 * @author Christian Bremer
 */
public class GeoJsonCrsConstants {

  /**
   * Maximum latitude for mercator projection.
   */
  public static final double MERCATOR_MAX_LAT = 85.05112878;

  /**
   * Minimum latitude for mercator projection.
   */
  public static final double MERCATOR_MIN_LAT = -85.05112878;

  /**
   * Default spatial authority: 'EPSG'.
   */
  public static final String DEFAULT_SPATIAL_AUTHORITY = "EPSG";

  /**
   * Reference ID of WGS84: '4326'.
   */
  public static final int WGS84_SPATIAL_REFERENCE_ID = 4326;

  /**
   * CRS (Coordinate reference system) of WGS84: 'EPSG:4326'.
   */
  public static final String WGS84_CRS =
      DEFAULT_SPATIAL_AUTHORITY + ":" + WGS84_SPATIAL_REFERENCE_ID;

  /**
   * Reference ID of mercator projection: '3857'.
   */
  public static final int MERCATOR_SPATIAL_REFERENCE_ID = 3857;

  /**
   * CRS (Coordinate reference system) of mercator: 'EPSG:3857'.
   */
  public static final String MERCATOR_CRS =
      DEFAULT_SPATIAL_AUTHORITY + ":" + MERCATOR_SPATIAL_REFERENCE_ID;

  /**
   * Alternative reference ID of mercator: '900913'.
   */
  public static final int MERCATOR_SPATIAL_REFERENCE_ID_ALT = 900913;

  /**
   * Alternative CRS (Coordinate reference system) of mercator: 'EPSG:900913'.
   */
  public static final String MERCATOR_CRS_ALT =
      DEFAULT_SPATIAL_AUTHORITY + ":" + MERCATOR_SPATIAL_REFERENCE_ID_ALT;
}

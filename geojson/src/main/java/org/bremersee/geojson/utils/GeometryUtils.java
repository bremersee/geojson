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

package org.bremersee.geojson.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateFilter;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

/**
 * Utility methods for geometry objects.
 *
 * @author Christian Bremer
 */
public abstract class GeometryUtils {

  /**
   * Never construct.
   */
  private GeometryUtils() {
  }

  /**
   * The earth radius in meters.
   */
  public static final double EARTH_RADIUS_METERS = 6378137.;

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
   * Alternative reference ID of mercator: '900913'.
   */
  public static final int MERCATOR_SPATIAL_REFERENCE_ID_ALT = 900913;

  /**
   * CRS (Coordinate reference system) of mercator: 'EPSG:3857'.
   */
  public static final String MERCATOR_CRS =
      DEFAULT_SPATIAL_AUTHORITY + ":" + MERCATOR_SPATIAL_REFERENCE_ID;

  /**
   * Alternative CRS (Coordinate reference system) of mercator: 'EPSG:900913'.
   */
  public static final String MERCATOR_CRS_ALT =
      DEFAULT_SPATIAL_AUTHORITY + ":" + MERCATOR_SPATIAL_REFERENCE_ID_ALT;

  /**
   * Default geometry factory.
   */
  private static final GeometryFactory DEFAULT_GEOMETRY_FACTORY = new GeometryFactory();

  private static GeometryFactory geometryFactory(GeometryFactory geometryFactory) {
    return geometryFactory != null ? geometryFactory : DEFAULT_GEOMETRY_FACTORY;
  }

  /**
   * Checks whether two geometry objects are equal.
   *
   * <p>Because the {@link GeometryCollection#equals(Geometry)} method throws an exception, this
   * method is used in the GeoJSON classes.
   *
   * @param g1 one geometry
   * @param g2 another geometry
   * @return {@code true} if the geometries are equal otherwise {@code false}
   */
  public static boolean equals(final Geometry g1, final Geometry g2) {
    if (g1 == null && g2 == null) {
      return true;
    }
    if (g1 == null || g2 == null) {
      return false;
    }
    if (g1 == g2) {
      return true;
    }
    if (g1 instanceof GeometryCollection && g2 instanceof GeometryCollection) {
      return equals((GeometryCollection) g1, (GeometryCollection) g2);
    }
    try {
      return g1.equals(g2);
    } catch (Exception t) {
      return false;
    }
  }

  /**
   * Checks whether two geometry collections are equal.
   *
   * @param gc1 one geometry collection
   * @param gc2 another geometry collection
   * @return {@code true} if the geometry collections are equal otherwise {@code false}
   */
  private static boolean equals(final GeometryCollection gc1, final GeometryCollection gc2) {
    if (gc1 == null && gc2 == null) {
      return true;
    }
    if (gc1 == null || gc2 == null) {
      return false;
    }
    if (gc1.getNumGeometries() != gc2.getNumGeometries()) {
      return false;
    }
    for (int i = 0; i < gc1.getNumGeometries(); i++) {
      Geometry g1 = gc1.getGeometryN(i);
      Geometry g2 = gc2.getGeometryN(i);
      if (!equals(g1, g2)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Calculate the bounding box of the specified geometry (see
   * <a href="https://tools.ietf.org/html/rfc7946#section-5">bounding-boxes</a>).
   *
   * <p>A GeoJSON object MAY have a member named "bbox" to include information on the coordinate
   * range for its Geometries, Features, or FeatureCollections.  The value of the bbox member MUST
   * be an array of length 2*n where n is the number of dimensions represented in the contained
   * geometries, with all axes of the most southwesterly point followed by all axes of the more
   * northeasterly point. The axes order of a bbox follows the axes order of geometries.
   *
   * @param geometry the geometry
   * @return {@code null} if the bounding box can not be calculated, otherwise the bounding box
   */
  public static double[] getBoundingBox(final Geometry geometry) {
    return geometry != null ? getBoundingBox(Collections.singletonList(geometry)) : null;
  }

  /**
   * Calculate the bounding box of the specified geometries.
   *
   * @param geometries the geometries
   * @return {@code null} if the bounding box can not be calculated, otherwise the bounding box
   */
  public static double[] getBoundingBox(final Collection<? extends Geometry> geometries) {
    if (geometries == null || geometries.isEmpty()) {
      return null;
    }
    double minX = Double.NaN;
    double minY = Double.NaN;
    double minZ = Double.NaN;
    double maxX = Double.NaN;
    double maxY = Double.NaN;
    double maxZ = Double.NaN;
    for (final Geometry geometry : geometries) {
      if (geometry != null && geometry.getCoordinates() != null) {
        final Coordinate[] coords = geometry.getCoordinates();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < coords.length; i++) {
          if (Double.isNaN(minX)) {
            minX = coords[i].getX();
          } else if (!Double.isNaN(coords[i].getX())) {
            minX = Math.min(minX, coords[i].getX());
          }
          if (Double.isNaN(minY)) {
            minY = coords[i].getY();
          } else if (!Double.isNaN(coords[i].getY())) {
            minY = Math.min(minY, coords[i].getY());
          }
          if (Double.isNaN(minZ)) {
            minZ = coords[i].getZ();
          } else if (!Double.isNaN(coords[i].getZ())) {
            minZ = Math.min(minZ, coords[i].getZ());
          }

          if (Double.isNaN(maxX)) {
            maxX = coords[i].getX();
          } else if (!Double.isNaN(coords[i].getX())) {
            maxX = Math.max(maxX, coords[i].getX());
          }
          if (Double.isNaN(maxY)) {
            maxY = coords[i].getY();
          } else if (!Double.isNaN(coords[i].getY())) {
            maxY = Math.max(maxY, coords[i].getY());
          }
          if (Double.isNaN(maxZ)) {
            maxZ = coords[i].getZ();
          } else if (!Double.isNaN(coords[i].getZ())) {
            maxZ = Math.max(maxZ, coords[i].getZ());
          }
        }
      }
    }
    if (!Double.isNaN(minX) && !Double.isNaN(maxX) && !Double.isNaN(minY) && !Double.isNaN(maxY)) {
      if (!Double.isNaN(minZ) && !Double.isNaN(maxZ)) {
        return new double[]{minX, minY, minZ, maxX, maxY, maxZ};
      }
      return new double[]{minX, minY, maxX, maxY};
    }
    return null;
  }

  /**
   * Returns the coordinate in the south-west.
   *
   * @param boundingBox the bounding box
   * @return the coordinate in the south-west
   */
  public static Coordinate getSouthWest(final double[] boundingBox) {
    if (boundingBox == null || !(boundingBox.length == 4 || boundingBox.length == 6)) {
      return null;
    }
    return createCoordinate(boundingBox[0], boundingBox[1]);
  }

  /**
   * Returns the coordinate in the north-west.
   *
   * @param boundingBox the bounding box
   * @return the coordinate in the north-west
   */
  public static Coordinate getNorthWest(final double[] boundingBox) {
    if (boundingBox == null || !(boundingBox.length == 4 || boundingBox.length == 6)) {
      return null;
    }
    if (boundingBox.length == 6) {
      // 0   1   2   3   4   5
      // x0, y0, z0, x1, y1, z1
      return createCoordinate(boundingBox[0], boundingBox[4]);
    } else {
      // 0   1   2   3
      // x0, y0, x1, y1
      return createCoordinate(boundingBox[0], boundingBox[3]);
    }
  }

  /**
   * Returns the coordinate in the north-east.
   *
   * @param boundingBox the bounding box
   * @return the coordinate in the north-east
   */
  public static Coordinate getNorthEast(final double[] boundingBox) {
    if (boundingBox == null || !(boundingBox.length == 4 || boundingBox.length == 6)) {
      return null;
    }
    if (boundingBox.length == 6) {
      // 0   1   2   3   4   5
      // x0, y0, z0, x1, y1, z1
      return createCoordinate(boundingBox[3], boundingBox[4]);
    } else {
      // 0   1   2   3
      // x0, y0, x1, y1
      return createCoordinate(boundingBox[2], boundingBox[3]);
    }
  }

  /**
   * Returns the coordinate in the south-east.
   *
   * @param boundingBox the bounding box
   * @return the coordinate in the south-east
   */
  public static Coordinate getSouthEast(final double[] boundingBox) {
    if (boundingBox == null || !(boundingBox.length == 4 || boundingBox.length == 6)) {
      return null;
    }
    if (boundingBox.length == 6) {
      // 0   1   2   3   4   5
      // x0, y0, z0, x1, y1, z1
      return createCoordinate(boundingBox[3], boundingBox[1]);
    } else {
      // 0   1   2   3
      // x0, y0, x1, y1
      return createCoordinate(boundingBox[2], boundingBox[1]);
    }
  }

  /**
   * Returns a polygon from the bounding box.
   *
   * @param boundingBox the bounding bos
   * @return the polygon or {@code null} if the bounding box is {@code null} or empty
   */
  public static Polygon getBoundingBoxAsPolygon2D(final double[] boundingBox) {
    return getBoundingBoxAsPolygon2D(boundingBox, null);
  }

  /**
   * Returns a polygon from the bounding box.
   *
   * @param boundingBox the bounding bos
   * @param geometryFactory the geometry factory
   * @return the polygon or {@code null} if the bounding box is {@code null} or empty
   */
  public static Polygon getBoundingBoxAsPolygon2D(
      final double[] boundingBox,
      final GeometryFactory geometryFactory) {

    final Coordinate sw = getSouthWest(boundingBox);
    final Coordinate se = getSouthEast(boundingBox);
    final Coordinate ne = getNorthEast(boundingBox);
    final Coordinate nw = getNorthWest(boundingBox);
    if (sw == null || se == null || ne == null || nw == null) {
      return null;
    }
    float x1 = (float) sw.getX();
    float x2 = (float) se.getX();
    if (x1 == x2) {
      return null;
    }
    float y1 = (float) sw.getY();
    float y2 = (float) nw.getY();
    if (y1 == y2) {
      return null;
    }
    return createPolygon(
        createLinearRing(Arrays.asList(sw, se, ne, nw, sw), geometryFactory),
        geometryFactory);
  }

  /**
   * Returns the bounding box of the geometry as polygon.
   *
   * @param geometry the geometry
   * @return the bounding box of the geometry as polygon
   */
  public static Polygon getBoundingBoxAsPolygon2D(final Geometry geometry) {
    return getBoundingBoxAsPolygon2D(geometry, null);
  }

  /**
   * Returns the bounding box of the geometry as polygon.
   *
   * @param geometry the geometry
   * @param geometryFactory the geometry factory to use
   * @return the bounding box of the geometry as polygon
   */
  public static Polygon getBoundingBoxAsPolygon2D(
      final Geometry geometry,
      final GeometryFactory geometryFactory) {
    return getBoundingBoxAsPolygon2D(getBoundingBox(geometry), geometryFactory);
  }

  /**
   * Returns the Well-known Text representation of this Geometry. For a definition of the Well-known
   * Text format, see the OpenGIS Simple Features Specification.
   *
   * @param geometry the geometry
   * @return the Well-known Text representation of this Geometry
   */
  public static String toWKT(final Geometry geometry) {
    return Optional.ofNullable(geometry)
        .map(Geometry::toText)
        .orElse(null);
  }

  /**
   * Reads a Well-Known Text representation of a Geometry from a {@link String}.
   *
   * @param wkt one or more strings (see the OpenGIS Simple Features Specification) separated by
   *     whitespace
   * @return a Geometry specified by wellKnownText
   * @throws RuntimeException if a parsing problem occurs
   */
  public static Geometry fromWKT(final String wkt) throws RuntimeException {
    return fromWKT(wkt, null);
  }

  /**
   * Reads a Well-Known Text representation of a Geometry from a {@link String}.
   *
   * @param wkt one or more strings (see the OpenGIS Simple Features Specification) separated by
   *     whitespace
   * @param geometryFactory the geometry factory to use
   * @return a Geometry specified by wellKnownText
   * @throws RuntimeException if a parsing problem occurs
   */
  public static Geometry fromWKT(final String wkt, final GeometryFactory geometryFactory)
      throws RuntimeException {

    try {
      return new WKTReader(geometryFactory(geometryFactory)).read(wkt);
    } catch (NullPointerException n) {
      return null;
    } catch (ParseException e) {
      throw new RuntimeException("Parsing WKT [" + wkt + "] failed.", e);
    }
  }

  /**
   * Reads a Well-Known Text representation of a Geometry from a {@link Reader}.
   *
   * @param reader a {@link Reader} which will return a string (see the OpenGIS Simple Features
   *     Specification)
   * @return a Geometry read from reader
   * @throws RuntimeException if a parsing problem occurs
   */
  public static Geometry fromWKT(final Reader reader) throws RuntimeException {
    return fromWKT(reader, null);
  }

  /**
   * Reads a Well-Known Text representation of a Geometry from a {@link Reader}.
   *
   * @param reader a {@link Reader} which will return a string (see the OpenGIS Simple Features
   *     Specification)
   * @param geometryFactory the geometry factory to use
   * @return a Geometry read from reader
   * @throws RuntimeException if a parsing problem occurs
   */
  public static Geometry fromWKT(final Reader reader, final GeometryFactory geometryFactory)
      throws RuntimeException {

    try (Reader r = reader) {
      return new WKTReader(geometryFactory(geometryFactory)).read(r);
    } catch (NullPointerException n) {
      return null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Reads a Well-Known Text representation of a Geometry from an {@link InputStream}.
   *
   * @param inputStream an {@link InputStream} which will return a string (see the OpenGIS
   *     Simple Features Specification)
   * @param charsetName the charset to use
   * @return a Geometry read from the input stream
   * @throws RuntimeException if a parsing problem occurs
   */
  public static Geometry fromWKT(final InputStream inputStream, final String charsetName)
      throws RuntimeException {
    return fromWKT(inputStream, charsetName, null);
  }

  /**
   * Reads a Well-Known Text representation of a Geometry from an {@link InputStream}.
   *
   * @param inputStream an {@link InputStream} which will return a string (see the OpenGIS
   *     Simple Features Specification)
   * @param charsetName the charset to use
   * @param geometryFactory the geometry factory to use
   * @return a Geometry read from the input stream
   * @throws RuntimeException if a parsing problem occurs
   */
  public static Geometry fromWKT(
      final InputStream inputStream,
      final String charsetName,
      final GeometryFactory geometryFactory)
      throws RuntimeException {

    final String cs = charsetName == null ? StandardCharsets.UTF_8.name() : charsetName;
    try (InputStreamReader reader = new InputStreamReader(inputStream, cs)) {
      return fromWKT(reader, geometryFactory);

    } catch (NullPointerException n) {
      return null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Creates a coordinate.
   *
   * @param x the x value
   * @param y the y value
   * @return the coordinate
   */
  public static Coordinate createCoordinate(final double x, final double y) {
    return new Coordinate(x, y);
  }

  /**
   * Creates a coordinate.
   *
   * @param x the x value
   * @param y the y value
   * @return the coordinate
   * @throws IllegalArgumentException if x or y is {@code null}
   */
  public static Coordinate createCoordinate(final BigDecimal x, final BigDecimal y) {

    return Optional.ofNullable(x)
        .map(xx -> Optional.ofNullable(y)
            .map(yy -> new Coordinate(xx.doubleValue(), yy.doubleValue()))
            .orElseThrow(() -> new IllegalArgumentException("Y must not be null.")))
        .orElseThrow(() -> new IllegalArgumentException("X must not be null."));
  }

  /**
   * Creates a coordinate.
   *
   * @param latitude the latitude value
   * @param longitude the longitude value
   * @return the coordinate
   */
  public static Coordinate createCoordinateWGS84(final double latitude, final double longitude) {
    return new Coordinate(longitude, latitude);
  }

  /**
   * Creates a coordinate.
   *
   * @param latitude the latitude value
   * @param longitude the longitude value
   * @return the coordinate
   * @throws IllegalArgumentException if latitude or longitude is {@code null}
   */
  public static Coordinate createCoordinateWGS84(
      final BigDecimal latitude, final BigDecimal longitude) {

    return Optional.ofNullable(latitude)
        .map(lat -> Optional.ofNullable(longitude)
            .map(lon -> new Coordinate(lon.doubleValue(), lat.doubleValue()))
            .orElseThrow(() -> new IllegalArgumentException("Longitude must not be null.")))
        .orElseThrow(() -> new IllegalArgumentException("Latitude must not be null."));
  }

  /**
   * Gets latitude value of WGS84.
   *
   * @param coordinate the coordinate
   * @return the latitude
   */
  public static double getLatitudeWGS84(Coordinate coordinate) {
    return Optional.ofNullable(coordinate)
        .map(Coordinate::getY)
        .orElseThrow(() -> new IllegalArgumentException("Coordinate must not be null."));
  }

  /**
   * Gets longitude value of WGS84.
   *
   * @param coordinate the coordinate
   * @return the longitude
   */
  public static double getLongitudeWGS84(final Coordinate coordinate) {
    return Optional.ofNullable(coordinate)
        .map(Coordinate::getX)
        .orElseThrow(() -> new IllegalArgumentException("Coordinate must not be null."));
  }

  /**
   * Creates a point.
   *
   * @param x the x value
   * @param y the y value
   * @return the point
   */
  public static Point createPoint(final double x, final double y) {
    return createPoint(createCoordinate(x, y), null);
  }

  /**
   * Creates a point.
   *
   * @param x the x value
   * @param y the y value
   * @param geometryFactory the geometry factory to use
   * @return the point
   */
  public static Point createPoint(
      final double x,
      final double y,
      final GeometryFactory geometryFactory) {
    return createPoint(createCoordinate(x, y), geometryFactory);
  }

  /**
   * Creates a point.
   *
   * @param x the x value
   * @param y the y value
   * @return the point
   */
  public static Point createPoint(final BigDecimal x, final BigDecimal y) {
    return createPoint(createCoordinate(x, y), null);
  }

  /**
   * Creates a point.
   *
   * @param x the x value
   * @param y the y value
   * @param geometryFactory the geometry factory to use
   * @return the point
   */
  public static Point createPoint(
      final BigDecimal x,
      final BigDecimal y,
      final GeometryFactory geometryFactory) {
    return createPoint(createCoordinate(x, y), geometryFactory);
  }

  /**
   * Creates a Point using the given Coordinate; a null Coordinate will create an empty Geometry.
   *
   * @param coordinate the coordinate of the point
   * @return the point
   */
  public static Point createPoint(final Coordinate coordinate) {
    return createPoint(coordinate, null);
  }

  /**
   * Creates a Point using the given Coordinate; a null Coordinate will create an empty Geometry.
   *
   * @param coordinate the coordinate of the point
   * @param geometryFactory the geometry factory to use
   * @return the point
   */
  public static Point createPoint(
      final Coordinate coordinate,
      final GeometryFactory geometryFactory) {
    return geometryFactory(geometryFactory).createPoint(coordinate);
  }

  /**
   * Creates a point from WGS84 latitude and longitude.<br> Latitude becomes the y-value.<br>
   * Longitude becomes the x-value.<br>
   *
   * @param latitude the latitude in degrees
   * @param longitude the longitude in degrees
   * @return the point
   */
  public static Point createPointWGS84(final double latitude, final double longitude) {
    return createPoint(longitude, latitude, null);
  }

  /**
   * Creates a point from WGS84 latitude and longitude.<br> Latitude becomes the y-value.<br>
   * Longitude becomes the x-value.<br>
   *
   * @param latitude the latitude in degrees
   * @param longitude the longitude in degrees
   * @return the point
   */
  public static Point createPointWGS84(final BigDecimal latitude, final BigDecimal longitude) {
    return createPoint(longitude, latitude, null);
  }

  /**
   * Creates a point from WGS84 latitude and longitude.<br> Latitude becomes the y-value.<br>
   * Longitude becomes the x-value.<br>
   *
   * @param latitude the latitude in degrees
   * @param longitude the longitude in degrees
   * @param geometryFactory the geometry factory to use
   * @return the point
   */
  public static Point createPointWGS84(
      final double latitude,
      final double longitude,
      final GeometryFactory geometryFactory) {
    return createPoint(longitude, latitude, geometryFactory);
  }

  /**
   * Creates a point from WGS84 latitude and longitude.<br> Latitude becomes the y-value.<br>
   * Longitude becomes the x-value.<br>
   *
   * @param latitude the latitude in degrees
   * @param longitude the longitude in degrees
   * @param geometryFactory the geometry factory to use
   * @return the point
   */
  public static Point createPointWGS84(
      final BigDecimal latitude,
      final BigDecimal longitude,
      final GeometryFactory geometryFactory) {
    return createPoint(longitude, latitude, geometryFactory);
  }

  /**
   * Creates a MultiPoint using the given Points. A null or empty collection will create an empty
   * MultiPoint.
   *
   * @param points the points of the {@link MultiPoint}
   * @return the {@link MultiPoint}
   */
  public static MultiPoint createMultiPoint(final Collection<? extends Point> points) {
    return createMultiPoint(points, null);
  }

  /**
   * Creates a MultiPoint using the given Points. A null or empty collection will create an empty
   * MultiPoint.
   *
   * @param points the points of the {@link MultiPoint}
   * @param geometryFactory the geometry factory to use
   * @return the {@link MultiPoint}
   */
  public static MultiPoint createMultiPoint(
      final Collection<? extends Point> points,
      final GeometryFactory geometryFactory) {

    return Optional.ofNullable(points)
        .map(c -> c.toArray(new Point[0]))
        .map(a -> geometryFactory(geometryFactory).createMultiPoint(a))
        .orElseGet(() -> geometryFactory(geometryFactory).createMultiPoint());
  }

  /**
   * Creates a LineString using the given coordinates; a null or empty collection will create an
   * empty LineString. Consecutive points must not be equal.
   *
   * @param coordinates the coordinates of the {@link LineString}
   * @return the {@link LineString}
   */
  public static LineString createLineString(final Collection<? extends Coordinate> coordinates) {
    return createLineString(coordinates, null);
  }

  /**
   * Creates a LineString using the given coordinates; a null or empty collection will create an
   * empty LineString. Consecutive points must not be equal.
   *
   * @param coordinates the coordinates of the {@link LineString}
   * @param geometryFactory the geometry factory to use
   * @return the {@link LineString}
   */
  public static LineString createLineString(
      final Collection<? extends Coordinate> coordinates,
      final GeometryFactory geometryFactory) {

    return Optional.ofNullable(coordinates)
        .map(c -> c.toArray(new Coordinate[0]))
        .map(a -> geometryFactory(geometryFactory).createLineString(a))
        .orElseGet(() -> geometryFactory(geometryFactory).createLineString());
  }

  /**
   * Creates a MultiLineString using the given LineStrings; a null or empty collection will create
   * an empty MultiLineString.
   *
   * @param lineStrings the {@link LineString}s of the {@link MultiLineString}
   * @return the {@link MultiLineString}
   */
  public static MultiLineString createMultiLineString(
      final Collection<? extends LineString> lineStrings) {
    return createMultiLineString(lineStrings, null);
  }

  /**
   * Creates a MultiLineString using the given LineStrings; a null or empty collection will create
   * an empty MultiLineString.
   *
   * @param lineStrings the {@link LineString}s of the {@link MultiLineString}
   * @param geometryFactory the geometry factory to use
   * @return the {@link MultiLineString}
   */
  public static MultiLineString createMultiLineString(
      final Collection<? extends LineString> lineStrings,
      final GeometryFactory geometryFactory) {

    return Optional.ofNullable(lineStrings)
        .map(c -> c.toArray(new LineString[0]))
        .map(a -> geometryFactory(geometryFactory).createMultiLineString(a))
        .orElseGet(() -> geometryFactory(geometryFactory).createMultiLineString());
  }

  /**
   * Creates a LinearRing using the given coordinates. A null or empty coordinates will create an
   * empty LinearRing.
   *
   * @param coordinates the coordinates
   * @return the created LinearRing
   */
  public static LinearRing createLinearRing(final Collection<? extends Coordinate> coordinates) {
    return createLinearRing(coordinates, null);
  }

  /**
   * Creates a LinearRing using the given coordinates. A null or empty coordinates will create an
   * empty LinearRing.
   *
   * @param coordinates the coordinates
   * @param geometryFactory the geometry factory
   * @return the created LinearRing
   */
  public static LinearRing createLinearRing(
      final Collection<? extends Coordinate> coordinates,
      final GeometryFactory geometryFactory) {

    return Optional.ofNullable(coordinates)
        .map(c -> c.toArray(new Coordinate[0]))
        .map(a -> geometryFactory(geometryFactory).createLinearRing(a))
        .orElseGet(() -> geometryFactory(geometryFactory).createLinearRing());
  }

  /**
   * Constructs a Polygon with the given exterior boundary.
   *
   * @param shell the outer boundary of the new Polygon, or null or an empty LinearRing if the
   *     empty geometry is to be created
   * @return the created Polygon
   */
  public static Polygon createPolygon(final LinearRing shell) {
    return createPolygon(shell, null, null);
  }

  /**
   * Constructs a Polygon with the given exterior boundary.
   *
   * @param shell the outer boundary of the new Polygon, or null or an empty LinearRing if the
   *     empty geometry is to be created
   * @param geometryFactory the geometry factory
   * @return the created Polygon
   */
  public static Polygon createPolygon(
      final LinearRing shell,
      final GeometryFactory geometryFactory) {
    return createPolygon(shell, null, geometryFactory);
  }

  /**
   * Constructs a Polygon with the given exterior boundary and interior boundaries.
   *
   * @param shell the outer boundary of the new Polygon, or null or an empty LinearRing if the
   *     empty geometry is to be created
   * @param holes the inner boundaries of the new Polygon, or null or empty LinearRing s if the
   *     empty geometry is to be created
   * @return the created Polygon
   */
  public static Polygon createPolygon(
      final LinearRing shell,
      final Collection<? extends LinearRing> holes) {
    return createPolygon(shell, holes, null);
  }

  /**
   * Constructs a Polygon with the given exterior boundary and interior boundaries.
   *
   * @param shell the outer boundary of the new Polygon, or null or an empty LinearRing if the
   *     empty geometry is to be created
   * @param holes the inner boundaries of the new Polygon, or null or empty LinearRing s if the
   *     empty geometry is to be created
   * @param geometryFactory the geometry factory
   * @return the created Polygon
   */
  public static Polygon createPolygon(
      final LinearRing shell,
      final Collection<? extends LinearRing> holes,
      final GeometryFactory geometryFactory) {

    return Optional.ofNullable(shell)
        .map(s -> Optional.ofNullable(holes)
            .map(c -> c.toArray(new LinearRing[0]))
            .map(a -> geometryFactory(geometryFactory).createPolygon(s, a))
            .orElseGet(() -> geometryFactory(geometryFactory).createPolygon(s)))
        .orElseGet(() -> geometryFactory(geometryFactory).createPolygon());
  }

  /**
   * Creates a MultiPolygon using the given Polygons; a null or empty array will create an empty
   * Polygon.
   *
   * @param polygons the polygons
   * @return the multi polygon
   */
  public static MultiPolygon createMultiPolygon(final Collection<? extends Polygon> polygons) {
    return createMultiPolygon(polygons, null);
  }

  /**
   * Creates a MultiPolygon using the given Polygons; a null or empty array will create an empty
   * Polygon.
   *
   * @param polygons the polygons
   * @param geometryFactory the geometry factory
   * @return the multi polygon
   */
  public static MultiPolygon createMultiPolygon(
      final Collection<? extends Polygon> polygons,
      final GeometryFactory geometryFactory) {

    return Optional.ofNullable(polygons)
        .map(c -> c.toArray(new Polygon[0]))
        .map(a -> geometryFactory(geometryFactory).createMultiPolygon(a))
        .orElseGet(() -> geometryFactory(geometryFactory).createMultiPolygon());
  }

  /**
   * Create geometry collection geometry collection.
   *
   * @param geometries the geometries
   * @return the geometry collection
   */
  public static GeometryCollection createGeometryCollection(final Geometry... geometries) {
    return Optional.ofNullable(geometries)
        .map(DEFAULT_GEOMETRY_FACTORY::createGeometryCollection)
        .orElseGet(DEFAULT_GEOMETRY_FACTORY::createGeometryCollection);
  }

  /**
   * Create geometry collection geometry collection.
   *
   * @param geometries the geometries
   * @return the geometry collection
   */
  public static GeometryCollection createGeometryCollection(
      final Collection<? extends Geometry> geometries) {
    return createGeometryCollection(geometries, DEFAULT_GEOMETRY_FACTORY);
  }

  /**
   * Create geometry collection geometry collection.
   *
   * @param geometries the geometries
   * @param geometryFactory the geometry factory
   * @return the geometry collection
   */
  public static GeometryCollection createGeometryCollection(
      final Collection<? extends Geometry> geometries,
      final GeometryFactory geometryFactory) {

    return Optional.ofNullable(geometries)
        .map(g -> g.toArray(new Geometry[0]))
        .map(a -> geometryFactory(geometryFactory).createGeometryCollection(a))
        .orElseGet(() -> geometryFactory(geometryFactory).createGeometryCollection());
  }

  /**
   * Copy and apply filters.
   *
   * @param geometry the geometry
   * @param filters the filters
   * @return the copied and filtered geometry
   */
  public static Geometry copyAndApplyFilters(
      final Geometry geometry,
      final CoordinateFilter... filters) {
    return Optional.ofNullable(geometry)
        .map(g -> {
          final Geometry result = g.copy();
          if (filters != null) {
            for (CoordinateFilter filter : filters) {
              result.apply(filter);
            }
          }
          return result;
        })
        .orElse(null);
  }

  /**
   * Transforms the coordinates of the given geometry from WGS84 into mercator.
   *
   * @param geometry the geometry
   * @return the transformed (cloned) geometry
   */
  public static Geometry transformWgs84ToMercator(final Geometry geometry) {
    return copyAndApplyFilters(geometry, new Wgs84ToMercatorCoordinateFilter());
  }

  /**
   * Transforms the coordinates of the given geometry from mercator into WGS84.
   *
   * @param geometry the geometry
   * @return the transformed (cloned) geometry
   */
  public static Geometry transformMercatorToWgs84(final Geometry geometry) {
    return copyAndApplyFilters(geometry, new MercatorToWgs84CoordinateFilter());
  }

  /**
   * Swap coordinates.
   *
   * @param geometry the geometry
   * @return the geometry with swapped coordinates
   */
  public static Geometry swapCoordinates(final Geometry geometry) {
    return copyAndApplyFilters(geometry, new SwapCoordinateFilter());
  }

}

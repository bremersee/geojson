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

package org.bremersee.geojson.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import org.bremersee.geojson.GeoJsonFeature;
import org.bremersee.geojson.GeoJsonObjectMapperModule;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
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
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

/**
 * Utility methods for geometry objects.
 *
 * @author Christian Bremer
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class GeometryUtils {

  public static void main(String[] args) throws Exception {
    ObjectMapper om = new ObjectMapper();
    om = om.registerModule(new GeoJsonObjectMapperModule());
    GeoJsonFeature f = new GeoJsonFeature();
    f.setId("Hallo");
    String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(f);
    System.out.println(json);

    GeoJsonFeature fn = om.readValue(json, GeoJsonFeature.class);
  }

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

  /**
   * Checks whether two geometry objects are equal.
   *
   * <p>Because the {@link GeometryCollection#equals(Geometry)} method throws an exception, this
   * method is used in the GeoJSON classes.
   *
   * @param g1 one geometry
   * @param g2 another geometry
   * @return <code>true</code> if the geometries are equal otherwise
   * <code>false</code>
   */
  public static boolean equals(final Geometry g1, final Geometry g2) { // NOSONAR
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
    } catch (Throwable t) { // NOSONAR
      return false;
    }
  }

  /**
   * Checks whether two geometry collections are equal.
   *
   * @param gc1 one geometry collection
   * @param gc2 another geometry collection
   * @return <code>true</code> if the geometry collections are equal otherwise
   * <code>false</code>
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
   * @param geometry the geometry
   * @return <code>null</code> if the bounding box can not be calculated,
   * otherwise the bounding box of the geometry
   */
  public static double[] getBoundingBox(final Geometry geometry) {
    if (geometry == null) {
      return null;
    }
    final int dimension = 2;
    double[] values = new double[2 * (dimension + 1)];
    Coordinate[] coords = geometry.getCoordinates();
    if (coords == null || coords.length == 0) {
      return null;
    }
    Coordinate c0 = coords[0];
    for (int dim = 0; dim <= dimension; dim++) {
      values[dim] = c0.getOrdinate(dim);
      values[dim + dimension + 1] = c0.getOrdinate(dim);
    }
    for (int i = 1; i < coords.length; i++) {
      for (int dim = 0; dim <= dimension; dim++) {
        double value = coords[i].getOrdinate(dim);
        values[dim] = Math.min(value, values[dim]);
        values[dim + dimension + 1] = Math.max(value, values[dim + dimension + 1]);
      }
    }
    return values;
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
    return createCoordinate(boundingBox[0], boundingBox[1]); // southWest
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
      // 0 1 2 3 4 5
      // x0, y0, z0, x1, y1, z1
      return createCoordinate(boundingBox[0], boundingBox[4]);
    } else {
      // 0 1 2 3
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
      // 0 1 2 3 4 5
      // x0, y0, z0, x1, y1, z1
      return createCoordinate(boundingBox[3], boundingBox[4]);
    } else {
      // 0 1 2 3
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
      // 0 1 2 3 4 5
      // x0, y0, z0, x1, y1, z1
      return createCoordinate(boundingBox[3], boundingBox[1]);
    } else {
      // 0 1 2 3
      // x0, y0, x1, y1
      return createCoordinate(boundingBox[2], boundingBox[1]);
    }
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
  @SuppressWarnings("SameParameterValue")
  public static Polygon getBoundingBoxAsPolygon2D(
      final Geometry geometry,
      final GeometryFactory geometryFactory) {

    final double[] bbox = getBoundingBox(geometry);
    if (bbox == null || !(bbox.length == 4 || bbox.length == 6)) {
      return null;
    }

    int add = bbox.length == 6 ? 1 : 0;
    Double a = bbox[0];
    Double b = bbox[2 + add];
    Double c = bbox[1];
    Double d = bbox[3 + add];
    if (a.equals(b) || c.equals(d)) {
      return null;
    }

    Coordinate c0 = getSouthWest(bbox);
    if (c0 == null) {
      throw new IllegalArgumentException("South west coordinate is null.");
    }
    Coordinate c1 = getNorthWest(bbox);
    Coordinate c2 = getNorthEast(bbox);
    Coordinate c3 = getSouthEast(bbox);

    final GeometryFactory gf = geometryFactory == null ? DEFAULT_GEOMETRY_FACTORY : geometryFactory;
    return gf.createPolygon(new Coordinate[]{c0, c1, c2, c3, (Coordinate) c0.clone()});
  }

  /**
   * Returns the Well-known Text representation of this Geometry. For a definition of the Well-known
   * Text format, see the OpenGIS Simple Features Specification.
   *
   * @param geometry the geometry
   * @return the Well-known Text representation of this Geometry
   */
  public static String toWKT(final Geometry geometry) {
    if (geometry == null) {
      return null;
    }
    return geometry.toText();
  }

  /**
   * Reads a Well-Known Text representation of a Geometry from a {@link String}.
   *
   * @param wkt one or more strings (see the OpenGIS Simple Features Specification) separated by
   * whitespace
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
   * whitespace
   * @param geometryFactory the geometry factory to use
   * @return a Geometry specified by wellKnownText
   * @throws RuntimeException if a parsing problem occurs
   */
  @SuppressWarnings("SameParameterValue")
  public static Geometry fromWKT(final String wkt, final GeometryFactory geometryFactory)
      throws RuntimeException {

    if (wkt == null || wkt.trim().length() == 0) {
      return null;
    }
    final GeometryFactory gf = geometryFactory == null ? DEFAULT_GEOMETRY_FACTORY : geometryFactory;
    WKTReader wktReader = new WKTReader(gf);
    try {
      return wktReader.read(wkt);
    } catch (ParseException e) {
      throw new RuntimeException("Parsing WKT [" + wkt + "] failed.", e);
    }
  }

  /**
   * Reads a Well-Known Text representation of a Geometry from a {@link Reader}.
   *
   * @param reader a {@link Reader} which will return a string (see the OpenGIS Simple Features
   * Specification)
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
   * Specification)
   * @param geometryFactory the geometry factory to use
   * @return a Geometry read from reader
   * @throws RuntimeException if a parsing problem occurs
   */
  @SuppressWarnings("SameParameterValue")
  public static Geometry fromWKT(final Reader reader, final GeometryFactory geometryFactory)
      throws RuntimeException {

    if (reader == null) {
      return null;
    }
    final GeometryFactory gf = geometryFactory == null ? DEFAULT_GEOMETRY_FACTORY : geometryFactory;
    WKTReader wktReader = new WKTReader(gf);
    try {
      return wktReader.read(reader);
    } catch (ParseException e) {
      throw new RuntimeException("Parsing WKT failed.", e);
    }
  }

  /**
   * Reads a Well-Known Text representation of a Geometry from an {@link InputStream}.
   *
   * @param inputStream an {@link InputStream} which will return a string (see the OpenGIS Simple
   * Features Specification)
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
   * @param inputStream an {@link InputStream} which will return a string (see the OpenGIS Simple
   * Features Specification)
   * @param charsetName the charset to use
   * @param geometryFactory the geometry factory to use
   * @return a Geometry read from the input stream
   * @throws RuntimeException if a parsing problem occurs
   */
  @SuppressWarnings("SameParameterValue")
  public static Geometry fromWKT(final InputStream inputStream, final String charsetName,
      final GeometryFactory geometryFactory)
      throws RuntimeException {
    if (inputStream == null) {
      return null;
    }
    final String cn =
        charsetName == null || charsetName.trim().length() == 0 ? StandardCharsets.UTF_8.name()
            : charsetName;
    final GeometryFactory gf = geometryFactory == null ? DEFAULT_GEOMETRY_FACTORY : geometryFactory;
    try {
      return fromWKT(new InputStreamReader(inputStream, cn), gf);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Parsing WKT failed.", e);
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
    if (x == null) {
      throw new IllegalArgumentException("X must not be null.");
    }
    if (y == null) {
      throw new IllegalArgumentException("Y must not be null.");
    }
    return new Coordinate(x.doubleValue(), y.doubleValue());
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
    if (latitude == null) {
      throw new IllegalArgumentException("Latitude must not be null.");
    }
    if (longitude == null) {
      throw new IllegalArgumentException("Longitude must not be null.");
    }
    return new Coordinate(longitude.doubleValue(), latitude.doubleValue());
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
  public static Point createPoint(final double x, final double y,
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
  public static Point createPoint(final BigDecimal x, final BigDecimal y,
      final GeometryFactory geometryFactory) {
    return createPoint(createCoordinate(x, y), geometryFactory);
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
  public static Point createPointWGS84(final double latitude, final double longitude,
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
  public static Point createPointWGS84(final BigDecimal latitude, final BigDecimal longitude,
      final GeometryFactory geometryFactory) {
    return createPoint(longitude, latitude, geometryFactory);
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
  @SuppressWarnings("SameParameterValue")
  public static Point createPoint(final Coordinate coordinate,
      final GeometryFactory geometryFactory) {
    final GeometryFactory gf = geometryFactory == null ? DEFAULT_GEOMETRY_FACTORY : geometryFactory;
    return gf.createPoint(coordinate);
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
  @SuppressWarnings("SameParameterValue")
  public static MultiPoint createMultiPoint(final Collection<? extends Point> points,
      final GeometryFactory geometryFactory) {
    Point[] ps;
    if (points == null) {
      ps = null;
    } else {
      ps = points.toArray(new Point[0]);
    }
    final GeometryFactory gf = geometryFactory == null ? DEFAULT_GEOMETRY_FACTORY : geometryFactory;
    return gf.createMultiPoint(ps);
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
  public static LineString createLineString(final Collection<? extends Coordinate> coordinates,
      @SuppressWarnings("SameParameterValue") final GeometryFactory geometryFactory) {
    CoordinateSequence points;
    if (coordinates == null) {
      points = null;
    } else {
      Coordinate[] coords = coordinates.toArray(new Coordinate[0]);
      points = CoordinateArraySequenceFactory.instance().create(coords);
    }
    final GeometryFactory gf = geometryFactory == null ? DEFAULT_GEOMETRY_FACTORY : geometryFactory;
    return gf.createLineString(points);
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
      @SuppressWarnings("SameParameterValue") final GeometryFactory geometryFactory) {
    LineString[] lines;
    if (lineStrings == null) {
      lines = null;
    } else {
      lines = lineStrings.toArray(new LineString[0]);
    }
    final GeometryFactory gf = geometryFactory == null ? DEFAULT_GEOMETRY_FACTORY : geometryFactory;
    return gf.createMultiLineString(lines);
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
  public static LinearRing createLinearRing(final Collection<? extends Coordinate> coordinates,
      @SuppressWarnings("SameParameterValue") final GeometryFactory geometryFactory) {
    CoordinateSequence points;
    if (coordinates == null) {
      points = null;
    } else {
      Coordinate[] coords = coordinates.toArray(new Coordinate[0]);
      if (coords.length > 0) {
        Coordinate coord0 = coords[0];
        Coordinate coordN = coords[coords.length - 1];
        if (!coord0.equals3D(coordN)) {
          coords = Arrays.copyOf(coords, coords.length + 1);
          coords[coords.length - 1] = (Coordinate) coord0.clone();
        }
      }
      points = CoordinateArraySequenceFactory.instance().create(coords);
    }
    final GeometryFactory gf = geometryFactory == null ? DEFAULT_GEOMETRY_FACTORY : geometryFactory;
    return gf.createLinearRing(points);
  }

  /**
   * Constructs a Polygon with the given exterior boundary.
   *
   * @param shell the outer boundary of the new Polygon, or null or an empty LinearRing if the empty
   * geometry is to be created
   * @return the created Polygon
   */
  public static Polygon createPolygon(final LinearRing shell) {
    return createPolygon(shell, null, null);
  }

  /**
   * Constructs a Polygon with the given exterior boundary.
   *
   * @param shell the outer boundary of the new Polygon, or null or an empty LinearRing if the empty
   * geometry is to be created
   * @param geometryFactory the geometry factory
   * @return the created Polygon
   */
  public static Polygon createPolygon(final LinearRing shell,
      final GeometryFactory geometryFactory) {
    return createPolygon(shell, null, geometryFactory);
  }

  /**
   * Constructs a Polygon with the given exterior boundary and interior boundaries.
   *
   * @param shell the outer boundary of the new Polygon, or null or an empty LinearRing if the empty
   * geometry is to be created
   * @param holes the inner boundaries of the new Polygon, or null or empty LinearRing s if the
   * empty geometry is to be created
   * @return the created Polygon
   */
  public static Polygon createPolygon(final LinearRing shell,
      final Collection<? extends LinearRing> holes) {
    return createPolygon(shell, holes, null);
  }

  /**
   * Constructs a Polygon with the given exterior boundary and interior boundaries.
   *
   * @param shell the outer boundary of the new Polygon, or null or an empty LinearRing if the empty
   * geometry is to be created
   * @param holes the inner boundaries of the new Polygon, or null or empty LinearRing s if the
   * empty geometry is to be created
   * @param geometryFactory the geometry factory
   * @return the created Polygon
   */
  public static Polygon createPolygon(final LinearRing shell,
      final Collection<? extends LinearRing> holes,
      final GeometryFactory geometryFactory) {
    LinearRing[] hs;
    if (holes == null) {
      hs = null;
    } else {
      hs = holes.toArray(new LinearRing[0]);
    }
    final GeometryFactory gf = geometryFactory == null ? DEFAULT_GEOMETRY_FACTORY : geometryFactory;
    return gf.createPolygon(shell, hs);
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
  public static MultiPolygon createMultiPolygon(final Collection<? extends Polygon> polygons,
      @SuppressWarnings("SameParameterValue") final GeometryFactory geometryFactory) {
    Polygon[] ps;
    if (polygons == null) {
      ps = null;
    } else {
      ps = polygons.toArray(new Polygon[0]);
    }
    final GeometryFactory gf = geometryFactory == null ? DEFAULT_GEOMETRY_FACTORY : geometryFactory;
    return gf.createMultiPolygon(ps);
  }

  /**
   * Transforms the coordinates of the given geometry from WGS84 into mercator.
   *
   * @param geometry the geometry
   * @return the transformed (cloned) geometry
   */
  public static Geometry transformWgs84ToMercator(final Geometry geometry) {
    if (geometry == null) {
      return null;
    }
    Geometry result = geometry.copy();
    result.apply(new Wgs84ToMercatorCoordinateFilter());
    return result;
  }

  /**
   * Transforms the coordinates of the given geometry from mercator into WGS84.
   *
   * @param geometry the geometry
   * @return the transformed (cloned) geometry
   */
  public static Geometry transformMercatorToWgs84(final Geometry geometry) {
    if (geometry == null) {
      return null;
    }
    Geometry result = geometry.copy();
    result.apply(new MercatorToWgs84CoordinateFilter());
    return result;
  }

}

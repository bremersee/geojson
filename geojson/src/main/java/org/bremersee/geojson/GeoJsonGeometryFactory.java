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

import static java.util.Objects.isNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import org.bremersee.geojson.model.LatLon;
import org.bremersee.geojson.model.LatLonAware;
import org.bremersee.geojson.model.LatitudeLongitude;
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
 * The geo json geometry factory.
 *
 * @author Christian Bremer
 */
public class GeoJsonGeometryFactory extends GeometryFactory {

  /**
   * Creates a coordinate.
   *
   * @param x the x value
   * @param y the y value
   * @return the coordinate
   */
  public static Coordinate createCoordinate(double x, double y) {
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
  public static Coordinate createCoordinate(BigDecimal x, BigDecimal y) {

    return Optional.ofNullable(x)
        .map(xx -> Optional.ofNullable(y)
            .map(yy -> new Coordinate(xx.doubleValue(), yy.doubleValue()))
            .orElseThrow(() -> new IllegalArgumentException("Y must not be null.")))
        .orElseThrow(() -> new IllegalArgumentException("X must not be null."));
  }

  /**
   * Create coordinate.
   *
   * @param latLon the lat lon
   * @return the coordinate
   */
  public static Coordinate createCoordinate(LatLonAware latLon) {
    if (isNull(latLon)) {
      return null;
    }
    return createCoordinate(latLon.getLongitude(), latLon.getLatitude());
  }

  /**
   * Create point.
   *
   * @param x the x
   * @param y the y
   * @return the point
   */
  public Point createPoint(double x, double y) {
    return createPoint(createCoordinate(x, y));
  }

  /**
   * Create point.
   *
   * @param x the x
   * @param y the y
   * @return the point
   */
  public Point createPoint(BigDecimal x, BigDecimal y) {
    return createPoint(createCoordinate(x, y));
  }

  /**
   * Create point.
   *
   * @param latLon the lat lon
   * @return the point
   */
  public Point createPoint(LatLonAware latLon) {
    if (isNull(latLon)) {
      return null;
    }
    return createPoint(createCoordinate(latLon));
  }

  /**
   * Creates a LineString using the given coordinates; a null or empty collection will create an
   * empty LineString. Consecutive points must not be equal.
   *
   * @param coordinates the coordinates of the {@link LineString}
   * @return the {@link LineString}
   */
  public LineString createLineString(Collection<? extends Coordinate> coordinates) {
    return Optional.ofNullable(coordinates)
        .map(c -> c.toArray(new Coordinate[0]))
        .map(this::createLineString)
        .orElseGet(this::createLineString);
  }

  /**
   * Creates a LinearRing using the given coordinates. A null or empty coordinates will create an
   * empty LinearRing.
   *
   * @param coordinates the coordinates
   * @return the created LinearRing
   */
  public LinearRing createLinearRing(Collection<? extends Coordinate> coordinates) {

    return Optional.ofNullable(coordinates)
        .map(c -> c.toArray(new Coordinate[0]))
        .map(this::createLinearRing)
        .orElseGet(this::createLinearRing);
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
  public Polygon createPolygon(
      LinearRing shell,
      Collection<? extends LinearRing> holes) {

    return Optional.ofNullable(shell)
        .map(s -> Optional.ofNullable(holes)
            .map(c -> c.toArray(new LinearRing[0]))
            .map(a -> createPolygon(s, a))
            .orElseGet(() -> createPolygon(s)))
        .orElseGet(this::createPolygon);
  }

  /**
   * Creates a MultiPoint using the given Points. A null or empty collection will create an empty
   * MultiPoint.
   *
   * @param points the points of the {@link MultiPoint}
   * @return the {@link MultiPoint}
   */
  public MultiPoint createMultiPoint(Collection<? extends Point> points) {
    return Optional.ofNullable(points)
        .map(c -> c.toArray(new Point[0]))
        .map(this::createMultiPoint)
        .orElseGet(this::createMultiPoint);
  }

  /**
   * Creates a MultiLineString using the given LineStrings; a null or empty collection will create
   * an empty MultiLineString.
   *
   * @param lineStrings the {@link LineString}s of the {@link MultiLineString}
   * @return the {@link MultiLineString}
   */
  public MultiLineString createMultiLineString(Collection<? extends LineString> lineStrings) {

    return Optional.ofNullable(lineStrings)
        .map(c -> c.toArray(new LineString[0]))
        .map(this::createMultiLineString)
        .orElseGet(this::createMultiLineString);
  }

  /**
   * Creates a MultiPolygon using the given Polygons; a null or empty array will create an empty
   * Polygon.
   *
   * @param polygons the polygons
   * @return the multi polygon
   */
  public MultiPolygon createMultiPolygon(Collection<? extends Polygon> polygons) {

    return Optional.ofNullable(polygons)
        .map(c -> c.toArray(new Polygon[0]))
        .map(this::createMultiPolygon)
        .orElseGet(this::createMultiPolygon);
  }

  /**
   * Create geometry collection.
   *
   * @param geometries the geometries
   * @return the geometry collection
   */
  public GeometryCollection createGeometryCollection(Collection<? extends Geometry> geometries) {

    return Optional.ofNullable(geometries)
        .map(g -> g.toArray(new Geometry[0]))
        .map(this::createGeometryCollection)
        .orElseGet(this::createGeometryCollection);
  }

  /**
   * Create lat lon.
   *
   * @param coordinate the coordinate
   * @return the lat lon
   */
  public static LatLon createLatLon(Coordinate coordinate) {
    if (isNull(coordinate)) {
      return null;
    }
    return new LatLon(
        BigDecimal.valueOf(coordinate.getY()),
        BigDecimal.valueOf(coordinate.getX()));
  }

  /**
   * Create lat lon.
   *
   * @param point the point
   * @return the lat lon
   */
  public static LatLon createLatLon(Point point) {
    if (isNull(point)) {
      return null;
    }
    return createLatLon(point.getCoordinate());
  }

  /**
   * Create latitude longitude.
   *
   * @param coordinate the coordinate
   * @return the latitude longitude
   */
  public static LatitudeLongitude createLatitudeLongitude(Coordinate coordinate) {
    if (isNull(coordinate)) {
      return null;
    }
    return new LatitudeLongitude(
        BigDecimal.valueOf(coordinate.getY()),
        BigDecimal.valueOf(coordinate.getX()));
  }

  /**
   * Create latitude longitude.
   *
   * @param point the point
   * @return the latitude longitude
   */
  public static LatitudeLongitude createLatitudeLongitude(Point point) {
    if (isNull(point)) {
      return null;
    }
    return createLatitudeLongitude(point.getCoordinate());
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
  public static boolean equals(Geometry g1, Geometry g2) {
    if (isNull(g1) && isNull(g2)) {
      return true;
    }
    if (isNull(g1) || isNull(g2)) {
      return false;
    }
    if (g1 == g2) {
      return true;
    }
    if (g1 instanceof GeometryCollection && g2 instanceof GeometryCollection) {
      return equals((GeometryCollection) g1, (GeometryCollection) g2);
    }
    if (g1 instanceof GeometryCollection || g2 instanceof GeometryCollection) {
      return false;
    }
    return g1.equals(g2);
  }

  /**
   * Checks whether two geometry collections are equal.
   *
   * @param gc1 one geometry collection
   * @param gc2 another geometry collection
   * @return {@code true} if the geometry collections are equal otherwise {@code false}
   */
  private static boolean equals(GeometryCollection gc1, GeometryCollection gc2) {
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
  public static double[] getBoundingBox(Geometry geometry) {
    return Optional.ofNullable(geometry)
        .map(g -> getBoundingBox(Collections.singletonList(g)))
        .orElse(null);
  }

  /**
   * Calculate the bounding box of the specified geometries.
   *
   * @param geometries the geometries
   * @return {@code null} if the bounding box can not be calculated, otherwise the bounding box
   */
  public static double[] getBoundingBox(Collection<? extends Geometry> geometries) {
    if (isNull(geometries) || geometries.isEmpty()) {
      return null;
    }
    double minX = Double.NaN;
    double minY = Double.NaN;
    double minZ = Double.NaN;
    double maxX = Double.NaN;
    double maxY = Double.NaN;
    double maxZ = Double.NaN;
    for (Geometry geometry : geometries) {
      if (geometry != null && geometry.getCoordinates() != null) {
        Coordinate[] coords = geometry.getCoordinates();
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
  public static Coordinate getSouthWest(double[] boundingBox) {
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
  public static Coordinate getNorthWest(double[] boundingBox) {
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
  public static Coordinate getNorthEast(double[] boundingBox) {
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
  public static Coordinate getSouthEast(double[] boundingBox) {
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
  public Polygon getBoundingBoxAsPolygon2D(double[] boundingBox) {

    Coordinate sw = getSouthWest(boundingBox);
    Coordinate se = getSouthEast(boundingBox);
    Coordinate ne = getNorthEast(boundingBox);
    Coordinate nw = getNorthWest(boundingBox);
    if (isNull(sw) || isNull(se) || isNull(ne) || isNull(nw)) {
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
    return createPolygon(new Coordinate[]{sw, se, ne, nw, sw});
  }

  /**
   * Returns the bounding box of the geometry as polygon.
   *
   * @param geometry the geometry
   * @return the bounding box of the geometry as polygon
   */
  public Polygon getBoundingBoxAsPolygon2D(Geometry geometry) {
    return getBoundingBoxAsPolygon2D(getBoundingBox(geometry));
  }

  /**
   * Reads a Well-Known Text representation of a Geometry from a {@link String}.
   *
   * @param wkt one or more strings (see the OpenGIS Simple Features Specification) separated by
   *     whitespace
   * @return a Geometry specified by wellKnownText
   * @throws IllegalArgumentException if a parsing problem occurs
   */
  public Geometry createGeometryFromWellKnownText(String wkt) throws IllegalArgumentException {
    try {
      return new WKTReader(this).read(wkt);
    } catch (NullPointerException n) {
      return null;
    } catch (ParseException e) {
      throw new IllegalArgumentException(String.format("Parsing WKT [%s] failed.", wkt), e);
    }
  }

  /**
   * Reads a Well-Known Text representation of a Geometry from a {@link Reader}.
   *
   * @param reader a {@link Reader} which will return a string (see the OpenGIS Simple Features
   *     Specification)
   * @return a Geometry read from reader
   * @throws IllegalArgumentException if a parsing problem occurs
   * @throws IOException if io problems occurs
   */
  public Geometry createGeometryFromWellKnownText(Reader reader)
      throws IllegalArgumentException, IOException {

    try (Reader r = reader) {
      return new WKTReader(this).read(r);
    } catch (NullPointerException n) {
      return null;
    } catch (ParseException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Reads a Well-Known Text representation of a Geometry from an {@link InputStream}.
   *
   * @param inputStream an {@link InputStream} which will return a string (see the OpenGIS
   *     Simple Features Specification)
   * @param charset the charset to use
   * @return a Geometry read from the input stream
   * @throws IllegalArgumentException if a parsing problem occurs
   * @throws IOException if io problems occurs
   */
  public Geometry createGeometryFromWellKnownText(
      InputStream inputStream,
      Charset charset) throws IllegalArgumentException, IOException {

    Charset cs = isNull(charset) ? StandardCharsets.UTF_8 : charset;
    try (InputStreamReader reader = new InputStreamReader(inputStream, cs)) {
      return new WKTReader(this).read(reader);

    } catch (NullPointerException n) {
      return null;
    } catch (ParseException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Copy and apply filters.
   *
   * @param geometry the geometry
   * @param filters the filters
   * @return the copied and filtered geometry
   */
  public static Geometry copyAndApplyFilters(
      Geometry geometry,
      CoordinateFilter... filters) {

    Geometry result;
    if (isNull(geometry) || isNull(filters) || filters.length == 0) {
      result = geometry;
    } else {
      result = geometry.copy();
      Arrays.stream(filters).forEach(result::apply);
    }
    return result;
  }

  /**
   * Copy and apply filters.
   *
   * @param geometry the geometry
   * @param filters the filters
   * @return the copied and filtered geometry
   */
  public static Geometry copyAndApplyFilters(
      Geometry geometry,
      Collection<? extends CoordinateFilter> filters) {

    Geometry result;
    if (isNull(geometry) || isNull(filters) || filters.isEmpty()) {
      result = geometry;
    } else {
      result = geometry.copy();
      filters.forEach(result::apply);
    }
    return result;
  }

}

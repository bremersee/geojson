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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
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

/**
 * The geometry utils test.
 *
 * @author Christian Bremer
 */
class GeometryUtilsTest {

  /**
   * Test equals.
   */
  @Test
  void testEquals() {
    Point g1 = GeometryUtils.createPoint(1.234, 5.678);
    Point g2 = GeometryUtils.createPoint(1.234, 5.678);
    Point g3 = GeometryUtils.createPoint(1.234, 5.679);
    assertTrue(GeometryUtils.equals(g1, g1));
    assertTrue(GeometryUtils.equals(g1, g2));
    assertFalse(GeometryUtils.equals(g1, g3));

    GeometryCollection gc1 = GeometryUtils.createGeometryCollection(g1, g3);
    GeometryCollection gc2 = GeometryUtils.createGeometryCollection(g1, g3);
    GeometryCollection gc3 = GeometryUtils.createGeometryCollection(g1, g2);
    GeometryCollection gc4 = GeometryUtils.createGeometryCollection(g1, g2, g3);
    assertTrue(GeometryUtils.equals(gc1, gc1));
    assertTrue(GeometryUtils.equals(gc1, gc2));
    assertFalse(GeometryUtils.equals(gc1, gc3));
    assertFalse(GeometryUtils.equals(gc1, gc4));

    assertTrue(GeometryUtils.equals(null, null));
    assertFalse(GeometryUtils.equals(gc1, null));
    assertFalse(GeometryUtils.equals(null, g1));
  }

  /**
   * Gets bounding box.
   */
  @Test
  void getBoundingBox() {
    assertNull(GeometryUtils.getBoundingBox((Geometry) null));
    assertNull(GeometryUtils.getBoundingBox((Collection<? extends Geometry>) null));

    assertNull(GeometryUtils.getBoundingBox(GeometryUtils.createPoint(Double.NaN, 1.)));
    assertNull(GeometryUtils.getBoundingBox(GeometryUtils.createPoint(1., Double.NaN)));

    Point p0 = GeometryUtils.createPoint(1., 20.);
    Point p1 = GeometryUtils.createPoint(10., 2.);
    Geometry geometry = GeometryUtils.createMultiPoint(Arrays.asList(p0, p1));
    double[] bounds = GeometryUtils.getBoundingBox(geometry);
    assertNotNull(bounds);
    assertEquals(4, bounds.length);
    // min x:
    assertEquals(1., bounds[0]);
    // min y:
    assertEquals(2., bounds[1]);
    // max x:
    assertEquals(10., bounds[2]);
    // max y:
    assertEquals(20., bounds[3]);

    Coordinate coordinate = GeometryUtils.getSouthWest(bounds);
    assertEquals(1., coordinate.x); // min x
    assertEquals(2., coordinate.y); // min y

    coordinate = GeometryUtils.getSouthEast(bounds);
    assertEquals(10., coordinate.x); // max x
    assertEquals(2., coordinate.y); // min y

    coordinate = GeometryUtils.getNorthEast(bounds);
    assertEquals(10., coordinate.x); // max x
    assertEquals(20., coordinate.y); // max y

    coordinate = GeometryUtils.getNorthWest(bounds);
    assertEquals(1., coordinate.x); // min x
    assertEquals(20., coordinate.y); // max y

    Polygon polygon = GeometryUtils.getBoundingBoxAsPolygon2D(bounds);
    assertNotNull(polygon);
    assertEquals(5, polygon.getCoordinates().length);
    assertEquals(polygon, GeometryUtils.getBoundingBoxAsPolygon2D(geometry));

    p0 = GeometryUtils.createPoint(new Coordinate(10., 2., 3.));
    p1 = GeometryUtils.createPoint(new Coordinate(1., 20., 30.));
    bounds = GeometryUtils.getBoundingBox(
        GeometryUtils.createMultiPoint(Arrays.asList(p0, p1)));
    assertNotNull(bounds);
    assertEquals(6, bounds.length);
    // min x:
    assertEquals(1., bounds[0]);
    // min y:
    assertEquals(2., bounds[1]);
    // min z:
    assertEquals(3., bounds[2]);
    // max x:
    assertEquals(10., bounds[3]);
    // max y:
    assertEquals(20., bounds[4]);
    // max z:
    assertEquals(30., bounds[5]);

    coordinate = GeometryUtils.getSouthWest(bounds);
    assertEquals(1., coordinate.x); // min x
    assertEquals(2., coordinate.y); // min y

    coordinate = GeometryUtils.getSouthEast(bounds);
    assertEquals(10., coordinate.x); // max x
    assertEquals(2., coordinate.y); // min y

    coordinate = GeometryUtils.getNorthEast(bounds);
    assertEquals(10., coordinate.x); // max x
    assertEquals(20., coordinate.y); // max y

    coordinate = GeometryUtils.getNorthWest(bounds);
    assertEquals(1., coordinate.x); // min x
    assertEquals(20., coordinate.y); // max y

    assertNull(GeometryUtils.getSouthEast(new double[0]));
    assertNull(GeometryUtils.getSouthWest(new double[0]));
    assertNull(GeometryUtils.getNorthEast(new double[0]));
    assertNull(GeometryUtils.getNorthWest(new double[0]));
  }

  /**
   * To and from wkt.
   */
  @Test
  void toAndFromWKT() {
    assertNull(GeometryUtils.toWKT(null));
    assertNull(GeometryUtils.fromWKT((String) null));
    assertNull(GeometryUtils.fromWKT((Reader) null));
    assertNull(GeometryUtils.fromWKT(null, StandardCharsets.UTF_8.name()));

    assertThrows(IllegalArgumentException.class, () -> GeometryUtils.fromWKT("nonsense"));
    assertThrows(IllegalArgumentException.class, () -> GeometryUtils
        .fromWKT(new StringReader("nonsense")));
    assertThrows(IllegalArgumentException.class, () -> GeometryUtils
        .fromWKT(new ByteArrayInputStream("nonsense".getBytes(StandardCharsets.UTF_8)), null));

    Geometry geometry = GeometryUtils.createPoint(1.1, 2.2);
    String wkt = GeometryUtils.toWKT(geometry);
    System.out.println("Point: " + wkt);
    assertTrue(GeometryUtils.equals(geometry, GeometryUtils.fromWKT(wkt)));
    assertTrue(GeometryUtils.equals(geometry, GeometryUtils.fromWKT(new StringReader(wkt))));
    assertTrue(GeometryUtils.equals(geometry, GeometryUtils.fromWKT(
        new ByteArrayInputStream(wkt.getBytes(StandardCharsets.UTF_8)),
        StandardCharsets.UTF_8.name())));

    geometry = GeometryUtils.createLineString(Arrays.asList(
        new Coordinate(1.1, 2.2),
        new Coordinate(5.5, 6.6)));
    wkt = GeometryUtils.toWKT(geometry);
    System.out.println("LineString: " + wkt);
    assertTrue(GeometryUtils.equals(geometry, GeometryUtils.fromWKT(wkt)));
  }

  /**
   * Create coordinate.
   */
  @Test
  void createCoordinate() {
    assertEquals(new Coordinate(1., 2.), GeometryUtils.createCoordinate(1., 2.));
    assertEquals(
        new Coordinate(1., 2.),
        GeometryUtils.createCoordinate(new BigDecimal("1"), new BigDecimal("2")));
    assertThrows(
        IllegalArgumentException.class,
        () -> GeometryUtils.createCoordinate(null, null));
    assertThrows(
        IllegalArgumentException.class,
        () -> GeometryUtils.createCoordinate(new BigDecimal("1"), null));
  }

  /**
   * Create coordinate wgs 84.
   */
  @Test
  void createCoordinateWGS84() {
    assertEquals(
        new Coordinate(2., 1.),
        GeometryUtils.createCoordinateWGS84(1., 2.));
    assertEquals(
        new Coordinate(2., 1.),
        GeometryUtils.createCoordinateWGS84(new BigDecimal("1"), new BigDecimal("2")));
    assertThrows(
        IllegalArgumentException.class,
        () -> GeometryUtils.createCoordinateWGS84(null, null));
    assertThrows(
        IllegalArgumentException.class,
        () -> GeometryUtils.createCoordinateWGS84(new BigDecimal("1"), null));
  }

  /**
   * Gets latitude wgs 84.
   */
  @Test
  void getLatitudeWGS84() {
    assertThrows(
        IllegalArgumentException.class,
        () -> GeometryUtils.getLatitudeWGS84(null));
    assertEquals(1., GeometryUtils.getLatitudeWGS84(new Coordinate(2., 1.)));
  }

  /**
   * Gets longitude wgs 84.
   */
  @Test
  void getLongitudeWGS84() {
    assertThrows(
        IllegalArgumentException.class,
        () -> GeometryUtils.getLongitudeWGS84(null));
    assertEquals(2., GeometryUtils.getLongitudeWGS84(new Coordinate(2., 1.)));
  }

  /**
   * Create point.
   */
  @Test
  void createPoint() {
    assertEquals(1., GeometryUtils.createPoint(1., 2.).getX());
    assertEquals(2., GeometryUtils.createPoint(1., 2.).getY());
    assertEquals(
        1.,
        GeometryUtils.createPoint(new BigDecimal("1"), new BigDecimal("2")).getX());
    assertEquals(
        2.,
        GeometryUtils.createPoint(new BigDecimal("1"), new BigDecimal("2")).getY());
    assertEquals(1., GeometryUtils.createPoint(new Coordinate(1., 2.)).getX());
    assertEquals(2., GeometryUtils.createPoint(new Coordinate(1., 2.)).getY());
  }

  /**
   * Create point wgs 84.
   */
  @Test
  void createPointWGS84() {
    assertEquals(2., GeometryUtils.createPointWGS84(1., 2.).getX());
    assertEquals(1., GeometryUtils.createPointWGS84(1., 2.).getY());
    assertEquals(
        2.,
        GeometryUtils.createPointWGS84(new BigDecimal("1"), new BigDecimal("2")).getX());
    assertEquals(
        1.,
        GeometryUtils.createPointWGS84(new BigDecimal("1"), new BigDecimal("2")).getY());

    assertEquals(2., GeometryUtils
        .createPointWGS84(1., 2., new GeometryFactory()).getX());
    assertEquals(1., GeometryUtils
        .createPointWGS84(1., 2., new GeometryFactory()).getY());
    assertEquals(
        2.,
        GeometryUtils.createPointWGS84(
            new BigDecimal("1"), new BigDecimal("2"), new GeometryFactory()).getX());
    assertEquals(
        1.,
        GeometryUtils.createPointWGS84(
            new BigDecimal("1"), new BigDecimal("2"), new GeometryFactory()).getY());
  }

  /**
   * Create multi point.
   */
  @Test
  void createMultiPoint() {
    Point g1 = GeometryUtils.createPoint(11.234, 15.678);
    Point g2 = GeometryUtils.createPoint(12.234, 16.678);
    Point g3 = GeometryUtils.createPoint(13.234, 17.679);
    MultiPoint geometry = GeometryUtils.createMultiPoint(Arrays.asList(g1, g2, g3));
    assertNotNull(geometry);
    assertEquals(3, geometry.getCoordinates().length);

    geometry = GeometryUtils.createMultiPoint(Collections.emptyList());
    assertNotNull(geometry);
    assertEquals(0, geometry.getCoordinates().length);

    geometry = GeometryUtils.createMultiPoint(null);
    assertNotNull(geometry);
    assertEquals(0, geometry.getCoordinates().length);
  }

  /**
   * Create line string.
   */
  @Test
  void createLineString() {
    Coordinate c0 = new Coordinate(123.456, 34.567);
    Coordinate c1 = new Coordinate(124.456, 35.567);
    Coordinate c2 = new Coordinate(125.456, 36.567);
    LineString geometry = GeometryUtils.createLineString(Arrays.asList(c0, c1, c2));
    assertNotNull(geometry);
    assertEquals(3, geometry.getCoordinates().length);

    geometry = GeometryUtils.createLineString(Collections.emptyList());
    assertNotNull(geometry);
    assertEquals(0, geometry.getCoordinates().length);

    geometry = GeometryUtils.createLineString(null);
    assertNotNull(geometry);
    assertEquals(0, geometry.getCoordinates().length);
  }

  /**
   * Create multi line string.
   */
  @Test
  void createMultiLineString() {
    Coordinate c0 = new Coordinate(123.456, 84.567);
    Coordinate c1 = new Coordinate(124.456, 85.567);
    Coordinate c2 = new Coordinate(125.456, 86.567);
    LineString ls0 = GeometryUtils.createLineString(Arrays.asList(c0, c1, c2));
    Coordinate c3 = new Coordinate(53.456, 44.567);
    Coordinate c4 = new Coordinate(54.456, 45.567);
    Coordinate c5 = new Coordinate(55.456, 46.567);
    LineString ls1 = GeometryUtils.createLineString(Arrays.asList(c3, c4, c5));
    MultiLineString geometry = GeometryUtils.createMultiLineString(Arrays.asList(ls0, ls1));
    assertNotNull(geometry);
    assertEquals(2, geometry.getNumGeometries());

    geometry = GeometryUtils.createMultiLineString(Collections.emptyList());
    assertNotNull(geometry);
    assertEquals(0, geometry.getCoordinates().length);

    geometry = GeometryUtils.createMultiLineString(null);
    assertNotNull(geometry);
    assertEquals(0, geometry.getCoordinates().length);
  }

  /**
   * Create linear ring.
   */
  @Test
  void createLinearRing() {
    Coordinate c0 = new Coordinate(3.456, 3.567);
    Coordinate c1 = new Coordinate(8.456, 3.567);
    Coordinate c2 = new Coordinate(8.456, 9.567);
    Coordinate c3 = new Coordinate(3.456, 10.567);
    LinearRing geometry = GeometryUtils.createLinearRing(Arrays.asList(c0, c1, c2, c3, c0));
    assertNotNull(geometry);
    assertEquals(5, geometry.getCoordinates().length);

    geometry = GeometryUtils.createLinearRing(Collections.emptyList());
    assertNotNull(geometry);
    assertEquals(0, geometry.getCoordinates().length);

    geometry = GeometryUtils.createLinearRing(null);
    assertNotNull(geometry);
    assertEquals(0, geometry.getCoordinates().length);
  }

  /**
   * Create polygon.
   */
  @Test
  void createPolygon() {
    Coordinate c0 = new Coordinate(3.456, 3.567);
    Coordinate c1 = new Coordinate(8.456, 3.567);
    Coordinate c2 = new Coordinate(8.456, 9.567);
    Coordinate c3 = new Coordinate(3.456, 10.567);
    LinearRing ring = GeometryUtils.createLinearRing(Arrays.asList(c0, c1, c2, c3, c0));
    Polygon geometry = GeometryUtils.createPolygon(ring);
    assertNotNull(geometry);
    assertEquals(5, geometry.getCoordinates().length);

    ring = GeometryUtils.createLinearRing(null);
    geometry = GeometryUtils.createPolygon(ring);
    assertNotNull(geometry);
    assertEquals(0, geometry.getCoordinates().length);

    geometry = GeometryUtils.createPolygon(null);
    assertNotNull(geometry);
    assertEquals(0, geometry.getCoordinates().length);
  }

  /**
   * Create polygon with holes.
   */
  @Test
  void createPolygonWithHoles() {
    Coordinate c0 = new Coordinate(3.456, 3.567);
    Coordinate c1 = new Coordinate(8.456, 3.567);
    Coordinate c2 = new Coordinate(8.456, 9.567);
    Coordinate c3 = new Coordinate(3.456, 10.567);
    LinearRing ring = GeometryUtils.createLinearRing(Arrays.asList(c0, c1, c2, c3, c0));

    Coordinate c00 = new Coordinate(4.456, 4.567);
    Coordinate c01 = new Coordinate(5.456, 4.567);
    Coordinate c02 = new Coordinate(5.456, 6.567);
    Coordinate c03 = new Coordinate(4.456, 6.567);
    LinearRing hole0 = GeometryUtils.createLinearRing(Arrays.asList(c00, c01, c02, c03, c00));

    Polygon geometry = GeometryUtils.createPolygon(ring, Collections.singletonList(hole0));
    assertNotNull(geometry);
    assertEquals(10, geometry.getCoordinates().length);
  }

  /**
   * Create multi polygon.
   */
  @Test
  void createMultiPolygon() {
    Coordinate p0 = new Coordinate(23.456, 23.567);
    Coordinate p1 = new Coordinate(28.456, 23.567);
    Coordinate p2 = new Coordinate(28.456, 29.567);
    Coordinate p3 = new Coordinate(23.456, 30.567);
    LinearRing ring0 = GeometryUtils.createLinearRing(Arrays.asList(p0, p1, p2, p3, p0));
    Polygon geometry0 = GeometryUtils.createPolygon(ring0);

    Coordinate c0 = new Coordinate(3.456, 3.567);
    Coordinate c1 = new Coordinate(8.456, 3.567);
    Coordinate c2 = new Coordinate(8.456, 9.567);
    Coordinate c3 = new Coordinate(3.456, 10.567);
    LinearRing ring = GeometryUtils.createLinearRing(Arrays.asList(c0, c1, c2, c3, c0));
    Coordinate c00 = new Coordinate(4.456, 4.567);
    Coordinate c01 = new Coordinate(5.456, 4.567);
    Coordinate c02 = new Coordinate(5.456, 6.567);
    Coordinate c03 = new Coordinate(4.456, 6.567);
    LinearRing hole0 = GeometryUtils.createLinearRing(Arrays.asList(c00, c01, c02, c03, c00));
    Polygon geometry1 = GeometryUtils.createPolygon(ring, Collections.singletonList(hole0));

    MultiPolygon geometry = GeometryUtils.createMultiPolygon(Arrays.asList(geometry0, geometry1));
    assertNotNull(geometry);
    assertEquals(2, geometry.getNumGeometries());
    assertEquals(15, geometry.getCoordinates().length);

    geometry = GeometryUtils.createMultiPolygon(null);
    assertNotNull(geometry);
    assertEquals(0, geometry.getCoordinates().length);
  }

  /**
   * Create geometry collection.
   */
  @Test
  void createGeometryCollection() {
    Coordinate p0 = new Coordinate(123.456, 34.567);
    Coordinate p1 = new Coordinate(124.456, 35.567);
    Coordinate p2 = new Coordinate(125.456, 36.567);
    LineString lineString = GeometryUtils.createLineString(Arrays.asList(p0, p1, p2));

    Coordinate c0 = new Coordinate(3.456, 3.567);
    Coordinate c1 = new Coordinate(8.456, 3.567);
    Coordinate c2 = new Coordinate(8.456, 9.567);
    Coordinate c3 = new Coordinate(3.456, 10.567);
    LinearRing ring = GeometryUtils.createLinearRing(Arrays.asList(c0, c1, c2, c3, c0));

    Polygon polygon = GeometryUtils.createPolygon(ring);

    GeometryCollection col = GeometryUtils.createGeometryCollection(lineString, ring, polygon);
    assertNotNull(col);
    assertEquals(3, col.getNumGeometries());

    col = GeometryUtils.createGeometryCollection(Arrays.asList(polygon, ring, lineString));
    assertNotNull(col);
    assertEquals(3, col.getNumGeometries());

    col = GeometryUtils.createGeometryCollection((Geometry[]) null);
    assertNotNull(col);
    assertEquals(0, col.getNumGeometries());

    col = GeometryUtils.createGeometryCollection((Collection<? extends Geometry>) null);
    assertNotNull(col);
    assertEquals(0, col.getNumGeometries());

    col = GeometryUtils.createGeometryCollection(Collections.emptyList());
    assertNotNull(col);
    assertEquals(0, col.getNumGeometries());
  }

  /**
   * Copy and apply filters.
   */
  @Test
  void copyAndApplyFilters() {
    assertNull(GeometryUtils.copyAndApplyFilters(null, new SwapCoordinateFilter()));

    Geometry geometry = GeometryUtils.createPoint(8., 9.);
    assertTrue(GeometryUtils.equals(geometry, GeometryUtils.copyAndApplyFilters(geometry)));
  }

  /**
   * Transform wgs 84 to mercator and back.
   */
  @Test
  void transformWgs84ToMercatorAndBack() {
    assertNull(GeometryUtils.transformWgs84ToMercator(null));
    assertNull(GeometryUtils.transformMercatorToWgs84(null));

    Coordinate c0 = new Coordinate(3.456, 3.567);
    Coordinate c1 = new Coordinate(8.456, 3.567);
    Coordinate c2 = new Coordinate(8.456, 9.567);
    Coordinate c3 = new Coordinate(3.456, 10.567);
    LineString lineString = GeometryUtils.createLineString(Arrays.asList(c0, c1, c2, c3));
    assertEquals(4, lineString.getCoordinates().length);

    LineString actual = (LineString) GeometryUtils.transformWgs84ToMercator(lineString);
    assertNotNull(actual);
    assertEquals(4, actual.getCoordinates().length);

    for (int i = 0; i < actual.getCoordinates().length; i++) {
      System.out.println(lineString.getCoordinateN(i) + " (WGS84)  --> "
          + actual.getCoordinateN(i) + " (Mercator)");
    }

    LineString oldActual = actual;
    actual = (LineString) GeometryUtils.transformMercatorToWgs84(actual);
    assertNotNull(actual);
    assertEquals(4, actual.getCoordinates().length);

    for (int i = 0; i < actual.getCoordinates().length; i++) {
      System.out.println(oldActual.getCoordinateN(i) + " (Mercator)  --> "
          + actual.getCoordinateN(i) + " (WGS84)");
      assertEquals(lineString.getCoordinateN(i).getX(), actual.getCoordinateN(i).getX(), 0.001);
      assertEquals(lineString.getCoordinateN(i).getY(), actual.getCoordinateN(i).getY(), 0.001);
    }
  }

  /**
   * Swap coordinates.
   */
  @Test
  void swapCoordinates() {
    assertNull(GeometryUtils.swapCoordinates(null));
    Point geometry = GeometryUtils.createPoint(4., 5.);
    Point actual = (Point) GeometryUtils.swapCoordinates(geometry);
    assertEquals(geometry.getX(), actual.getY());
    assertEquals(geometry.getY(), actual.getX());
  }

}
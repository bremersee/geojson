/*
 * Copyright 2015-2022 the original author or authors.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.geojson.filter.SwapCoordinateFilter;
import org.bremersee.geojson.model.LatLon;
import org.bremersee.geojson.model.LatLonAware;
import org.bremersee.geojson.model.LatitudeLongitude;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * The type Geo json geometry factory test.
 */
@ExtendWith(SoftAssertionsExtension.class)
class GeoJsonGeometryFactoryTest {

  private static final GeoJsonGeometryFactory target = new GeoJsonGeometryFactory();

  /**
   * Create coordinate with double.
   *
   * @param softly the softly
   */
  @Test
  void createCoordinateWithDouble(SoftAssertions softly) {
    Coordinate actual = GeoJsonGeometryFactory.createCoordinate(1., 2.);
    softly.assertThat(actual.getX()).isEqualTo(1.);
    softly.assertThat(actual.getY()).isEqualTo(2.);
  }

  /**
   * Create coordinate with big decimal.
   *
   * @param softly the softly
   */
  @Test
  void createCoordinateWithBigDecimal(SoftAssertions softly) {
    Coordinate actual = GeoJsonGeometryFactory.createCoordinate(BigDecimal.ONE, BigDecimal.TEN);
    softly.assertThat(actual.getX()).isEqualTo(1.);
    softly.assertThat(actual.getY()).isEqualTo(10.);
  }

  /**
   * Create coordinate with null.
   */
  @Test
  void createCoordinateWithNull() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> GeoJsonGeometryFactory.createCoordinate(BigDecimal.ZERO, null));
  }

  /**
   * Create coordinate with nulls.
   */
  @Test
  void createCoordinateWithNulls() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> GeoJsonGeometryFactory.createCoordinate(null, null));
  }

  /**
   * Create coordinate with lat lon aware.
   *
   * @param softly the softly
   */
  @Test
  void createCoordinateWithLatLonAware(SoftAssertions softly) {
    Coordinate actual = GeoJsonGeometryFactory.createCoordinate(null);
    softly.assertThat(actual).isNull();
    actual = GeoJsonGeometryFactory.createCoordinate(LatLonAware.builder()
        .longitude(1.)
        .latitude(10.)
        .build());
    softly.assertThat(actual)
        .isEqualTo(new Coordinate(1., 10.));
  }

  /**
   * Create point with double.
   */
  @Test
  void createPointWithDouble() {
    Point actual = target.createPoint(0., 10.);
    assertThat(actual.getCoordinate())
        .isEqualTo(new Coordinate(0., 10.));

  }

  /**
   * Create point with big decimal.
   */
  @Test
  void createPointWithBigDecimal() {
    Point actual = target.createPoint(BigDecimal.TEN, BigDecimal.ZERO);
    assertThat(actual.getCoordinate())
        .isEqualTo(new Coordinate(10., 0.));
  }

  /**
   * Create point with lat lon aware.
   */
  @Test
  void createPointWithLatLonAware() {
    Point actual = target.createPoint(LatLonAware.builder()
        .longitude(8.)
        .latitude(7.)
        .build());
    assertThat(actual.getCoordinate())
        .isEqualTo(new Coordinate(8., 7.));
  }

  /**
   * Create line string.
   */
  @Test
  void createLineString() {
    Coordinate c0 = new Coordinate(123.456, 34.567);
    Coordinate c1 = new Coordinate(124.456, 35.567);
    Coordinate c2 = new Coordinate(125.456, 36.567);
    LineString actual = target.createLineString(List.of(c0, c1, c2));
    assertThat(actual.getCoordinates())
        .isNotNull()
        .containsExactly(c0, c1, c2);
  }

  /**
   * Create line string with empty collection.
   */
  @Test
  void createLineStringWithEmptyCollection() {
    LineString actual = target.createLineString(List.of());
    assertThat(actual.getCoordinates())
        .isEmpty();
  }

  /**
   * Create line string with null.
   */
  @Test
  void createLineStringWithNull() {
    LineString actual = target.createLineString((Collection<? extends Coordinate>) null);
    assertThat(actual.getCoordinates())
        .isEmpty();
  }

  /**
   * Create linear ring.
   */
  @Test
  void createLinearRing() {
    Coordinate c0 = new Coordinate(0, 0);
    Coordinate c1 = new Coordinate(1, 0);
    Coordinate c2 = new Coordinate(1, 1);
    Coordinate c3 = new Coordinate(0, 1);
    LinearRing actual = target.createLinearRing(List.of(c0, c1, c2, c3, c0));
    assertThat(actual.getCoordinates())
        .containsExactly(c0, c1, c2, c3, c0);
  }

  /**
   * Create linear ring with empty collection.
   */
  @Test
  void createLinearRingWithEmptyCollection() {
    LinearRing actual = target.createLinearRing(List.of());
    assertThat(actual.getCoordinates())
        .isEmpty();
  }

  /**
   * Create linear ring with null.
   */
  @Test
  void createLinearRingWithNull() {
    LinearRing actual = target.createLinearRing((Collection<? extends Coordinate>) null);
    assertThat(actual.getCoordinates())
        .isEmpty();
  }

  /**
   * Create polygon.
   */
  @Test
  void createPolygon() {
    Coordinate c0 = new Coordinate(0, 0);
    Coordinate c1 = new Coordinate(1, 0);
    Coordinate c2 = new Coordinate(1, 1);
    Coordinate c3 = new Coordinate(0, 1);
    LinearRing shell = target.createLinearRing(List.of(c0, c1, c2, c3, c0));

    Coordinate h0 = new Coordinate(0.25, 0.25);
    Coordinate h1 = new Coordinate(0.75, 0.25);
    Coordinate h2 = new Coordinate(0.75, 0.75);
    Coordinate h3 = new Coordinate(0.25, 0.75);
    LinearRing hole0 = target.createLinearRing(List.of(h0, h1, h2, h3, h0));

    Polygon actual = target.createPolygon(shell, List.of(hole0));
    assertThat(actual.getCoordinates())
        .containsExactly(c0, c1, c2, c3, c0, h0, h1, h2, h3, h0);
  }

  /**
   * Create polygon null holes.
   */
  @Test
  void createPolygonNullHoles() {
    Coordinate c0 = new Coordinate(0, 0);
    Coordinate c1 = new Coordinate(1, 0);
    Coordinate c2 = new Coordinate(1, 1);
    Coordinate c3 = new Coordinate(0, 1);
    LinearRing shell = target.createLinearRing(List.of(c0, c1, c2, c3, c0));
    Polygon actual = target.createPolygon(shell, (Collection<? extends LinearRing>) null);
    assertThat(actual.getCoordinates())
        .containsExactly(c0, c1, c2, c3, c0);
  }

  /**
   * Create polygon empty holes.
   */
  @Test
  void createPolygonEmptyHoles() {
    Coordinate c0 = new Coordinate(0, 0);
    Coordinate c1 = new Coordinate(1, 0);
    Coordinate c2 = new Coordinate(1, 1);
    Coordinate c3 = new Coordinate(0, 1);
    LinearRing shell = target.createLinearRing(List.of(c0, c1, c2, c3, c0));
    Polygon actual = target.createPolygon(shell, List.of());
    assertThat(actual.getCoordinates())
        .containsExactly(c0, c1, c2, c3, c0);
  }

  /**
   * Create multi point.
   */
  @Test
  void createMultiPoint() {
    Coordinate c0 = new Coordinate(0, 0);
    Coordinate c1 = new Coordinate(1, 0);
    MultiPoint actual = target.createMultiPoint(List.of(
        target.createPoint(c0),
        target.createPoint(c1)
    ));
    assertThat(actual.getCoordinates())
        .containsExactly(c0, c1);
  }

  /**
   * Create multi line string.
   */
  @Test
  void createMultiLineString() {
    Coordinate c0 = new Coordinate(0, 0);
    Coordinate c1 = new Coordinate(1, 0);
    Coordinate c2 = new Coordinate(1, 1);
    Coordinate c3 = new Coordinate(0, 1);
    MultiLineString actual = target.createMultiLineString(List.of(
        target.createLineString(List.of(c0, c1)),
        target.createLineString(List.of(c2, c3))
    ));
    assertThat(actual.getCoordinates())
        .containsExactly(c0, c1, c2, c3);
  }

  /**
   * Create multi polygon.
   */
  @Test
  void createMultiPolygon() {
    Coordinate c0 = new Coordinate(0, 0);
    Coordinate c1 = new Coordinate(1, 0);
    Coordinate c2 = new Coordinate(1, 1);
    Coordinate c3 = new Coordinate(0, 1);
    LinearRing shell = target.createLinearRing(List.of(c0, c1, c2, c3, c0));

    Coordinate h0 = new Coordinate(0.25, 0.25);
    Coordinate h1 = new Coordinate(0.75, 0.25);
    Coordinate h2 = new Coordinate(0.75, 0.75);
    Coordinate h3 = new Coordinate(0.25, 0.75);
    LinearRing hole0 = target.createLinearRing(List.of(h0, h1, h2, h3, h0));

    MultiPolygon actual = target.createMultiPolygon(List.of(
        target.createPolygon(shell, List.of(hole0))
    ));
    assertThat(actual.getCoordinates())
        .containsExactly(c0, c1, c2, c3, c0, h0, h1, h2, h3, h0);
  }

  /**
   * Create geometry collection.
   */
  @Test
  void createGeometryCollection() {
    Coordinate c0 = new Coordinate(0, 0);
    Coordinate c1 = new Coordinate(1, 0);
    GeometryCollection actual = target.createGeometryCollection(List.of(
        target.createPoint(c0),
        target.createPoint(c1)
    ));
    assertThat(actual.getCoordinates())
        .containsExactly(c0, c1);
  }

  /**
   * Create lat lon from coordinate.
   *
   * @param softly the softly
   */
  @Test
  void createLatLonFromCoordinate(SoftAssertions softly) {
    Coordinate c = new Coordinate(1, 0);
    LatLon actual = GeoJsonGeometryFactory.createLatLon(c);
    softly.assertThat(actual)
        .isNotNull()
        .extracting(LatLon::getLatitude)
        .extracting(BigDecimal::doubleValue)
        .isEqualTo(c.getY());
    softly.assertThat(actual)
        .isNotNull()
        .extracting(LatLon::getLongitude)
        .extracting(BigDecimal::doubleValue)
        .isEqualTo(c.getX());
  }

  /**
   * Create lat lon from coordinate null.
   */
  @Test
  void createLatLonFromCoordinateNull() {
    assertThat(GeoJsonGeometryFactory.createLatLon((Coordinate) null))
        .isNull();
  }

  /**
   * Create lat lon from point.
   *
   * @param softly the softly
   */
  @Test
  void createLatLonFromPoint(SoftAssertions softly) {
    Coordinate c = new Coordinate(1, 0);
    LatLon actual = GeoJsonGeometryFactory.createLatLon(target.createPoint(c));
    softly.assertThat(actual)
        .isNotNull()
        .extracting(LatLon::getLatitude)
        .extracting(BigDecimal::doubleValue)
        .isEqualTo(c.getY());
    softly.assertThat(actual)
        .isNotNull()
        .extracting(LatLon::getLongitude)
        .extracting(BigDecimal::doubleValue)
        .isEqualTo(c.getX());
  }

  /**
   * Create lat lon from point null.
   */
  @Test
  void createLatLonFromPointNull() {
    assertThat(GeoJsonGeometryFactory.createLatLon((Point) null))
        .isNull();
  }

  /**
   * Create latitude longitude from coordinate.
   *
   * @param softly the softly
   */
  @Test
  void createLatitudeLongitudeFromCoordinate(SoftAssertions softly) {
    Coordinate c = new Coordinate(1, 0);
    LatitudeLongitude actual = GeoJsonGeometryFactory.createLatitudeLongitude(c);
    softly.assertThat(actual)
        .isNotNull()
        .extracting(LatitudeLongitude::getLatitude)
        .extracting(BigDecimal::doubleValue)
        .isEqualTo(c.getY());
    softly.assertThat(actual)
        .isNotNull()
        .extracting(LatitudeLongitude::getLongitude)
        .extracting(BigDecimal::doubleValue)
        .isEqualTo(c.getX());
  }

  /**
   * Create latitude longitude from coordinate null.
   */
  @Test
  void createLatitudeLongitudeFromCoordinateNull() {
    assertThat(GeoJsonGeometryFactory.createLatitudeLongitude((Coordinate) null))
        .isNull();
  }

  /**
   * Create latitude longitude from point.
   *
   * @param softly the softly
   */
  @Test
  void createLatitudeLongitudeFromPoint(SoftAssertions softly) {
    Coordinate c = new Coordinate(1, 0);
    LatitudeLongitude actual = GeoJsonGeometryFactory
        .createLatitudeLongitude(target.createPoint(c));
    softly.assertThat(actual)
        .isNotNull()
        .extracting(LatitudeLongitude::getLatitude)
        .extracting(BigDecimal::doubleValue)
        .isEqualTo(c.getY());
    softly.assertThat(actual)
        .isNotNull()
        .extracting(LatitudeLongitude::getLongitude)
        .extracting(BigDecimal::doubleValue)
        .isEqualTo(c.getX());
  }

  /**
   * Create latitude longitude from point null.
   */
  @Test
  void createLatitudeLongitudeFromPointNull() {
    assertThat(GeoJsonGeometryFactory.createLatitudeLongitude((Point) null))
        .isNull();
  }

  /**
   * Test equals.
   *
   * @param softly the softly
   */
  @Test
  void testEquals(SoftAssertions softly) {
    Point g1 = target.createPoint(1.234, 5.678);
    Point g2 = target.createPoint(1.234, 5.678);
    Point g3 = target.createPoint(1.234, 5.679);
    softly.assertThat(GeoJsonGeometryFactory.equals(null, null)).isTrue();
    softly.assertThat(GeoJsonGeometryFactory.equals(g1, g1)).isTrue();
    softly.assertThat(GeoJsonGeometryFactory.equals(g1, g2)).isTrue();
    softly.assertThat(GeoJsonGeometryFactory.equals(g1, g3)).isFalse();
    softly.assertThat(GeoJsonGeometryFactory.equals(g1, null)).isFalse();
    softly.assertThat(GeoJsonGeometryFactory.equals(null, g1)).isFalse();

    GeometryCollection gc1 = target.createGeometryCollection(List.of(g1, g3));
    GeometryCollection gc2 = target.createGeometryCollection(List.of(g1, g3));
    GeometryCollection gc3 = target.createGeometryCollection(List.of(g1, g2));
    GeometryCollection gc4 = target.createGeometryCollection(List.of(g1, g2, g3));
    softly.assertThat(GeoJsonGeometryFactory.equals(gc1, gc1)).isTrue();
    softly.assertThat(GeoJsonGeometryFactory.equals(gc1, gc2)).isTrue();
    softly.assertThat(GeoJsonGeometryFactory.equals(gc1, gc3)).isFalse();
    softly.assertThat(GeoJsonGeometryFactory.equals(gc1, gc4)).isFalse();
  }

  /**
   * Gets bounding box.
   *
   * @param softly the softly
   */
  @Test
  void getBoundingBox(SoftAssertions softly) {
    softly.assertThat(GeoJsonGeometryFactory.getBoundingBox((Geometry) null))
        .isNull();
    softly.assertThat(GeoJsonGeometryFactory.getBoundingBox((Collection<? extends Geometry>) null))
        .isNull();

    softly.assertThat(GeoJsonGeometryFactory.getBoundingBox(target.createPoint(Double.NaN, 1.)))
        .isNull();
    softly.assertThat(GeoJsonGeometryFactory.getBoundingBox(target.createPoint(1., Double.NaN)))
        .isNull();

    Point p0 = target.createPoint(1., 20.);
    Point p1 = target.createPoint(10., 2.);
    Geometry geometry = target.createMultiPoint(List.of(p0, p1));
    double[] bounds = GeoJsonGeometryFactory.getBoundingBox(geometry);
    softly.assertThat(bounds)
        .isNotNull();
    softly.assertThat(bounds.length)
        .isEqualTo(4);
    // min x:
    softly.assertThat(bounds[0])
        .isEqualTo(1.);
    // min y:
    softly.assertThat(bounds[1])
        .isEqualTo(2.);
    // max x:
    softly.assertThat(bounds[2])
        .isEqualTo(10.);
    // max y:
    softly.assertThat(bounds[3])
        .isEqualTo(20.);

    Coordinate coordinate = GeoJsonGeometryFactory.getSouthWest(bounds);
    softly.assertThat(coordinate.x)
        .isEqualTo(1.); // min x
    softly.assertThat(coordinate.y)
        .isEqualTo(2.); // min y

    coordinate = GeoJsonGeometryFactory.getSouthEast(bounds);
    softly.assertThat(coordinate.x)
        .isEqualTo(10.); // max x
    softly.assertThat(coordinate.y)
        .isEqualTo(2.); // min y

    coordinate = GeoJsonGeometryFactory.getNorthEast(bounds);
    softly.assertThat(coordinate.x)
        .isEqualTo(10.); // max x
    softly.assertThat(coordinate.y)
        .isEqualTo(20.); // max y

    coordinate = GeoJsonGeometryFactory.getNorthWest(bounds);
    softly.assertThat(coordinate.x)
        .isEqualTo(1.); // min x
    softly.assertThat(coordinate.y)
        .isEqualTo(20.); // max y

    Polygon polygon = target.getBoundingBoxAsPolygon2D(bounds);
    softly.assertThat(polygon.getCoordinates().length)
        .isEqualTo(5);
    softly.assertThat(GeoJsonGeometryFactory
            .equals(polygon, target.getBoundingBoxAsPolygon2D(geometry)))
        .isTrue();

    p0 = target.createPoint(new Coordinate(10., 2., 3.));
    p1 = target.createPoint(new Coordinate(1., 20., 30.));
    bounds = GeoJsonGeometryFactory.getBoundingBox(
        target.createMultiPoint(List.of(p0, p1)));
    softly.assertThat(bounds).isNotNull();
    softly.assertThat(bounds.length).isEqualTo(6);
    // min x:
    softly.assertThat(bounds[0])
        .isEqualTo(1.);
    // min y:
    softly.assertThat(bounds[1])
        .isEqualTo(2.);
    // min z:
    softly.assertThat(bounds[2])
        .isEqualTo(3.);
    // max x:
    softly.assertThat(bounds[3])
        .isEqualTo(10.);
    // max y:
    softly.assertThat(bounds[4])
        .isEqualTo(20.);
    // max z:
    softly.assertThat(bounds[5])
        .isEqualTo(30.);

    coordinate = GeoJsonGeometryFactory.getSouthWest(bounds);
    softly.assertThat(coordinate.x)
        .isEqualTo(1.); // min x
    softly.assertThat(coordinate.y)
        .isEqualTo(2.); // min y

    coordinate = GeoJsonGeometryFactory.getSouthEast(bounds);
    softly.assertThat(coordinate.x)
        .isEqualTo(10.); // max x
    softly.assertThat(coordinate.y)
        .isEqualTo(2.); // min y

    coordinate = GeoJsonGeometryFactory.getNorthEast(bounds);
    softly.assertThat(coordinate.x)
        .isEqualTo(10.); // max x
    softly.assertThat(coordinate.y)
        .isEqualTo(20.); // max y

    coordinate = GeoJsonGeometryFactory.getNorthWest(bounds);
    softly.assertThat(coordinate.x)
        .isEqualTo(1.); // min x
    softly.assertThat(coordinate.y)
        .isEqualTo(20.); // max y

    softly.assertThat(GeoJsonGeometryFactory.getSouthEast(new double[0]))
        .isNull();
    softly.assertThat(GeoJsonGeometryFactory.getSouthWest(new double[0]))
        .isNull();
    softly.assertThat(GeoJsonGeometryFactory.getNorthEast(new double[0]))
        .isNull();
    softly.assertThat(GeoJsonGeometryFactory.getNorthWest(new double[0]))
        .isNull();
  }

  /**
   * Create geometry from well known text.
   *
   * @param softly the softly
   * @throws IOException the io exception
   */
  @Test
  void createGeometryFromWellKnownText(SoftAssertions softly) throws IOException {
    Coordinate c1 = new Coordinate(1, 0);

    String wkt = "POINT (1 0)";
    Geometry expected = target.createPoint(c1);
    Geometry actual = target.createGeometryFromWellKnownText(wkt);
    softly.assertThat(GeoJsonGeometryFactory
            .equals(actual, expected))
        .isTrue();

    Coordinate c3 = new Coordinate(0, 1);
    wkt = "LINESTRING (1 0, 0 1)";
    expected = target.createLineString(List.of(c1, c3));
    actual = target.createGeometryFromWellKnownText(wkt);
    softly.assertThat(GeoJsonGeometryFactory
            .equals(actual, expected))
        .isTrue();

    Coordinate c0 = new Coordinate(0, 0);
    Coordinate c2 = new Coordinate(1, 1);
    LinearRing shell = target.createLinearRing(List.of(c0, c1, c2, c3, c0));

    Coordinate h0 = new Coordinate(0.25, 0.25);
    Coordinate h1 = new Coordinate(0.75, 0.25);
    Coordinate h2 = new Coordinate(0.75, 0.75);
    Coordinate h3 = new Coordinate(0.25, 0.75);
    LinearRing hole0 = target.createLinearRing(List.of(h0, h1, h2, h3, h0));
    wkt = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0), "
        + "(0.25 0.25, 0.75 0.25, 0.75 0.75, 0.25 0.75, 0.25 0.25))";
    expected = target.createPolygon(shell, List.of(hole0));
    actual = target.createGeometryFromWellKnownText(new StringReader(wkt));
    softly.assertThat(GeoJsonGeometryFactory
            .equals(actual, expected))
        .isTrue();

    wkt = "GEOMETRYCOLLECTION (POINT (0.75 0.25), LINESTRING (1 0, 0 1))";
    expected = target.createGeometryCollection(List.of(
        target.createPoint(h1),
        target.createLineString(List.of(c1, c3))
    ));
    actual = target.createGeometryFromWellKnownText(
        new ByteArrayInputStream(wkt.getBytes(StandardCharsets.UTF_8)),
        StandardCharsets.UTF_8);
    softly.assertThat(GeoJsonGeometryFactory
            .equals(actual, expected))
        .isTrue();
  }

  /**
   * Create geometry from well known text with nonsense.
   */
  @Test
  void createGeometryFromWellKnownTextWithNonsense() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> target.createGeometryFromWellKnownText("nonsense"));
  }

  /**
   * Create geometry from well known text with nonsense reader.
   */
  @Test
  void createGeometryFromWellKnownTextWithNonsenseReader() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> target.createGeometryFromWellKnownText(new StringReader("nonsense")));
  }

  /**
   * Create geometry from well known text with nonsense input stream.
   */
  @Test
  void createGeometryFromWellKnownTextWithNonsenseInputStream() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> target.createGeometryFromWellKnownText(
            new ByteArrayInputStream("nonsense".getBytes(StandardCharsets.UTF_8)),
            StandardCharsets.UTF_8));
  }

  /**
   * Create geometry from well known text with null.
   *
   * @param softly the softly
   * @throws IOException the io exception
   */
  @Test
  void createGeometryFromWellKnownTextWithNull(SoftAssertions softly) throws IOException {
    //noinspection unchecked
    softly.assertThat(target.createGeometryFromWellKnownText((String) null))
        .isNull();
    //noinspection unchecked
    softly.assertThat(target.createGeometryFromWellKnownText((Reader) null))
        .isNull();
    //noinspection unchecked
    softly.assertThat(target.createGeometryFromWellKnownText(null, null))
        .isNull();
  }

  /**
   * Copy and apply filters.
   *
   * @param softly the softly
   */
  @Test
  void copyAndApplyFilters(SoftAssertions softly) {
    //noinspection unchecked
    softly
        .assertThat(GeoJsonGeometryFactory.copyAndApplyFilters(
            null,
            new SwapCoordinateFilter()))
        .isNull();

    Geometry geometry = target.createPoint(8., 9.);
    softly
        .assertThat(GeoJsonGeometryFactory
            .equals(
                geometry,
                GeoJsonGeometryFactory.copyAndApplyFilters(geometry)))
        .isTrue();

    softly
        .assertThat(GeoJsonGeometryFactory
            .equals(
                target.createPoint(9., 8.),
                GeoJsonGeometryFactory.copyAndApplyFilters(
                    geometry,
                    new SwapCoordinateFilter())))
        .isTrue();

    softly
        .assertThat(GeoJsonGeometryFactory
            .equals(
                target.createPoint(9., 8.),
                GeoJsonGeometryFactory.copyAndApplyFilters(
                    geometry,
                    List.of(new SwapCoordinateFilter()))))
        .isTrue();
  }
}
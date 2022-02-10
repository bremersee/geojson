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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;
import org.bremersee.geojson.utils.GeometryUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

/**
 * JUnit test that writes and reads GeoJSON objects.
 *
 * @author Christian Bremer
 */
class GeoJsonTests {

  private static final GeometryFactory geometryFactory = new GeometryFactory();

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private static GeometryFactory getGeometryFactory() {
    return geometryFactory;
  }

  private static ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  /**
   * Init class.
   */
  @BeforeAll
  static void initClass() {
    getObjectMapper().registerModules(
        new Jdk8Module(),
        new JavaTimeModule(),
        new GeoJsonObjectMapperModule(getGeometryFactory()));
    getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    getObjectMapper().enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
    getObjectMapper().setDateFormat(new StdDateFormat());
    getObjectMapper().setTimeZone(TimeZone.getTimeZone("GMT"));
    getObjectMapper().setLocale(Locale.GERMANY);
  }

  private static LineString createLineString(Coordinate[] coordinates) {
    CoordinateSequence points = new CoordinateArraySequence(coordinates);
    return new LineString(points, getGeometryFactory());
  }

  private static LineString createLineString() {
    return createLineString(new Coordinate[]{new Coordinate(0., 0.), new Coordinate(0., 1.),
        new Coordinate(1., 1.), new Coordinate(1., 0.), new Coordinate(0., 0.)});
  }

  private static MultiLineString createMultiLineString() {
    LineString[] lineStrings = new LineString[]{createLineString(),
        createLineString(new Coordinate[]{new Coordinate(2., 2.), new Coordinate(2., 4.),
            new Coordinate(4., 4.), new Coordinate(4., 2.), new Coordinate(2., 2.)})};
    return new MultiLineString(lineStrings, getGeometryFactory());
  }

  private static Point createPoint(double x, double y) {
    CoordinateSequence coordinates = new CoordinateArraySequence(
        new Coordinate[]{new Coordinate(x, y)});
    return new Point(coordinates, getGeometryFactory());
  }

  private static Point createPoint() {
    return createPoint(3., 6.);
  }

  private MultiPoint createMultiPoint() {
    return new MultiPoint(new Point[]{createPoint(), createPoint(11., 12.)}, getGeometryFactory());
  }

  private Polygon createPolygon() {
    return new Polygon(
        new LinearRing(createLineString().getCoordinateSequence(), getGeometryFactory()), null,
        getGeometryFactory());
  }

  private Polygon createPolygonWithHoles() {
    LineString ls0 = createLineString(
        new Coordinate[]{new Coordinate(0.1, 0.1), new Coordinate(0.1, 0.2),
            new Coordinate(0.2, 0.2), new Coordinate(0.2, 0.1), new Coordinate(0.1, 0.1)});
    LineString ls1 = createLineString(
        new Coordinate[]{new Coordinate(0.8, 0.8), new Coordinate(0.8, 0.9),
            new Coordinate(0.9, 0.9), new Coordinate(0.9, 0.8), new Coordinate(0.8, 0.8)});

    LinearRing[] holes = new LinearRing[2];
    holes[0] = new LinearRing(ls0.getCoordinateSequence(), getGeometryFactory());
    holes[1] = new LinearRing(ls1.getCoordinateSequence(), getGeometryFactory());

    return new Polygon(
        new LinearRing(createLineString().getCoordinateSequence(), getGeometryFactory()), holes,
        getGeometryFactory());
  }

  private MultiPolygon createMultiPolygon() {
    Polygon[] polygons = new Polygon[]{createPolygon(), createPolygonWithHoles()};
    return new MultiPolygon(polygons, getGeometryFactory());
  }

  /**
   * Configure GeoJSON object mapper module.
   */
  @Test
  void configureGeoJsonObjectMapperModule() {
    ObjectMapper om = JsonMapper.builder()
        .enable(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS)
        .build();
    GeoJsonObjectMapperModule.configure(om);
    assertTrue(om.getRegisteredModuleIds().stream()
        .anyMatch(module -> GeoJsonObjectMapperModule.TYPE_ID
            .equals(String.valueOf(module))));

    om = new ObjectMapper();
    om.registerModule(new GeoJsonObjectMapperModule());
    assertTrue(om.getRegisteredModuleIds().stream()
        .anyMatch(module -> GeoJsonObjectMapperModule.TYPE_ID
            .equals(String.valueOf(module))));

    om = new ObjectMapper();
    om.registerModule(new GeoJsonObjectMapperModule(new GeometryFactory()));
    assertTrue(om.getRegisteredModuleIds().stream()
        .anyMatch(module -> GeoJsonObjectMapperModule.TYPE_ID
            .equals(String.valueOf(module))));
  }

  /**
   * Test point.
   *
   * @throws Exception the exception
   */
  @Test
  void testPoint() throws Exception {
    Point geometry = createPoint();
    System.out.println("Testing " + geometry.toText());
    String jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
        .writeValueAsString(geometry);
    System.out.println(jsonStr);
    Point readGeometry = getObjectMapper().readValue(jsonStr, Point.class);
    assertTrue(GeometryUtils.equals(geometry, readGeometry));

    GeometryWrapper gjg = new GeometryWrapper(geometry);
    jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(gjg);
    System.out.println(jsonStr);
    GeometryWrapper readGjg = getObjectMapper().readValue(jsonStr, GeometryWrapper.class);
    assertEquals(gjg, readGjg);
    assertTrue(GeometryUtils.equals(geometry, readGjg.getGeometry()));
  }

  /**
   * Test line string.
   *
   * @throws Exception the exception
   */
  @Test
  void testLineString() throws Exception {
    LineString geometry = createLineString();
    System.out.println("Testing " + geometry.toText());
    String jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
        .writeValueAsString(geometry);
    System.out.println(jsonStr);
    LineString readGeometry = getObjectMapper().readValue(jsonStr, LineString.class);
    assertTrue(GeometryUtils.equals(geometry, readGeometry));

    GeometryWrapper gjg = new GeometryWrapper(geometry);
    jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(gjg);
    System.out.println(jsonStr);
    GeometryWrapper readGjg = getObjectMapper().readValue(jsonStr, GeometryWrapper.class);
    assertEquals(gjg, readGjg);
    assertTrue(GeometryUtils.equals(geometry, readGjg.getGeometry()));
  }

  /**
   * Test polygon.
   *
   * @throws Exception the exception
   */
  @Test
  void testPolygon() throws Exception {
    Polygon geometry = createPolygon();
    System.out.println("Testing " + geometry.toText());
    String jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
        .writeValueAsString(geometry);
    System.out.println(jsonStr);
    Polygon readGeometry = getObjectMapper().readValue(jsonStr, Polygon.class);
    assertTrue(GeometryUtils.equals(geometry, readGeometry));

    GeometryWrapper gjg = new GeometryWrapper(geometry);
    jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(gjg);
    System.out.println(jsonStr);
    GeometryWrapper readGjg = getObjectMapper().readValue(jsonStr, GeometryWrapper.class);
    assertEquals(gjg, readGjg);
    assertTrue(GeometryUtils.equals(geometry, readGjg.getGeometry()));
  }

  /**
   * Test polygon with holes.
   *
   * @throws Exception the exception
   */
  @Test
  void testPolygonWithHoles() throws Exception {
    Polygon geometry = createPolygonWithHoles();
    System.out.println("Testing with holes " + geometry.toText());
    String jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
        .writeValueAsString(geometry);
    System.out.println(jsonStr);
    Polygon readGeometry = getObjectMapper().readValue(jsonStr, Polygon.class);
    assertTrue(GeometryUtils.equals(geometry, readGeometry));

    GeometryWrapper gjg = new GeometryWrapper(geometry);
    jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(gjg);
    System.out.println(jsonStr);
    GeometryWrapper readGjg = getObjectMapper().readValue(jsonStr, GeometryWrapper.class);
    assertEquals(gjg, readGjg);
    assertTrue(GeometryUtils.equals(geometry, readGjg.getGeometry()));
  }

  /**
   * Test multi point.
   *
   * @throws Exception the exception
   */
  @Test
  void testMultiPoint() throws Exception {
    MultiPoint geometry = createMultiPoint();
    System.out.println("Testing " + geometry.toText());
    String jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
        .writeValueAsString(geometry);
    System.out.println(jsonStr);
    MultiPoint readGeometry = getObjectMapper().readValue(jsonStr, MultiPoint.class);
    assertTrue(GeometryUtils.equals(geometry, readGeometry));

    GeometryWrapper gjg = new GeometryWrapper(geometry);
    jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(gjg);
    System.out.println(jsonStr);
    GeometryWrapper readGjg = getObjectMapper().readValue(jsonStr, GeometryWrapper.class);
    assertEquals(gjg, readGjg);
    assertTrue(GeometryUtils.equals(geometry, readGjg.getGeometry()));
  }

  /**
   * Test multi line string.
   *
   * @throws Exception the exception
   */
  @Test
  void testMultiLineString() throws Exception {
    MultiLineString geometry = createMultiLineString();
    System.out.println("Testing " + geometry.toText());
    String jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
        .writeValueAsString(geometry);
    System.out.println(jsonStr);
    MultiLineString readGeometry = getObjectMapper().readValue(jsonStr, MultiLineString.class);
    assertTrue(GeometryUtils.equals(geometry, readGeometry));

    GeometryWrapper gjg = new GeometryWrapper(geometry);
    jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(gjg);
    System.out.println(jsonStr);
    GeometryWrapper readGjg = getObjectMapper().readValue(jsonStr, GeometryWrapper.class);
    assertEquals(gjg, readGjg);
    assertTrue(GeometryUtils.equals(geometry, readGjg.getGeometry()));
  }

  /**
   * Test multi polygon.
   *
   * @throws Exception the exception
   */
  @Test
  void testMultiPolygon() throws Exception {
    MultiPolygon geometry = createMultiPolygon();
    System.out.println("Testing " + geometry.toText());
    String jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
        .writeValueAsString(geometry);
    System.out.println(jsonStr);
    MultiPolygon readGeometry = getObjectMapper().readValue(jsonStr, MultiPolygon.class);
    assertTrue(GeometryUtils.equals(geometry, readGeometry));

    GeometryWrapper gjg = new GeometryWrapper(geometry);
    jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(gjg);
    System.out.println(jsonStr);
    GeometryWrapper readGjg = getObjectMapper().readValue(jsonStr, GeometryWrapper.class);
    assertEquals(gjg, readGjg);
    assertTrue(GeometryUtils.equals(geometry, readGjg.getGeometry()));
  }

  /**
   * Test geometry collection.
   *
   * @throws Exception the exception
   */
  @Test
  void testGeometryCollection() throws Exception {
    GeometryCollection geometry = new GeometryCollection(
        new Geometry[]{createLineString(), createMultiLineString()}, getGeometryFactory());
    System.out.println("Testing " + geometry.toText());
    String jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
        .writeValueAsString(geometry);
    System.out.println(jsonStr);
    GeometryCollection readGeometry = getObjectMapper()
        .readValue(jsonStr, GeometryCollection.class);
    assertTrue(GeometryUtils.equals(geometry, readGeometry));

    GeometryWrapper gjg = new GeometryWrapper(geometry);
    jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(gjg);
    System.out.println(jsonStr);
    GeometryWrapper readGjg = getObjectMapper().readValue(jsonStr, GeometryWrapper.class);
    assertEquals(gjg, readGjg);
    assertTrue(GeometryUtils.equals(geometry, readGjg.getGeometry()));
  }

  /**
   * Test geometry wrapper holder.
   *
   * @throws Exception the exception
   */
  @Test
  void testGeometryWrapperHolder() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();

    Geometry geometry = GeometryUtils.createPoint(1., 2.);
    GeometryWrapper wrapper = new GeometryWrapper(geometry);
    GeometryWrapperHolder holder = new GeometryWrapperHolder();
    holder.id = "test";
    holder.geometry = wrapper;
    String jsonStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(holder);
    System.out.println(jsonStr);

    GeometryWrapperHolder readHolder = objectMapper
        .readValue(jsonStr, GeometryWrapperHolder.class);
    assertEquals(holder.geometry, readHolder.geometry);

    geometry = GeometryUtils.createLineString(null);
    wrapper = new GeometryWrapper(geometry);
    holder.geometry = wrapper;
    jsonStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(holder);
    System.out.println(jsonStr);

    readHolder = objectMapper
        .readValue(jsonStr, GeometryWrapperHolder.class);
    assertEquals(holder.geometry.getGeometry(), readHolder.geometry.getGeometry());

    holder.geometry = null;
    jsonStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(holder);
    System.out.println(jsonStr);

    readHolder = objectMapper
        .readValue(jsonStr, GeometryWrapperHolder.class);
    assertNull(readHolder.geometry);

  }

  /**
   * Test geo json linked crs.
   *
   * @throws Exception the exception
   */
  @Test
  void testGeoJsonLinkedCrs() throws Exception {
    GeoJsonLinkedCrs crs = new GeoJsonLinkedCrs("https://example.com/crs/42", "proj4");
    System.out.println("Testing " + crs);
    String json = getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(crs);
    System.out.println(json);
    GeoJsonLinkedCrs readCrs = getObjectMapper().readValue(json, GeoJsonLinkedCrs.class);
    assertEquals(crs, readCrs);
  }

  /**
   * Test geo json named crs.
   *
   * @throws Exception the exception
   */
  @Test
  void testGeoJsonNamedCrs() throws Exception {
    GeoJsonNamedCrs crs = new GeoJsonNamedCrs("urn:ogc:def:crs:OGC:1.3:CRS84");
    System.out.println("Testing " + crs);
    String json = getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(crs);
    System.out.println(json);
    GeoJsonNamedCrs readCrs = getObjectMapper().readValue(json, GeoJsonNamedCrs.class);
    assertEquals(crs, readCrs);
  }

  /**
   * Test geo json feature.
   *
   * @throws Exception the exception
   */
  @Test
  void testGeoJsonFeature() throws Exception {
    GeometryCollection geometry = new GeometryCollection(
        new Geometry[]{createLineString(), createMultiLineString()}, getGeometryFactory());

    GeoJsonFeature f = new GeoJsonFeature();
    f.setGeometry(geometry);
    f.setBbox(GeometryUtils.getBoundingBox(geometry));
    f.unknown("crs", new GeoJsonNamedCrs("urn:ogc:def:crs:OGC:1.3:CRS84"));
    f.setId("100");
    f.getProperties().put("myKey", "myValue");

    System.out.println("Testing " + f);

    String json = getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(f);

    System.out.println(json);

    GeoJsonFeature readF = getObjectMapper().readValue(json, GeoJsonFeature.class);

    System.out
        .println(getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(readF));

    assertEquals(f, readF);
    assertEquals(f.findUnknown("crs", GeoJsonNamedCrs.class),
        readF.findUnknown("crs", GeoJsonNamedCrs.class));
  }

  /**
   * Test geo json feature collection.
   *
   * @throws Exception the exception
   */
  @Test
  void testGeoJsonFeatureCollection() throws Exception {
    GeometryCollection geometry = new GeometryCollection(
        new Geometry[]{createLineString(), createMultiLineString()}, getGeometryFactory());

    GeoJsonFeature f1 = new GeoJsonFeature();
    f1.setGeometry(geometry);
    f1.setBbox(GeometryUtils.getBoundingBox(geometry));
    //f1.setCrs(new GeoJsonNamedCrs("urn:ogc:def:crs:OGC:1.3:CRS84"));
    f1.setId("101");
    f1.getProperties().put("myKey1", "myValue1");

    GeoJsonFeature f2 = new GeoJsonFeature();
    f2.setGeometry(createMultiPolygon());
    f2.setBbox(GeometryUtils.getBoundingBox(geometry));
    //f2.setCrs(new GeoJsonNamedCrs("urn:ogc:def:crs:OGC:1.3:CRS84"));
    f2.setId("102");
    f2.getProperties().put("myKey2", "myValue2");

    ArrayList<GeoJsonFeature> features = new ArrayList<>();
    features.add(f1);
    features.add(f2);
    GeoJsonFeatureCollection fc = new GeoJsonFeatureCollection(features, true);

    System.out.println("Testing " + fc);

    String json = getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(fc);

    System.out.println(json);

    GeoJsonFeatureCollection readFc = getObjectMapper()
        .readValue(json, GeoJsonFeatureCollection.class);

    System.out
        .println(getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(readFc));

    assertEquals(fc, readFc);
  }

  /**
   * Test abstract geo json crs.
   *
   * @throws Exception the exception
   */
  @Test
  void testAbstractGeoJsonCrs() throws Exception {
    System.out.println("Testing AbstractGeoJsonCrs...");
    GeoJsonNamedCrs named = new GeoJsonNamedCrs("A_CRS_NAME");
    String jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(named);
    System.out.println(jsonStr);
    AbstractGeoJsonCrs crs = getObjectMapper().readValue(jsonStr, AbstractGeoJsonCrs.class);
    assertEquals(named, crs);
  }

  /**
   * Test bounding box.
   */
  @Test
  void testBoundingBox() {
    double[] actual = GeometryUtils.getBoundingBox((Geometry) null);
    assertNull(actual);

    Point point = createPoint(0.1, 0.2);
    actual = GeometryUtils.getBoundingBox(point);
    assertNotNull(actual);
    assertEquals(4, actual.length);
    assertArrayEquals(new double[]{0.1, 0.2, 0.1, 0.2}, actual, 0.);

    point = GeometryUtils.createPoint(new Coordinate(0.1, 0.2, 0.3));
    actual = GeometryUtils.getBoundingBox(point);
    assertNotNull(actual);
    assertEquals(6, actual.length);
    assertArrayEquals(new double[]{0.1, 0.2, 0.3, 0.1, 0.2, 0.3}, actual, 0.);

    LineString lineString = (LineString) GeometryUtils.fromWKT("LINESTRING (3 2, 0.1 5.5)");
    actual = GeometryUtils.getBoundingBox(lineString);
    assertNotNull(actual);
    assertEquals(4, actual.length);
    assertArrayEquals(new double[]{0.1, 2., 3., 5.5}, actual, 0.);

    lineString = (LineString) GeometryUtils.fromWKT("LINESTRING (3 2 4, 0.1 5.5 1)");
    actual = GeometryUtils.getBoundingBox(lineString);
    assertNotNull(actual);
    assertEquals(6, actual.length);
    assertArrayEquals(new double[]{0.1, 2., 1., 3., 5.5, 4.}, actual, 0.);
  }

  /**
   * The geometry wrapper holder.
   */
  static class GeometryWrapperHolder {

    /**
     * The Id.
     */
    @JsonProperty("id")
    String id;

    /**
     * The Geometry.
     */
    @JsonProperty("geometry")
    GeometryWrapper geometry;
  }

}

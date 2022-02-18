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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * The geo json object mapper module test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class GeoJsonObjectMapperModuleTest {

  private static final GeoJsonGeometryFactory geometryFactory = new GeoJsonGeometryFactory();

  private static final ObjectMapper target = JsonMapper.builder().build();

  /**
   * Init.
   */
  @BeforeAll
  static void init() {
    target.registerModule(new GeoJsonObjectMapperModule(geometryFactory, true, true));
  }

  /**
   * Configure.
   *
   * @param softly the softly
   */
  @Test
  void configure(SoftAssertions softly) {
    ObjectMapper om = JsonMapper.builder().build();
    om.registerModule(new GeoJsonObjectMapperModule());
    softly.assertThat(om.getRegisteredModuleIds())
        .anyMatch(module -> GeoJsonObjectMapperModule.TYPE_ID
            .equals(String.valueOf(module)));

    om = new ObjectMapper();
    om.registerModule(new GeoJsonObjectMapperModule(false, true));
    softly.assertThat(om.getRegisteredModuleIds())
        .anyMatch(module -> GeoJsonObjectMapperModule.TYPE_ID
            .equals(String.valueOf(module)));

    om = new ObjectMapper();
    om.registerModule(new GeoJsonObjectMapperModule(new GeoJsonGeometryFactory()));
    softly.assertThat(om.getRegisteredModuleIds())
        .anyMatch(module -> GeoJsonObjectMapperModule.TYPE_ID
            .equals(String.valueOf(module)));

    om = new ObjectMapper();
    om.registerModule(new GeoJsonObjectMapperModule(new GeoJsonGeometryFactory(), true, false));
    softly.assertThat(om.getRegisteredModuleIds())
        .anyMatch(module -> GeoJsonObjectMapperModule.TYPE_ID
            .equals(String.valueOf(module)));
  }

  /**
   * Map point.
   *
   * @throws Exception the exception
   */
  @Test
  void mapPoint() throws Exception {
    var expected = geometryFactory.createPoint(1.234, 5.6789);
    var json = target.writerWithDefaultPrettyPrinter().writeValueAsString(expected);
    System.out.println(json);

    var actual = target.readValue(json, Point.class);
    //noinspection unchecked
    assertThat(actual).isEqualTo(expected);
  }

  /**
   * Map line string.
   *
   * @throws Exception the exception
   */
  @Test
  void mapLineString() throws Exception {
    var expected = geometryFactory.createGeometryFromWellKnownText(
        "LINESTRING (0 0, 0 1, 1 1, 1 0, 0 0)");
    var json = target.writerWithDefaultPrettyPrinter().writeValueAsString(expected);
    System.out.println(json);

    var actual = target.readValue(json, LineString.class);
    //noinspection unchecked
    assertThat(actual).isEqualTo(expected);
  }

  /**
   * Map polygon.
   *
   * @throws Exception the exception
   */
  @Test
  void mapPolygon() throws Exception {
    var expected = geometryFactory.createGeometryFromWellKnownText(
        "POLYGON ((0 0, 0 1, 1 1, 1 0, 0 0))");
    var json = target.writerWithDefaultPrettyPrinter().writeValueAsString(expected);
    System.out.println(json);

    var actual = target.readValue(json, Polygon.class);
    //noinspection unchecked
    assertThat(actual).isEqualTo(expected);
  }

  /**
   * Map multi point.
   *
   * @throws Exception the exception
   */
  @Test
  void mapMultiPoint() throws Exception {
    var expected = geometryFactory.createGeometryFromWellKnownText(
        "MULTIPOINT ((1 2), (6 7))");
    var json = target.writerWithDefaultPrettyPrinter().writeValueAsString(expected);
    System.out.println(json);

    var actual = target.readValue(json, MultiPoint.class);
    //noinspection unchecked
    assertThat(actual).isEqualTo(expected);
  }

  /**
   * Map multi line string.
   *
   * @throws Exception the exception
   */
  @Test
  void mapMultiLineString() throws Exception {
    var expected = geometryFactory.createGeometryFromWellKnownText(
        "MULTILINESTRING ((0 0, 0 1, 1 1, 1 0, 0 0), (2 2, 2 4, 4 4, 4 2, 2 2))");
    var json = target.writerWithDefaultPrettyPrinter().writeValueAsString(expected);
    System.out.println(json);

    var actual = target.readValue(json, MultiLineString.class);
    //noinspection unchecked
    assertThat(actual).isEqualTo(expected);
  }

  /**
   * Map multi polygon.
   *
   * @throws Exception the exception
   */
  @Test
  void mapMultiPolygon() throws Exception {
    var expected = geometryFactory.createGeometryFromWellKnownText(
        "MULTIPOLYGON (((0 0, 0 1, 1 1, 1 0, 0 0)), ((0 0, 0 1, 1 1, 1 0, 0 0), "
            + "(0.1 0.1, 0.1 0.2, 0.2 0.2, 0.2 0.1, 0.1 0.1), "
            + "(0.8 0.8, 0.8 0.9, 0.9 0.9, 0.9 0.8, 0.8 0.8)))");
    var json = target.writerWithDefaultPrettyPrinter().writeValueAsString(expected);
    System.out.println(json);

    var actual = target.readValue(json, MultiPolygon.class);
    //noinspection unchecked
    assertThat(actual).isEqualTo(expected);
  }

  /**
   * Map geometry collection.
   *
   * @throws Exception the exception
   */
  @Test
  void mapGeometryCollection() throws Exception {
    var expected = geometryFactory.createGeometryFromWellKnownText(
        "GEOMETRYCOLLECTION (POINT (-17.8 -6.001), LINESTRING (0 0, 0 1, 1 1, 1 0, 0 0), "
            + "POLYGON ((0 0, 0 1, 1 1, 1 0, 0 0)), MULTIPOINT ((5.5 -3.3)), "
            + "MULTILINESTRING ((0 0, 0 1, 1 1, 1 0, 0 0), (2 2, 2 4, 4 4, 4 2, 2 2)), "
            + "MULTIPOLYGON (((0 0, 0 1, 1 1, 1 0, 0 0)), ((0 0, 0 1, 1 1, 1 0, 0 0), "
            + "(0.1 0.1, 0.1 0.2, 0.2 0.2, 0.2 0.1, 0.1 0.1), "
            + "(0.8 0.8, 0.8 0.9, 0.9 0.9, 0.9 0.8, 0.8 0.8))), "
            + "GEOMETRYCOLLECTION (POINT (17.8 6.001)))");
    var json = target.writerWithDefaultPrettyPrinter().writeValueAsString(expected);
    System.out.println(json);

    var actual = target.readValue(json, GeometryCollection.class);
    assertThat(GeoJsonGeometryFactory.equals(actual, expected))
        .isTrue();
  }

  /**
   * Map feature.
   *
   * @throws Exception the exception
   */
  @Test
  void mapFeature() throws Exception {
    var lineString = geometryFactory.createGeometryFromWellKnownText(
        "LINESTRING (0 0, 0 1, 1 1, 1 0, 0 0)");
    var expected = new GeoJsonFeature<Geometry, Map<String, Object>>(
        "987",
        lineString,
        true,
        Map.of("foo", "bar"));
    var json = target.writerWithDefaultPrettyPrinter().writeValueAsString(expected);
    System.out.println(json);
    GeoJsonFeature<Geometry, Map<String, Object>> actual = target
        .readValue(json, new TypeReference<>() {
        });
    assertThat(actual)
        .isEqualTo(expected);
  }

  /**
   * Map feature collection.
   *
   * @throws Exception the exception
   */
  @Test
  void mapFeatureCollection() throws Exception {
    var lineString = geometryFactory.createGeometryFromWellKnownText(
        "LINESTRING (0 0, 0 1, 1 1, 1 0, 0 0)");
    var f0 = new GeoJsonFeature<Geometry, Object>(
        "986",
        lineString,
        false,
        Map.of("foo", "bar"));
    var f1 = new GeoJsonFeature<Geometry, Object>(
        "987",
        lineString,
        false,
        Map.of("foo", "bar"));
    var expected = new GeoJsonFeatureCollection<>(List.of(f0, f1), true);
    var json = target.writerWithDefaultPrettyPrinter().writeValueAsString(expected);
    System.out.println(json);
    GeoJsonFeatureCollection<Geometry, Object> actual = target
        .readValue(json, new TypeReference<>() {
        });
    assertThat(actual)
        .isEqualTo(expected);
  }

}
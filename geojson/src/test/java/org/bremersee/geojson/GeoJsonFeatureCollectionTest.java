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
import static org.bremersee.geojson.GeoJsonConstants.FEATURE_COLLECTION;

import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;

/**
 * The GeoJSON feature collection test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class GeoJsonFeatureCollectionTest {

  private static final GeoJsonGeometryFactory factory = new GeoJsonGeometryFactory();

  /**
   * Gets type.
   */
  @Test
  void getType() {
    GeoJsonFeatureCollection<Point, Object> model = new GeoJsonFeatureCollection<>(false);
    assertThat(model.getType()).isEqualTo(FEATURE_COLLECTION);
  }

  /**
   * Gets bounding box.
   *
   * @param softly the softly
   */
  @Test
  void getBoundingBox(SoftAssertions softly) {
    double[] value = new double[]{1., 1., 10., 10.};
    GeoJsonFeatureCollection<Geometry, Object> model = new GeoJsonFeatureCollection<>(value, null);

    softly.assertThat(model.getBbox()).containsExactly(value);

    softly.assertThat(model)
        .isEqualTo(model);
    softly.assertThat(model.hashCode())
        .isEqualTo(model.hashCode());
    softly.assertThat(model)
        .isNotEqualTo(null);
    softly.assertThat(model)
        .isNotEqualTo(new Object());
    softly.assertThat(model.toString())
        .contains(Arrays.toString(value));

    MultiPoint multiPoint = factory.createMultiPoint(List.of(
        factory.createPoint(1., 1.),
        factory.createPoint(5, 5.)
    ));
    GeoJsonFeature<Geometry, Object> f0 = new GeoJsonFeature<>(null, multiPoint, false, null);
    Point point = factory.createPoint(10., 10.);
    GeoJsonFeature<Geometry, Object> f1 = new GeoJsonFeature<>(null, point, false, null);
    model = new GeoJsonFeatureCollection<>(FEATURE_COLLECTION, value, List.of(f0, f1));
    softly.assertThat(model)
        .isEqualTo(new GeoJsonFeatureCollection<>(List.of(f0, f1), true, null));

    double[] illegal = new double[]{1., 1., 10.};
    softly.assertThatThrownBy(() -> new GeoJsonFeatureCollection<>(illegal, null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  /**
   * Gets features.
   *
   * @param softly the softly
   */
  @Test
  void getFeatures(SoftAssertions softly) {
    Point point = factory.createPoint(10., 10.);
    GeoJsonFeature<Geometry, Object> value = new GeoJsonFeature<>(null, point, false, null);
    GeoJsonFeatureCollection<Geometry, Object> model = new GeoJsonFeatureCollection<>(
        List.of(value), false);

    softly.assertThat(model.getFeatures()).containsExactly(value);

    softly.assertThat(model)
        .isEqualTo(model);
    softly.assertThat(model.hashCode())
        .isEqualTo(model.hashCode());
    softly.assertThat(model)
        .isNotEqualTo(null);
    softly.assertThat(model)
        .isNotEqualTo(new Object());
    softly.assertThat(model.toString())
        .contains(value.toString());
  }

  /**
   * Add.
   *
   * @param softly the softly
   */
  @Test
  void add(SoftAssertions softly) {
    GeoJsonFeatureCollection<Geometry, Object> target = new GeoJsonFeatureCollection<>(
        true);
    Point point = factory.createPoint(10., 10.);
    GeoJsonFeature<Geometry, Object> value = new GeoJsonFeature<>(null, point, false, null);
    target.add(value);
    softly.assertThat(target.getFeatures())
        .containsExactly(value);
  }

  /**
   * Add all.
   *
   * @param softly the softly
   */
  @Test
  void addAll(SoftAssertions softly) {
    GeoJsonFeatureCollection<Geometry, Object> target = new GeoJsonFeatureCollection<>(
        true, (o1, o2) -> o1.getId().compareToIgnoreCase(o2.getId()));
    Point point = factory.createPoint(10., 10.);
    GeoJsonFeature<Geometry, Object> value0 = new GeoJsonFeature<>("0", point, false, null);
    GeoJsonFeature<Geometry, Object> value1 = new GeoJsonFeature<>("1", point, false, null);
    target.addAll(List.of(value1, value0));
    softly.assertThat(target.getFeatures())
        .containsExactly(value0, value1);
  }

}
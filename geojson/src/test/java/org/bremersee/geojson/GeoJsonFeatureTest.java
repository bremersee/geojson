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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;

/**
 * The GeoJSON feature test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class GeoJsonFeatureTest {

  private static final GeoJsonGeometryFactory factory = new GeoJsonGeometryFactory();

  /**
   * Gets type.
   */
  @Test
  void getType() {
    GeoJsonFeature<Point, Object> model = new GeoJsonFeature<>(null, null, null, null);
    assertThat(model.getType()).isEqualTo(GeoJsonConstants.FEATURE);
  }

  /**
   * Gets id.
   *
   * @param softly the softly
   */
  @Test
  void getId(SoftAssertions softly) {
    String value = "123456789";
    GeoJsonFeature<Point, Object> model = new GeoJsonFeature<>(value, null, null, null);

    softly.assertThat(model.getId()).isEqualTo(value);

    softly.assertThat(model)
        .isEqualTo(model);
    softly.assertThat(model.hashCode())
        .isEqualTo(model.hashCode());
    softly.assertThat(model)
        .isNotEqualTo(null);
    softly.assertThat(model)
        .isNotEqualTo(new Object());
    softly.assertThat(model.toString())
        .contains(value);

    softly.assertThat(model)
        .isEqualTo(new GeoJsonFeature<>(value, null, false, null));
  }

  /**
   * Gets bounding box.
   *
   * @param softly the softly
   */
  @Test
  void getBoundingBox(SoftAssertions softly) {
    String id = "123456789";
    double[] value = new double[]{1., 1., 10., 10.};
    GeoJsonFeature<MultiPoint, Object> model = new GeoJsonFeature<>(id, value, null, null);

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
        factory.createPoint(10., 10.)
    ));
    model = new GeoJsonFeature<>(id, value, multiPoint, null);
    softly.assertThat(model)
        .isEqualTo(new GeoJsonFeature<>(id, multiPoint, true, null));

    double[] illegal = new double[]{1., 1., 10.};
    softly.assertThatThrownBy(() -> new GeoJsonFeature<>(id, illegal, null, null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  /**
   * Gets geometry.
   *
   * @param softly the softly
   */
  @Test
  void getGeometry(SoftAssertions softly) {
    String id = "123456789";
    MultiPoint value = factory.createMultiPoint(List.of(
        factory.createPoint(1., 1.),
        factory.createPoint(10., 10.)
    ));
    GeoJsonFeature<MultiPoint, Object> model = new GeoJsonFeature<>(id, null, value, null);

    softly.assertThat(GeoJsonGeometryFactory.equals(model.getGeometry(), value))
        .isTrue();

    softly.assertThat(model)
        .isEqualTo(model);
    softly.assertThat(model.hashCode())
        .isEqualTo(model.hashCode());
    softly.assertThat(model)
        .isNotEqualTo(null);
    softly.assertThat(model)
        .isNotEqualTo(new Object());
    softly.assertThat(model.toString())
        .contains(value.toText());

    softly.assertThat(model)
        .isEqualTo(new GeoJsonFeature<>(id, value, false, null));
  }

  /**
   * Gets properties.
   *
   * @param softly the softly
   */
  @Test
  void getProperties(SoftAssertions softly) {
    Map<String, Object> value = Map.of("junit", "123456789");
    GeoJsonFeature<Point, Object> model = new GeoJsonFeature<>(null, null, null, value);

    softly.assertThat(model.getProperties()).isEqualTo(value);

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

    softly.assertThat(model)
        .isEqualTo(new GeoJsonFeature<>(null, null, false, value));
  }

}
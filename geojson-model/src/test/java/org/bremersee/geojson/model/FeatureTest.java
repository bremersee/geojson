/*
 * Copyright 2018-2022 the original author or authors.
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

package org.bremersee.geojson.model;

import java.math.BigDecimal;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.geojson.GeoJsonConstants;
import org.bremersee.geojson.model.Feature.TypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The feature test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class FeatureTest {

  /**
   * Gets id.
   *
   * @param softly the soft assertions
   */
  @Test
  void getId(SoftAssertions softly) {
    Feature model = new Feature();
    model.setId("value");
    softly.assertThat(model.getId()).isEqualTo("value");

    model = Feature.builder().id("value").build();
    softly.assertThat(model.getId()).isEqualTo("value");

    softly.assertThat(model.toBuilder().id("value").build()).isEqualTo(model);

    softly.assertThat(model.toString()).contains("value");
  }

  /**
   * Gets bbox.
   *
   * @param softly the soft assertions
   */
  @Test
  void getBbox(SoftAssertions softly) {
    BoundingBox value = new BoundingBox(
        List.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
    Feature model = new Feature();
    model.setBbox(value);
    softly.assertThat(model.getBbox()).isEqualTo(value);

    model = Feature.builder().bbox(value).build();
    softly.assertThat(model.getBbox()).isEqualTo(value);

    softly.assertThat(model.toBuilder().bbox(value).build()).isEqualTo(model);
  }

  /**
   * Gets geometry.
   *
   * @param softly the soft assertions
   */
  @Test
  void getGeometry(SoftAssertions softly) {
    Geometry value = Point.builder()
        .coordinates(new Position(BigDecimal.ONE, BigDecimal.ZERO))
        .build();
    Feature model = new Feature();
    model.setGeometry(value);
    softly.assertThat(model.getGeometry()).isEqualTo(value);

    model = Feature.builder().geometry(value).build();
    softly.assertThat(model.getGeometry()).isEqualTo(value);

    softly.assertThat(model.toBuilder().geometry(value).build()).isEqualTo(model);

    softly.assertThat(model.toString()).contains(value.toString());
  }

  /**
   * Gets properties.
   *
   * @param softly the soft assertions
   */
  @Test
  void getProperties(SoftAssertions softly) {
    Feature model = new Feature();
    model.setProperties("value");
    softly.assertThat(model.getProperties()).isEqualTo("value");

    model = Feature.builder().properties("value").build();
    softly.assertThat(model.getProperties()).isEqualTo("value");

    softly.assertThat(model.toBuilder().properties("value").build()).isEqualTo(model);

    softly.assertThat(model.toString()).contains("value");
  }

  /**
   * Test type.
   *
   * @param softly the softly
   */
  @Test
  void testType(SoftAssertions softly) {
    softly.assertThat(TypeEnum.FEATURE.toString())
        .isEqualTo(GeoJsonConstants.FEATURE);
    softly.assertThat(TypeEnum.fromValue(GeoJsonConstants.FEATURE))
        .isEqualTo(TypeEnum.FEATURE);
    softly.assertThatThrownBy(() -> TypeEnum.fromValue("illegal"))
        .isInstanceOf(IllegalArgumentException.class);
  }

}
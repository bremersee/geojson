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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.geojson.GeoJsonConstants;
import org.bremersee.geojson.model.FeatureCollection.TypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The feature collection test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class FeatureCollectionTest {

  /**
   * Gets bbox.
   *
   * @param softly the soft assertions
   */
  @Test
  void getBbox(SoftAssertions softly) {
    BoundingBox value = new BoundingBox(
        Arrays.asList(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
    FeatureCollection model = new FeatureCollection();
    model.setBbox(value);
    softly.assertThat(model.getBbox()).isEqualTo(value);

    model = FeatureCollection.builder().bbox(value).build();
    softly.assertThat(model.getBbox()).isEqualTo(value);

    softly.assertThat(model.toBuilder().bbox(value).build()).isEqualTo(model);
  }

  /**
   * Gets features.
   *
   * @param softly the soft assertions
   */
  @Test
  void getFeatures(SoftAssertions softly) {
    Geometry geometry = Point.builder()
        .coordinates(new Position(BigDecimal.ONE, BigDecimal.ZERO))
        .build();
    Feature feature = Feature.builder().geometry(geometry).build();
    List<Feature> value = Collections.singletonList(feature);
    FeatureCollection model = new FeatureCollection();
    model.setFeatures(value);
    softly.assertThat(model.getFeatures()).isEqualTo(value);

    model = FeatureCollection.builder().features(value).build();
    softly.assertThat(model.getFeatures()).isEqualTo(value);

    softly.assertThat(model.toBuilder().features(value).build()).isEqualTo(model);

    softly.assertThat(model.toString()).contains(value.toString());
  }

  /**
   * Test type.
   *
   * @param softly the softly
   */
  @Test
  void testType(SoftAssertions softly) {
    softly.assertThat(TypeEnum.FEATURE_COLLECTION.toString())
        .isEqualTo(GeoJsonConstants.FEATURE_COLLECTION);
    softly.assertThat(TypeEnum.fromValue(GeoJsonConstants.FEATURE_COLLECTION))
        .isEqualTo(TypeEnum.FEATURE_COLLECTION);
    softly.assertThatThrownBy(() -> TypeEnum.fromValue("illegal"))
        .isInstanceOf(IllegalArgumentException.class);
  }

}
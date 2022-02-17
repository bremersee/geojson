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

import static org.bremersee.geojson.GeoJsonConstants.FEATURE_COLLECTION;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
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
   * Gets type.
   *
   * @param softly the soft assertions
   */
  @Test
  void getType(SoftAssertions softly) {
    FeatureCollection model = new FeatureCollection();
    model.setType(FEATURE_COLLECTION);
    softly.assertThat(model.getType())
        .isEqualTo(FEATURE_COLLECTION);
    softly.assertThatThrownBy(() -> model.setType("illegal"))
        .isInstanceOf(IllegalArgumentException.class);

    softly.assertThat(model).isNotEqualTo(null);
    softly.assertThat(model).isNotEqualTo(new Object());
    softly.assertThat(model).isEqualTo(model);
    softly.assertThat(model).isEqualTo(new FeatureCollection());
    softly.assertThat(model.hashCode()).isEqualTo(new FeatureCollection().hashCode());

    softly.assertThat(model.toString()).contains(FEATURE_COLLECTION);
  }

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

}
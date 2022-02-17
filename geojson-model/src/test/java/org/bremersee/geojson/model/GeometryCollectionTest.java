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

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The geometry collection test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class GeometryCollectionTest {

  /**
   * Gets geometries.
   *
   * @param softly the soft assertions
   */
  @Test
  void getGeometries(SoftAssertions softly) {
    List<Geometry> value = Arrays.asList(
        Point.builder().coordinates(new Position(BigDecimal.ONE, BigDecimal.ONE)).build(),
        Point.builder().coordinates(new Position(BigDecimal.TEN, BigDecimal.ZERO)).build());
    GeometryCollection model = new GeometryCollection();
    model.setGeometries(value);
    softly.assertThat(model.getGeometries()).isEqualTo(value);
    softly.assertThat(model.getGeometryJsonValue())
        .isEqualTo(value.stream().map(Geometry::toJson).collect(Collectors.toList()));

    model = GeometryCollection.builder().geometries(value).build();
    softly.assertThat(model.getGeometries()).isEqualTo(value);

    softly.assertThat(model.toBuilder().geometries(value).build()).isEqualTo(model);

    softly.assertThat(model).isNotEqualTo(null);
    softly.assertThat(model).isNotEqualTo(new Object());
    softly.assertThat(model).isEqualTo(model);

    softly.assertThat(model.toString()).contains(value.toString());
  }

  /**
   * Gets type.
   */
  @Test
  void getType() {
    GeometryCollection model = new GeometryCollection();
    assertThat(model.getType().toString()).isEqualTo("GeometryCollection");
  }

  /**
   * Gets bbox.
   *
   * @param softly the soft assertions
   */
  @Test
  void getBbox(SoftAssertions softly) {
    BoundingBox value = new BoundingBox(
        Arrays.asList(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE));
    GeometryCollection model = new GeometryCollection();
    model.setBbox(value);
    softly.assertThat(model.getBbox()).isEqualTo(value);

    model = GeometryCollection.builder().bbox(value).build();
    softly.assertThat(model.getBbox()).isEqualTo(value);

    softly.assertThat(model.toBuilder().bbox(value).build()).isEqualTo(model);
    softly.assertThat(new GeometryCollection(value, null)).isEqualTo(model);
  }

}
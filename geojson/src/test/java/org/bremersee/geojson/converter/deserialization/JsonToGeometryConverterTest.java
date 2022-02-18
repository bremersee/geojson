/*
 * Copyright 2022 the original author or authors.
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

package org.bremersee.geojson.converter.deserialization;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.bremersee.geojson.GeoJsonConstants.GEOMETRY_COLLECTION;
import static org.bremersee.geojson.GeoJsonConstants.TYPE;

import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Geometry;

/**
 * The type Json to geometry converter test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class JsonToGeometryConverterTest {

  /**
   * Convert and expect empty geometry.
   *
   * @param softly the softly
   */
  @Test
  void convertAndExpectEmptyGeometry(SoftAssertions softly) {
    JsonToGeometryConverter target = new JsonToGeometryConverter();
    Geometry actual = target.convert(Map.of(TYPE, GEOMETRY_COLLECTION));
    //noinspection unchecked
    softly.assertThat(actual)
        .isNotNull();
    softly.assertThat(actual.getCoordinates())
        .isEmpty();
  }

  /**
   * Convert and expect illegal argument exception.
   */
  @Test
  void convertAndExpectIllegalArgumentException() {
    JsonToGeometryConverter target = new JsonToGeometryConverter();
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> target.convert(Map.of("IllegalGeometry", new Object())));
  }
}
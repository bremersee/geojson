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

package org.bremersee.geojson.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.junit.jupiter.api.Test;

/**
 * The geometry converters test.
 *
 * @author Christian Bremer
 */
class GeometryConvertersTest {

  /**
   * Gets converters to register.
   */
  @Test
  void getConvertersToRegister() {
    assertThat(GeometryConverters.getConvertersToRegister(new GeoJsonGeometryFactory()))
        .isNotEmpty();
  }
}
/*
 * Copyright 2018-2020 the original author or authors.
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

package org.bremersee.geojson.spring.data.mongodb.convert;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collection;
import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.core.convert.converter.Converter;

/**
 * The GeoJSON converters test.
 *
 * @author Christian Bremer
 */
class GeoJsonConvertersTest {

  /**
   * Gets converters to register.
   */
  @Test
  void getConvertersToRegister() {
    Collection<? extends Converter<?, ?>> converters = GeoJsonConverters
        .getConvertersToRegister(new GeoJsonGeometryFactory());
    assertNotNull(converters);
  }
}
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

package org.bremersee.geojson.boot.data.mongodb;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.bremersee.geojson.spring.data.mongodb.convert.GeoJsonConverters;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.convert.converter.Converter;

/**
 * The geo json mongo custom conversions' provider test.
 *
 * @author Christian Bremer
 */
class GeoJsonMongoCustomConversionsProviderTest {

  private GeoJsonMongoCustomConversionsProvider newInstance() {
    //noinspection unchecked
    ObjectProvider<GeoJsonGeometryFactory> objectProvider = mock(ObjectProvider.class);
    when(objectProvider.getIfAvailable(any())).thenReturn(new GeoJsonGeometryFactory());
    return new GeoJsonMongoCustomConversionsProvider(objectProvider);
  }

  /**
   * Init.
   */
  @Test
  void init() {
    GeoJsonMongoCustomConversionsProvider target = newInstance();
    target.init();
  }

  /**
   * Gets custom conversions.
   */
  @Test
  void getCustomConversions() {
    GeoJsonMongoCustomConversionsProvider target = newInstance();
    List<Converter<?, ?>> actual = target.getCustomConversions();
    Assertions.assertThat(actual)
        .isNotNull()
        .hasSize(GeoJsonConverters.getConvertersToRegister(new GeoJsonGeometryFactory()).size());
  }
}
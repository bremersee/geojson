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

package org.bremersee.geojson.spring.boot.autoconfigure.jackson;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.bremersee.geojson.spring.boot.autoconfigure.GeoJsonProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * The geo json jackson 2 object mapper builder customizer test.
 *
 * @author Christian Bremer
 */
class GeoJsonJackson2ObjectMapperBuilderCustomizerTest {

  private GeoJsonJackson2ObjectMapperBuilderCustomizer newInstance(GeoJsonProperties properties) {
    //noinspection unchecked
    ObjectProvider<GeoJsonGeometryFactory> objectProvider = mock(ObjectProvider.class);
    when(objectProvider.getIfAvailable(any())).thenReturn(new GeoJsonGeometryFactory());
    return new GeoJsonJackson2ObjectMapperBuilderCustomizer(properties, objectProvider);
  }

  /**
   * Init.
   */
  @Test
  void init() {
    GeoJsonProperties properties = new GeoJsonProperties();
    properties.setUseBigDecimal(true);
    properties.setWithBoundingBox(true);
    properties = spy(properties);
    GeoJsonJackson2ObjectMapperBuilderCustomizer target = newInstance(properties);
    target.init();
    verify(properties, atLeast(1)).isUseBigDecimal();
    verify(properties, atLeast(1)).isWithBoundingBox();
  }

  /**
   * Customize.
   */
  @Test
  void customize() {
    GeoJsonProperties properties = new GeoJsonProperties();
    properties.setUseBigDecimal(false);
    properties.setWithBoundingBox(false);
    properties = spy(properties);
    GeoJsonJackson2ObjectMapperBuilderCustomizer target = newInstance(properties);
    Jackson2ObjectMapperBuilder builder = mock(Jackson2ObjectMapperBuilder.class);
    target.customize(builder);
    //noinspection unchecked
    verify(builder).postConfigurer(any(Consumer.class));
    verify(properties, atLeast(1)).isUseBigDecimal();
    verify(properties, atLeast(1)).isWithBoundingBox();
  }
}
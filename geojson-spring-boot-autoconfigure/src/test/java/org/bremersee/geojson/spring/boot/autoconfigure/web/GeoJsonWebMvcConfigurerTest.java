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

package org.bremersee.geojson.spring.boot.autoconfigure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.bremersee.geojson.converter.GeometryConverters;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;

/**
 * The geo json web mvc configurer test.
 *
 * @author Christian Bremer
 */
class GeoJsonWebMvcConfigurerTest {

  private GeoJsonWebMvcConfigurer newInstance() {
    //noinspection unchecked
    ObjectProvider<GeoJsonGeometryFactory> objectProvider = mock(ObjectProvider.class);
    when(objectProvider.getIfAvailable(any())).thenReturn(new GeoJsonGeometryFactory());
    return new GeoJsonWebMvcConfigurer(objectProvider);
  }

  /**
   * Init.
   */
  @Test
  void init() {
    GeoJsonWebMvcConfigurer target = newInstance();
    target.init();
  }

  /**
   * Add formatters.
   */
  @Test
  void addFormatters() {
    GeoJsonWebMvcConfigurer target = newInstance();
    FormatterRegistry formatterRegistry = mock(FormatterRegistry.class);
    target.addFormatters(formatterRegistry);
    int wantedNumberOfInvocations = GeometryConverters
        .getConvertersToRegister(new GeoJsonGeometryFactory())
        .size();
    verify(formatterRegistry, times(wantedNumberOfInvocations))
        .addConverter(any(Converter.class));
  }
}
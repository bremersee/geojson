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

import lombok.extern.slf4j.Slf4j;
import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.bremersee.geojson.converter.GeometryConverters;
import org.bremersee.geojson.spring.boot.autoconfigure.GeoJsonGeometryFactoryAutoConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * The GeoJSON web flux configurer.
 *
 * @author Christian Bremer
 */
@ConditionalOnClass(name = {"org.bremersee.geojson.converter.GeometryConverters"})
@ConditionalOnWebApplication(type = Type.REACTIVE)
@AutoConfiguration
@AutoConfigureAfter(GeoJsonGeometryFactoryAutoConfiguration.class)
@Slf4j
public class GeoJsonWebFluxConfigurer implements WebFluxConfigurer {

  private final GeoJsonGeometryFactory geometryFactory;

  /**
   * Instantiates a new GeoJSON web flux configurer.
   *
   * @param geometryFactory the geometry factory
   */
  public GeoJsonWebFluxConfigurer(ObjectProvider<GeoJsonGeometryFactory> geometryFactory) {
    this.geometryFactory = geometryFactory.getIfAvailable(GeoJsonGeometryFactory::new);
  }

  /**
   * Init.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    log.info("""

            *********************************************************************************
            * {}
            *********************************************************************************""",
        ClassUtils.getUserClass(getClass()).getSimpleName());
  }

  @Override
  public void addFormatters(@NonNull FormatterRegistry registry) {
    GeometryConverters.getConvertersToRegister(geometryFactory)
        .forEach(registry::addConverter);
  }

}

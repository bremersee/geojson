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

package org.bremersee.geojson.spring.boot.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.bremersee.geojson.GeoJsonObjectMapperModule;
import org.bremersee.geojson.spring.boot.GeoJsonGeometryFactoryAutoConfiguration;
import org.bremersee.geojson.spring.boot.GeoJsonProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.ClassUtils;

/**
 * @author Christian Bremer
 */
@ConditionalOnWebApplication
@ConditionalOnClass({
    GeoJsonObjectMapperModule.class,
    Jackson2ObjectMapperBuilder.class,
    ObjectMapper.class})
@Configuration
@AutoConfigureAfter(GeoJsonGeometryFactoryAutoConfiguration.class)
@EnableConfigurationProperties(GeoJsonProperties.class)
@Slf4j
public class GeoJsonJackson2ObjectMapperBuilderCustomizer
    implements Jackson2ObjectMapperBuilderCustomizer {

  private final GeoJsonProperties properties;

  private final GeoJsonGeometryFactory geometryFactory;

  public GeoJsonJackson2ObjectMapperBuilderCustomizer(
      GeoJsonProperties properties,
      ObjectProvider<GeoJsonGeometryFactory> geometryFactory) {
    this.properties = properties;
    this.geometryFactory = geometryFactory.getIfAvailable(GeoJsonGeometryFactory::new);
  }

  /**
   * Init.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    log.info("\n"
            + "*********************************************************************************\n"
            + "* {}\n"
            + "*********************************************************************************\n"
            + "* properties = {}\n"
            + "*********************************************************************************",
        ClassUtils.getUserClass(getClass()).getSimpleName(), properties);
  }

  @Override
  public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
    GeoJsonObjectMapperModule module = new GeoJsonObjectMapperModule(
        geometryFactory,
        properties.isWithBoundingBox(),
        properties.isUseBigDecimal()
    );
    jacksonObjectMapperBuilder.postConfigurer(objectMapper -> objectMapper
        .registerModule(module));
  }
}

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

package org.bremersee.geojson.spring.boot.autoconfigure.data.mongo;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.bremersee.geojson.spring.boot.autoconfigure.GeoJsonGeometryFactoryAutoConfiguration;
import org.bremersee.geojson.spring.data.mongodb.convert.GeoJsonConverters;
import org.bremersee.spring.data.mongodb.core.convert.MongoCustomConversionsProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.convert.converter.Converter;

/**
 * The GeoJSON mongo custom conversions provider.
 *
 * @author Christian Bremer
 */
@ConditionalOnClass(name = {
    "org.bremersee.geojson.GeoJsonGeometryFactory",
    "org.bremersee.geojson.spring.data.mongodb.convert.GeoJsonConverters"
})
@AutoConfigureAfter(GeoJsonGeometryFactoryAutoConfiguration.class)
@AutoConfiguration
@Slf4j
public class GeoJsonMongoCustomConversionsProvider implements MongoCustomConversionsProvider {

  private final GeoJsonGeometryFactory geometryFactory;

  /**
   * Instantiates a new GeoJSON mongo custom conversions provider.
   *
   * @param geometryFactory the geometry factory
   */
  public GeoJsonMongoCustomConversionsProvider(
      ObjectProvider<GeoJsonGeometryFactory> geometryFactory) {
    this.geometryFactory = geometryFactory.getIfAvailable(GeoJsonGeometryFactory::new);
  }

  @Override
  public List<Converter<?, ?>> getCustomConversions() {
    log.info("Providing GeoJSON converters for MongoDB.");
    return GeoJsonConverters.getConvertersToRegister(geometryFactory);
  }
}

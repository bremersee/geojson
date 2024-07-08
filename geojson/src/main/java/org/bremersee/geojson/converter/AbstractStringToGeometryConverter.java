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

import static java.util.Objects.isNull;

import org.bremersee.geojson.GeoJsonGeometryFactory;

/**
 * The abstract string to geometry converter.
 *
 * @author Christian Bremer
 */
abstract class AbstractStringToGeometryConverter {

  private final GeoJsonGeometryFactory geometryFactory;

  /**
   * Instantiates a new abstract string to geometry converter.
   */
  AbstractStringToGeometryConverter() {
    this(new GeoJsonGeometryFactory());
  }

  /**
   * Instantiates a new abstract string to geometry converter.
   *
   * @param geometryFactory the geometry factory
   */
  AbstractStringToGeometryConverter(GeoJsonGeometryFactory geometryFactory) {
    this.geometryFactory = isNull(geometryFactory) ? new GeoJsonGeometryFactory() : geometryFactory;
  }

  /**
   * Gets geometry factory.
   *
   * @return the geometry factory
   */
  GeoJsonGeometryFactory getGeometryFactory() {
    return geometryFactory;
  }
}

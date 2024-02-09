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

import java.io.Serial;
import java.io.Serializable;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * The type Abstract json to geometry converter.
 *
 * @author Christian Bremer
 */
abstract class AbstractJsonToGeometryConverter implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private final GeometryFactory geometryFactory;

  /**
   * Instantiates a new Abstract json to geometry converter.
   *
   * @param geometryFactory the geometry factory
   */
  AbstractJsonToGeometryConverter(GeometryFactory geometryFactory) {
    this.geometryFactory = geometryFactory;
  }

  /**
   * Gets geometry factory.
   *
   * @return the geometry factory
   */
  GeometryFactory getGeometryFactory() {
    return geometryFactory;
  }
}

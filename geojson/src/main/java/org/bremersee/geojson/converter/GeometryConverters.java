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

import java.util.List;
import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.springframework.core.convert.converter.Converter;

/**
 * The geometry converters.
 *
 * @author Christian Bremer
 */
public abstract class GeometryConverters {

  /**
   * Gets converters to register.
   *
   * @param geometryFactory the geometry factory
   * @return the converters to register
   */
  public static List<Converter<?, ?>> getConvertersToRegister(
      GeoJsonGeometryFactory geometryFactory) {

    return List.of(
        new GeometryToStringConverter(),
        new StringToPointConverter(geometryFactory),
        new StringToLineStringConverter(geometryFactory),
        new StringToPolygonConverter(geometryFactory),
        new StringToMultiPointConverter(geometryFactory),
        new StringToMultiLineStringConverter(geometryFactory),
        new StringToMultiPolygonConverter(geometryFactory),
        new StringToGeometryCollectionConverter(geometryFactory),
        new StringToGeometryConverter(geometryFactory)
    );
  }

}

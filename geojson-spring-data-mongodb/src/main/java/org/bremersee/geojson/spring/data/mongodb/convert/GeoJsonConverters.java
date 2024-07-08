/*
 * Copyright 2018-2022 the original author or authors.
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

import java.util.List;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.core.convert.converter.Converter;

/**
 * The geo json converters.
 *
 * @author Christian Bremer
 */
public abstract class GeoJsonConverters {

  private GeoJsonConverters() {
  }

  /**
   * Gets converters to register.
   *
   * @param geometryFactory the geometry factory (can be {@code null})
   * @return the converters to register
   */
  public static List<Converter<?, ?>> getConvertersToRegister(
      GeometryFactory geometryFactory) {

    return List.of(
        new DocumentToGeometryCollectionConverter(geometryFactory),
        new DocumentToGeometryConverter(geometryFactory),
        new DocumentToLineStringConverter(geometryFactory),
        new DocumentToMultiLineStringConverter(geometryFactory),
        new DocumentToMultiPointConverter(geometryFactory),
        new DocumentToMultiPolygonConverter(geometryFactory),
        new DocumentToPointConverter(geometryFactory),
        new DocumentToPolygonConverter(geometryFactory),
        new GeometryCollectionToDocumentConverter(),
        new LineStringToDocumentConverter(),
        new MultiLineStringToDocumentConverter(),
        new MultiPointToDocumentConverter(),
        new MultiPolygonToDocumentConverter(),
        new PointToDocumentConverter(),
        new PolygonToDocumentConverter()
    );
  }
}

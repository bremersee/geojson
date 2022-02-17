/*
 * Copyright 2020 the original author or authors.
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

import static org.bremersee.geojson.GeoJsonConstants.COORDINATES;
import static org.bremersee.geojson.GeoJsonConstants.MULTI_POLYGON;
import static org.bremersee.geojson.GeoJsonConstants.TYPE;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.springframework.util.Assert;

/**
 * The json to multi polygon converter.
 *
 * @author Christian Bremer
 */
class JsonToMultiPolygonConverter extends AbstractJsonToGeometryConverter {

  private static final long serialVersionUID = 1L;

  private final JsonToPolygonConverter polygonConverter;

  /**
   * Instantiates a new json to multi polygon converter.
   *
   * @param geometryFactory the geometry factory
   * @param polygonConverter the polygon converter
   */
  JsonToMultiPolygonConverter(
      GeometryFactory geometryFactory,
      JsonToPolygonConverter polygonConverter) {

    super(geometryFactory);
    Assert.notNull(polygonConverter, "LineString converter must be present.");
    this.polygonConverter = polygonConverter;
  }

  /**
   * Convert multi polygon.
   *
   * @param source the source
   * @return the multi polygon
   */
  MultiPolygon convert(Map<String, Object> source) {
    Assert.isTrue(
        source.get(TYPE).equals(MULTI_POLYGON),
        String.format("Source is not a %s: %s", MULTI_POLYGON, source));
    return convertCoordinates(source.get(COORDINATES));
  }

  /**
   * Convert coordinates multi polygon.
   *
   * @param source the source
   * @return the multi polygon
   */
  MultiPolygon convertCoordinates(Object source) {
    //noinspection unchecked
    List<Object> coordinates = isEmpty(source) ? List.of() : (List<Object>) source;
    Polygon[] polygons = coordinates.stream()
        .map(polygonConverter::convertCoordinates)
        .toArray(Polygon[]::new);
    return getGeometryFactory().createMultiPolygon(polygons);
  }

}

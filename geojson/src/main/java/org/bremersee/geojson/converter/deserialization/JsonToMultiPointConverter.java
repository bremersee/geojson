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
import static org.bremersee.geojson.GeoJsonConstants.MULTI_POINT;
import static org.bremersee.geojson.GeoJsonConstants.TYPE;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.springframework.util.Assert;

/**
 * The json to multi point converter.
 *
 * @author Christian Bremer
 */
class JsonToMultiPointConverter extends AbstractJsonToGeometryConverter {

  private static final long serialVersionUID = 1L;

  private final JsonToPointConverter pointConverter;

  /**
   * Instantiates a new json to multi point converter.
   *
   * @param geometryFactory the geometry factory
   * @param pointConverter the point converter
   */
  JsonToMultiPointConverter(
      GeometryFactory geometryFactory,
      JsonToPointConverter pointConverter) {

    super(geometryFactory);
    Assert.notNull(pointConverter, "Point converter must be present.");
    this.pointConverter = pointConverter;
  }

  /**
   * Convert multi point.
   *
   * @param source the source
   * @return the multi point
   */
  MultiPoint convert(Map<String, Object> source) {
    Assert.isTrue(
        source.get(TYPE).equals(MULTI_POINT),
        String.format("Source is not a %s: %s", MULTI_POINT, source));
    return convertCoordinates(source.get(COORDINATES));
  }

  /**
   * Convert coordinates multi point.
   *
   * @param source the source
   * @return the multi point
   */
  MultiPoint convertCoordinates(Object source) {
    //noinspection unchecked
    List<Object> coordinates = isEmpty(source) ? List.of() : (List<Object>) source;
    Point[] points = coordinates.stream()
        .map(pointConverter::convertCoordinates)
        .toArray(Point[]::new);
    return getGeometryFactory().createMultiPoint(points);
  }

}

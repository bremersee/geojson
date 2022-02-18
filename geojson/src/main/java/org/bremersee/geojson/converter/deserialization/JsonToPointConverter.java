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

import static org.bremersee.geojson.GeoJsonConstants.COORDINATES;
import static org.bremersee.geojson.GeoJsonConstants.POINT;
import static org.bremersee.geojson.GeoJsonConstants.TYPE;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.Map;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.util.Assert;

/**
 * The json to point converter.
 *
 * @author Christian Bremer
 */
class JsonToPointConverter extends AbstractJsonToGeometryConverter {

  private static final long serialVersionUID = 1L;

  private final ObjectToCoordinateConverter coordinateConverter;

  /**
   * Instantiates a new json to point converter.
   *
   * @param geometryFactory the geometry factory
   * @param coordinateConverter the coordinate converter
   */
  JsonToPointConverter(
      GeometryFactory geometryFactory,
      ObjectToCoordinateConverter coordinateConverter) {

    super(geometryFactory);
    Assert.notNull(coordinateConverter, "Coordinate converter must be present.");
    this.coordinateConverter = coordinateConverter;
  }

  /**
   * Convert point.
   *
   * @param source the source
   * @return the point
   */
  Point convert(Map<String, Object> source) {
    Assert.isTrue(
        source.get(TYPE).equals(POINT),
        String.format("Source is not a %s: %s", POINT, source));
    return convertCoordinates(source.get(COORDINATES));
  }

  /**
   * Convert coordinates point.
   *
   * @param source the source
   * @return the point
   */
  Point convertCoordinates(Object source) {
    Coordinate coordinate;
    if (isEmpty(source)) {
      coordinate = null;
    } else {
      coordinate = coordinateConverter.convert(source);
    }
    return getGeometryFactory().createPoint(coordinate);
  }

}

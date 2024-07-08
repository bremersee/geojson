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

package org.bremersee.geojson.converter.serialization;

import static java.util.Objects.isNull;
import static org.bremersee.geojson.GeoJsonConstants.POINT;

import java.io.Serial;
import org.locationtech.jts.geom.Point;

/**
 * The type Point to json converter.
 *
 * @author Christian Bremer
 */
class PointToJsonConverter extends AbstractGeometryToJsonConverter<Point> {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * The Coordinate converter.
   */
  final CoordinateToListConverter coordinateConverter;

  /**
   * Instantiates a new Point to json converter.
   *
   * @param coordinateConverter the coordinate converter
   * @param withBoundingBox the with bounding box
   */
  PointToJsonConverter(CoordinateToListConverter coordinateConverter, boolean withBoundingBox) {
    super(withBoundingBox);
    if (isNull(coordinateConverter)) {
      throw new IllegalArgumentException("Coordinate converter must be present.");
    }
    this.coordinateConverter = coordinateConverter;
  }

  @Override
  String getGeometryType() {
    return POINT;
  }

  @Override
  Object getGeometryJsonValue(Point source) {
    return coordinateConverter.convert(source.getCoordinate());
  }
}

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

import static org.bremersee.geojson.GeoJsonConstants.LINESTRING;

import org.locationtech.jts.geom.LineString;
import org.springframework.util.Assert;

/**
 * The type Line string to json converter.
 *
 * @author Christian Bremer
 */
class LineStringToJsonConverter extends AbstractGeometryToJsonConverter<LineString> {

  private static final long serialVersionUID = 1L;

  private final CoordinateSequenceToListConverter coordinateSequenceConverter;

  /**
   * Instantiates a new Line string to json converter.
   *
   * @param coordinateSequenceConverter the coordinate sequence converter
   * @param withBoundingBox the with bounding box
   */
  LineStringToJsonConverter(
      CoordinateSequenceToListConverter coordinateSequenceConverter,
      boolean withBoundingBox) {

    super(withBoundingBox);
    Assert.notNull(coordinateSequenceConverter, "Coordinate sequence converter must be present.");
    this.coordinateSequenceConverter = coordinateSequenceConverter;
  }

  @Override
  String getGeometryType() {
    return LINESTRING;
  }

  @Override
  Object getGeometryJsonValue(LineString source) {
    return coordinateSequenceConverter.convert(source.getCoordinateSequence());
  }
}

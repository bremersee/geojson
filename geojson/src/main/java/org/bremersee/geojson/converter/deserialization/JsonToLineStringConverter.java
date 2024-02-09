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

import static java.util.Objects.isNull;
import static org.bremersee.geojson.GeoJsonConstants.COORDINATES;
import static org.bremersee.geojson.GeoJsonConstants.LINESTRING;
import static org.bremersee.geojson.GeoJsonConstants.TYPE;

import java.io.Serial;
import java.util.Map;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

/**
 * The json to line string converter.
 *
 * @author Christian Bremer
 */
class JsonToLineStringConverter extends AbstractJsonToGeometryConverter {

  @Serial
  private static final long serialVersionUID = 1L;

  private final ObjectToCoordinateSequenceConverter coordinateSequenceConverter;

  /**
   * Instantiates a new json to line string converter.
   *
   * @param geometryFactory the geometry factory
   * @param coordinateSequenceConverter the coordinate sequence converter
   */
  JsonToLineStringConverter(
      GeometryFactory geometryFactory,
      ObjectToCoordinateSequenceConverter coordinateSequenceConverter) {

    super(geometryFactory);
    if (isNull(coordinateSequenceConverter)) {
      throw new IllegalArgumentException("Coordinate sequence converter must be present.");
    }
    this.coordinateSequenceConverter = coordinateSequenceConverter;
  }

  /**
   * Convert line string.
   *
   * @param source the source
   * @return the line string
   */
  LineString convert(Map<String, Object> source) {
    if (isNull(source)) {
      return null;
    }
    if (!LINESTRING.equals(source.get(TYPE))) {
      throw new IllegalArgumentException(String
          .format("Source is not a %s: %s", LINESTRING, source));
    }
    return convertCoordinates(source.get(COORDINATES));
  }

  /**
   * Convert coordinates line string.
   *
   * @param source the source
   * @return the line string
   */
  LineString convertCoordinates(Object source) {
    CoordinateSequence coordinateSequence = coordinateSequenceConverter.convert(source);
    return getGeometryFactory().createLineString(coordinateSequence);
  }

}

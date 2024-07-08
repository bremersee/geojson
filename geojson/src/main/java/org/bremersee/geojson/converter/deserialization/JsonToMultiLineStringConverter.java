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
import static org.bremersee.geojson.GeoJsonConstants.MULTI_LINESTRING;
import static org.bremersee.geojson.GeoJsonConstants.TYPE;

import java.io.Serial;
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;

/**
 * The json to multi line string converter.
 *
 * @author Christian Bremer
 */
public class JsonToMultiLineStringConverter extends AbstractJsonToGeometryConverter {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * The line string converter.
   */
  private final JsonToLineStringConverter lineStringConverter;

  /**
   * Instantiates a new json to multi line string converter.
   *
   * @param geometryFactory the geometry factory
   * @param lineStringConverter the line string converter
   */
  JsonToMultiLineStringConverter(
      GeometryFactory geometryFactory,
      JsonToLineStringConverter lineStringConverter) {

    super(geometryFactory);
    if (isNull(lineStringConverter)) {
      throw new IllegalArgumentException("LineString converter must be present.");
    }
    this.lineStringConverter = lineStringConverter;
  }

  /**
   * Convert multi line string.
   *
   * @param source the source
   * @return the multi line string
   */
  MultiLineString convert(Map<String, Object> source) {
    if (isNull(source)) {
      return null;
    }
    if (!MULTI_LINESTRING.equals(source.get(TYPE))) {
      throw new IllegalArgumentException(String
          .format("Source is not a %s: %s", MULTI_LINESTRING, source));
    }
    return convertCoordinates(source.get(COORDINATES));
  }

  /**
   * Convert coordinates multi line string.
   *
   * @param source the source
   * @return the multi line string
   */
  MultiLineString convertCoordinates(Object source) {
    //noinspection unchecked
    List<Object> coordinates = isNull(source) ? List.of() : (List<Object>) source;
    LineString[] lineStrings = coordinates.stream()
        .map(lineStringConverter::convertCoordinates)
        .toArray(LineString[]::new);
    return getGeometryFactory().createMultiLineString(lineStrings);
  }

}

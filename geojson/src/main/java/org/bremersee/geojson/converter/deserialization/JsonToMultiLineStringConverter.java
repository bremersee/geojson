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

import static org.bremersee.geojson.GeoJsonConstants.JSON_COORDINATES_ATTRIBUTE_NAME;
import static org.bremersee.geojson.GeoJsonConstants.JSON_TYPE_ATTRIBUTE;
import static org.bremersee.geojson.GeoJsonConstants.JSON_TYPE_MULTI_LINESTRING;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.springframework.util.Assert;

/**
 * The json to multi line string converter.
 *
 * @author Christian Bremer
 */
public class JsonToMultiLineStringConverter extends AbstractJsonToGeometryConverter {

  private static final long serialVersionUID = 1L;

  private final JsonToLineStringConverter lineStringConverter;

  /**
   * Instantiates a new json to multi line string converter.
   */
  public JsonToMultiLineStringConverter() {
    this.lineStringConverter = new JsonToLineStringConverter();
  }

  /**
   * Instantiates a new json to multi line string converter.
   *
   * @param geometryFactory the geometry factory
   * @param lineStringConverter the line string converter
   */
  public JsonToMultiLineStringConverter(
      GeometryFactory geometryFactory,
      JsonToLineStringConverter lineStringConverter) {

    super(geometryFactory);
    Assert.notNull(lineStringConverter, "LineString converter must be present.");
    this.lineStringConverter = lineStringConverter;
  }

  /**
   * Convert multi line string.
   *
   * @param source the source
   * @return the multi line string
   */
  public MultiLineString convert(Map<String, Object> source) {
    if (isEmpty(source)) {
      return null;
    }
    Assert.isTrue(
        source.get(JSON_TYPE_ATTRIBUTE).equals(JSON_TYPE_MULTI_LINESTRING),
        String.format("Source is not a %s: %s", JSON_TYPE_MULTI_LINESTRING, source));
    return convertCoordinates(source.get(JSON_COORDINATES_ATTRIBUTE_NAME));
  }

  /**
   * Convert coordinates multi line string.
   *
   * @param source the source
   * @return the multi line string
   */
  MultiLineString convertCoordinates(Object source) {
    //noinspection unchecked
    List<Object> coordinates = isEmpty(source) ? List.of() : (List<Object>) source;
    LineString[] lineStrings = coordinates.stream()
        .map(lineStringConverter::convertCoordinates)
        .toArray(LineString[]::new);
    return getGeometryFactory().createMultiLineString(lineStrings);
  }

}

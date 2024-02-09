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

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.isNull;
import static org.bremersee.geojson.GeoJsonConstants.MULTI_LINESTRING;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;

/**
 * The type Multi line string to json converter.
 *
 * @author Christian Bremer
 */
class MultiLineStringToJsonConverter extends AbstractGeometryToJsonConverter<MultiLineString> {

  @Serial
  private static final long serialVersionUID = 1L;

  private final LineStringToJsonConverter lineStringConverter;

  /**
   * Instantiates a new Multi line string to json converter.
   *
   * @param lineStringConverter the line string converter
   * @param withBoundingBox the with bounding box
   */
  MultiLineStringToJsonConverter(
      LineStringToJsonConverter lineStringConverter,
      boolean withBoundingBox) {

    super(withBoundingBox);
    if (isNull(lineStringConverter)) {
      throw new IllegalArgumentException("Line string converter must be present.");
    }
    this.lineStringConverter = lineStringConverter;
  }

  @Override
  String getGeometryType() {
    return MULTI_LINESTRING;
  }

  @Override
  Object getGeometryJsonValue(MultiLineString source) {
    List<Object> list = new ArrayList<>(source.getNumGeometries());
    for (int i = 0; i < source.getNumGeometries(); i++) {
      LineString lineString = (LineString) source.getGeometryN(i);
      list.add(lineStringConverter.getGeometryJsonValue(lineString));
    }
    return unmodifiableList(list);
  }
}

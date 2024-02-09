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
import static org.bremersee.geojson.GeoJsonConstants.POLYGON;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Polygon;

/**
 * The type Polygon to json converter.
 *
 * @author Christian Bremer
 */
class PolygonToJsonConverter extends AbstractGeometryToJsonConverter<Polygon> {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * The Coordinate sequence converter.
   */
  final CoordinateSequenceToListConverter coordinateSequenceConverter;

  /**
   * Instantiates a new Polygon to json converter.
   *
   * @param coordinateSequenceConverter the coordinate sequence converter
   * @param withBoundingBox the with bounding box
   */
  PolygonToJsonConverter(
      CoordinateSequenceToListConverter coordinateSequenceConverter,
      boolean withBoundingBox) {

    super(withBoundingBox);
    if (isNull(coordinateSequenceConverter)) {
      throw new IllegalArgumentException("Coordinate sequence converter must be present.");
    }
    this.coordinateSequenceConverter = coordinateSequenceConverter;
  }

  @Override
  String getGeometryType() {
    return POLYGON;
  }

  @Override
  Object getGeometryJsonValue(Polygon source) {
    List<List<List<Number>>> list = new ArrayList<>();
    list.add(coordinateSequenceConverter
        .convert(source.getExteriorRing().getCoordinateSequence()));
    for (int i = 0; i < source.getNumInteriorRing(); i++) {
      list.add(coordinateSequenceConverter
          .convert(source.getInteriorRingN(i).getCoordinateSequence()));
    }
    return unmodifiableList(list);
  }
}

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

package org.bremersee.geojson.converter.serialization;

import static java.util.Collections.unmodifiableList;
import static org.bremersee.geojson.GeoJsonConstants.POLYGON;

import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Polygon;
import org.springframework.util.Assert;

/**
 * @author Christian Bremer
 */
class PolygonToJsonConverter extends AbstractGeometryToJsonConverter<Polygon> {

  private static final long serialVersionUID = 1L;

  final CoordinateSequenceToListConverter coordinateSequenceConverter;

  PolygonToJsonConverter(CoordinateSequenceToListConverter coordinateSequenceConverter) {
    Assert.notNull(coordinateSequenceConverter, "Coordinate sequence converter must be present.");
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

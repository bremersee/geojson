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
import static org.bremersee.geojson.GeoJsonConstants.POLYGON;
import static org.bremersee.geojson.GeoJsonConstants.TYPE;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

/**
 * The json to polygon converter.
 *
 * @author Christian Bremer
 */
class JsonToPolygonConverter extends AbstractJsonToGeometryConverter {

  @Serial
  private static final long serialVersionUID = 1L;

  private final ObjectToCoordinateSequenceConverter coordinateSequenceConverter;

  /**
   * Instantiates a new json to polygon converter.
   *
   * @param geometryFactory the geometry factory
   * @param coordinateSequenceConverter the coordinate sequence converter
   */
  JsonToPolygonConverter(
      GeometryFactory geometryFactory,
      ObjectToCoordinateSequenceConverter coordinateSequenceConverter) {

    super(geometryFactory);
    if (isNull(coordinateSequenceConverter)) {
      throw new IllegalArgumentException("Coordinate sequence converter must be present.");
    }
    this.coordinateSequenceConverter = coordinateSequenceConverter;
  }

  /**
   * Convert polygon.
   *
   * @param source the source
   * @return the polygon
   */
  Polygon convert(Map<String, Object> source) {
    if (isNull(source)) {
      return null;
    }
    if (!POLYGON.equals(source.get(TYPE))) {
      throw new IllegalArgumentException(String
          .format("Source is not a %s: %s", POLYGON, source));
    }
    return convertCoordinates(source.get(COORDINATES));
  }

  /**
   * Convert coordinates polygon.
   *
   * @param source the source
   * @return the polygon
   */
  Polygon convertCoordinates(Object source) {
    Polygon polygon;
    if (isNull(source)) {
      polygon = getGeometryFactory().createPolygon();
    } else {
      List<CoordinateSequence> list = new ArrayList<>();
      //noinspection unchecked
      for (Object coordinates : (List<Object>) source) {
        list.add(coordinateSequenceConverter.convert(coordinates));
      }
      if (list.isEmpty()) {
        polygon = getGeometryFactory().createPolygon();
      } else if (list.size() == 1) {
        polygon = getGeometryFactory().createPolygon(list.get(0));
      } else {
        GeometryFactory gf = getGeometryFactory();
        LinearRing[] holes = new LinearRing[list.size() - 1];
        for (int i = 1; i < list.size(); i++) {
          holes[i - 1] = gf.createLinearRing(list.get(i));
        }
        polygon = gf.createPolygon(gf.createLinearRing(list.get(0)), holes);
      }
    }
    return polygon;
  }

}

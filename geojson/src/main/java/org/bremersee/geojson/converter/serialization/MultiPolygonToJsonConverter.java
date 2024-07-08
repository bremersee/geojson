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
import static org.bremersee.geojson.GeoJsonConstants.MULTI_POLYGON;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

/**
 * The type Multi polygon to json converter.
 *
 * @author Christian Bremer
 */
class MultiPolygonToJsonConverter extends AbstractGeometryToJsonConverter<MultiPolygon> {

  @Serial
  private static final long serialVersionUID = 1L;

  private final PolygonToJsonConverter polygonConverter;

  /**
   * Instantiates a new Multi polygon to json converter.
   *
   * @param polygonConverter the polygon converter
   * @param withBoundingBox the with bounding box
   */
  MultiPolygonToJsonConverter(PolygonToJsonConverter polygonConverter, boolean withBoundingBox) {
    super(withBoundingBox);
    if (isNull(polygonConverter)) {
      throw new IllegalArgumentException("Polygon converter must be present.");
    }
    this.polygonConverter = polygonConverter;
  }

  @Override
  String getGeometryType() {
    return MULTI_POLYGON;
  }

  @Override
  Object getGeometryJsonValue(MultiPolygon source) {
    List<Object> list = new ArrayList<>(source.getNumGeometries());
    for (int i = 0; i < source.getNumGeometries(); i++) {
      Polygon polygon = (Polygon) source.getGeometryN(i);
      list.add(polygonConverter.getGeometryJsonValue(polygon));
    }
    return unmodifiableList(list);
  }
}

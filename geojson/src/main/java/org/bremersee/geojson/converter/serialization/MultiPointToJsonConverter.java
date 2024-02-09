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
import static org.bremersee.geojson.GeoJsonConstants.MULTI_POINT;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;

/**
 * The type Multi point to json converter.
 *
 * @author Christian Bremer
 */
class MultiPointToJsonConverter extends AbstractGeometryToJsonConverter<MultiPoint> {

  @Serial
  private static final long serialVersionUID = 1L;

  private final PointToJsonConverter pointConverter;

  /**
   * Instantiates a new Multi point to json converter.
   *
   * @param pointConverter the point converter
   * @param withBoundingBox the with bounding box
   */
  MultiPointToJsonConverter(PointToJsonConverter pointConverter, boolean withBoundingBox) {
    super(withBoundingBox);
    if (isNull(pointConverter)) {
      throw new IllegalArgumentException("Point converter must be present.");
    }
    this.pointConverter = pointConverter;
  }

  @Override
  String getGeometryType() {
    return MULTI_POINT;
  }

  @Override
  Object getGeometryJsonValue(MultiPoint source) {
    List<Object> list = new ArrayList<>(source.getNumGeometries());
    for (int i = 0; i < source.getNumGeometries(); i++) {
      Point point = (Point) source.getGeometryN(i);
      list.add(pointConverter.getGeometryJsonValue(point));
    }
    return unmodifiableList(list);
  }
}

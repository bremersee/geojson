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

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static org.bremersee.geojson.GeoJsonConstants.BBOX;
import static org.bremersee.geojson.GeoJsonConstants.COORDINATES;
import static org.bremersee.geojson.GeoJsonConstants.TYPE;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.locationtech.jts.geom.Geometry;

/**
 * The type Abstract geometry to json converter.
 *
 * @param <S> the type parameter
 * @author Christian Bremer
 */
abstract class AbstractGeometryToJsonConverter<S extends Geometry> implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private final boolean withBoundingBox;

  /**
   * Instantiates a new Abstract geometry to json converter.
   *
   * @param withBoundingBox the with bounding box
   */
  AbstractGeometryToJsonConverter(boolean withBoundingBox) {
    this.withBoundingBox = withBoundingBox;
  }

  /**
   * Convert map.
   *
   * @param source the source
   * @return the map
   */
  Map<String, Object> convert(S source) {
    if (isNull(source)) {
      return null;
    }
    Map<String, Object> map = new LinkedHashMap<>();
    map.put(TYPE, requireNonNull(getGeometryType()));
    if (withBoundingBox) {
      Optional.ofNullable(GeoJsonGeometryFactory.getBoundingBox(source))
          .map(bbox -> Arrays.stream(bbox).boxed().collect(Collectors.toList()))
          .ifPresent(bbox -> map.put(BBOX, bbox));
    }
    map.put(COORDINATES, getGeometryJsonValue(source));
    return unmodifiableMap(map);
  }

  /**
   * Gets geometry type.
   *
   * @return the geometry type
   */
  abstract String getGeometryType();

  /**
   * Gets geometry json value.
   *
   * @param source the source
   * @return the geometry json value
   */
  abstract Object getGeometryJsonValue(S source);
}

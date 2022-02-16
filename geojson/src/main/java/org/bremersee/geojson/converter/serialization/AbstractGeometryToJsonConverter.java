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

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static org.bremersee.geojson.GeoJsonConstants.JSON_COORDINATES_ATTRIBUTE_NAME;
import static org.bremersee.geojson.GeoJsonConstants.JSON_TYPE_ATTRIBUTE;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;

/**
 * @author Christian Bremer
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
abstract class AbstractGeometryToJsonConverter<S extends Geometry> implements Serializable {

  private static final long serialVersionUID = 1L;

  Map<String, Object> convert(S source) {
    if (isNull(source)) {
      return null;
    }
    Map<String, Object> map = new LinkedHashMap<>();
    map.put(JSON_TYPE_ATTRIBUTE, requireNonNull(getTypeAttributeValue()));
    map.put(JSON_COORDINATES_ATTRIBUTE_NAME, getGeometryJsonValue(source));
    return unmodifiableMap(map);
  }

  abstract String getTypeAttributeValue();

  abstract Object getGeometryJsonValue(S source);
}

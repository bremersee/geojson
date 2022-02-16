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

package org.bremersee.geojson;

import java.util.Map;
import java.util.function.Function;
import lombok.Getter;
import org.bremersee.geojson.converter.serialization.GeometryToJsonConverter;
import org.locationtech.jts.geom.Geometry;

/**
 * @author Christian Bremer
 */
public enum GeoJson {

  POINT;

  private Class<? extends Geometry> geometryClass;

  private String type;

  private ValueAttribute valueAttribute;

  <T> T fromJson(Map map) {
    return null;
  }

  public static Map<String, Object> toJson(Geometry geometry, boolean useBigDecimal) {
    return new GeometryToJsonConverter(useBigDecimal).convert(geometry);
  }

  public enum ValueAttribute {

    COORDINATES("coordinates"),

    GEOMETRIES("geometries");

    @Getter
    private final String key;

    ValueAttribute(String key) {
      this.key = key;
    }
  }

}

/*
 * Copyright 2015-2022 the original author or authors.
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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.io.Serial;
import java.util.Map;
import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * A Jackson deserializer for a {@link Geometry}.
 *
 * @author Christian Bremer
 */
public class JacksonGeometryDeserializer extends StdDeserializer<Geometry> {

  @Serial
  private static final long serialVersionUID = 3L;

  /**
   * The json to geometry converter.
   */
  private final JsonToGeometryConverter geometryConverter;

  /**
   * Default constructor.
   */
  public JacksonGeometryDeserializer() {
    this(new GeometryFactory());
  }

  /**
   * Constructs a deserializer that uses the specified geometry factory.
   *
   * @param geometryFactory the geometry factory
   */
  public JacksonGeometryDeserializer(GeometryFactory geometryFactory) {
    super(Geometry.class);
    GeometryFactory gf = isNull(geometryFactory) ? new GeoJsonGeometryFactory() : geometryFactory;
    this.geometryConverter = new JsonToGeometryConverter(gf);
  }

  @Override
  public Geometry deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    Map<String, Object> map = jp.readValueAs(new MapTypeReference());
    if (isNull(map) || map.isEmpty()) {
      return null;
    }
    return geometryConverter.convert(map);
  }

  private static class MapTypeReference extends TypeReference<Map<String, Object>> {

    private MapTypeReference() {
    }
  }
}

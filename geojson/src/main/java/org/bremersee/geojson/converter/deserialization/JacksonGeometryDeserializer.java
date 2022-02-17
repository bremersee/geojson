/*
 * Copyright 2015-2020 the original author or authors.
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
import static org.bremersee.geojson.GeoJsonConstants.GEOMETRIES;
import static org.bremersee.geojson.GeoJsonConstants.TYPE;
import static org.bremersee.geojson.GeoJsonConstants.GEOMETRY_COLLECTION;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

  private static final long serialVersionUID = 3L;

  private final GeometryFactory geometryFactory;

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
    this.geometryFactory = isNull(geometryFactory) ? new GeoJsonGeometryFactory() : geometryFactory;
    this.geometryConverter = new JsonToGeometryConverter(this.geometryFactory);
  }

  @Override
  public Geometry deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException {

    String type = null;
    List<Object> coordinates = new ArrayList<>();
    List<Geometry> geometries = new ArrayList<>();

    JsonToken currentToken;
    while ((currentToken = jp.nextValue()) != null) {
      if (JsonToken.VALUE_STRING.equals(currentToken)) {
        if (TYPE.equals(jp.getCurrentName())) {
          type = jp.getText();
        }
      } else if (JsonToken.START_ARRAY.equals(currentToken)) {
        if (COORDINATES.equals(jp.getCurrentName())) {
          parseCoordinates(0, coordinates, jp, ctxt);
        } else if (GEOMETRIES.equals(jp.getCurrentName())) {
          geometries.addAll(parseGeometries(jp, ctxt));
        }
      } else if (JsonToken.END_OBJECT.equals(currentToken)) {
        break;
      }
    }

    if (GEOMETRY_COLLECTION.equals(type)) {
      return geometryFactory.createGeometryCollection(geometries.toArray(new Geometry[0]));
    } else {
      return createGeometry(type, coordinates);
    }
  }

  private void parseCoordinates(int depth, List<Object> coordinates,
      JsonParser jp,
      @SuppressWarnings("unused") DeserializationContext ctxt)
      throws IOException {

    JsonToken currentToken;
    while ((currentToken = jp.nextValue()) != null) {
      if (JsonToken.END_ARRAY.equals(currentToken)) {
        break;
      } else if (JsonToken.START_ARRAY.equals(currentToken)) {
        List<Object> list = new ArrayList<>();
        parseCoordinates(depth + 1, list, jp, ctxt);
        coordinates.add(list);
      } else if (JsonToken.VALUE_NUMBER_FLOAT.equals(currentToken)) {
        coordinates.add(jp.getDoubleValue());
      } else if (JsonToken.VALUE_NUMBER_INT.equals(currentToken)) {
        long v = jp.getLongValue();
        double d = (double) v;
        coordinates.add(d);
      }
    }
  }

  private List<Geometry> parseGeometries(JsonParser jsonParser,
      DeserializationContext deserializationContext)
      throws IOException {

    String type = null;
    List<Object> coordinates = new ArrayList<>();
    List<Geometry> geometries = new ArrayList<>();

    JsonToken currentToken;
    while ((currentToken = jsonParser.nextValue()) != null) {
      if (JsonToken.END_OBJECT.equals(currentToken)
          && type != null
          && !coordinates.isEmpty()) {

        geometries.add(createGeometry(type, coordinates));
        type = null;
        coordinates = new ArrayList<>();

      } else if (JsonToken.VALUE_STRING.equals(currentToken)
          && TYPE.equals(jsonParser.getCurrentName())) {

        type = jsonParser.getText();

      } else if (JsonToken.START_ARRAY.equals(currentToken)
          && COORDINATES.equals(jsonParser.getCurrentName())) {

        parseCoordinates(0, coordinates, jsonParser, deserializationContext);

      } else if (JsonToken.END_ARRAY.equals(currentToken)) {

        break;
      }
    }
    return geometries;
  }

  /**
   * Creates a geometry by type and coordinates.
   *
   * @param type the geometry type
   * @param coordinates the coordinates of the geometry
   * @return the created geometry
   */
  private Geometry createGeometry(String type, final List<Object> coordinates) {
    Map<String, Object> jsonMap = new LinkedHashMap<>();
    jsonMap.put(TYPE, type);
    jsonMap.put(COORDINATES, coordinates);
    return geometryConverter.convert(jsonMap);
  }

}

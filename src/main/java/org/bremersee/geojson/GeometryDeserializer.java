/*
 * Copyright 2015-2018 the original author or authors.
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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bremersee.geojson.utils.ConvertHelper;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * A Jackson deserializer for a {@link Geometry}.
 *
 * @author Christian Bremer
 */
@SuppressWarnings("WeakerAccess")
public class GeometryDeserializer extends StdDeserializer<Geometry> {

  private static final long serialVersionUID = 2L;

  private final ConvertHelper convertHelper;

  /**
   * Default constructor.
   */
  public GeometryDeserializer() {
    this(null);
  }

  /**
   * Constructs a deserializer that uses the specified geometry factory.
   *
   * @param geometryFactory the geometry factory
   */
  public GeometryDeserializer(final GeometryFactory geometryFactory) {
    super(Geometry.class);
    this.convertHelper = new ConvertHelper(geometryFactory);
  }

  @Override
  public Geometry deserialize(final JsonParser jp, final DeserializationContext ctxt) //NOSONAR
      throws IOException {

    String type = null;
    List<Object> coordinates = new ArrayList<>();
    List<Geometry> geometries = new ArrayList<>();

    JsonToken currentToken;
    while ((currentToken = jp.nextValue()) != null) {
      if (JsonToken.VALUE_STRING.equals(currentToken)) {
        if ("type".equals(jp.getCurrentName())) {
          type = jp.getText();
        }
      } else if (JsonToken.START_ARRAY.equals(currentToken)) {
        if ("coordinates".equals(jp.getCurrentName())) {
          parseCoordinates(0, coordinates, jp, ctxt);
        } else if ("geometries".equals(jp.getCurrentName())) {
          geometries.addAll(parseGeometries(jp, ctxt));
        }
      } else if (JsonToken.END_OBJECT.equals(currentToken)) {
        break;
      }
    }

    if ("GeometryCollection".equals(type)) {
      return convertHelper.createGeometryCollection(geometries);
    } else {
      return createGeometry(type, coordinates);
    }
  }

  private void parseCoordinates(final int depth, final List<Object> coordinates,
      final JsonParser jp,
      @SuppressWarnings("unused") final DeserializationContext ctxt)
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
        final long v = jp.getLongValue();
        final double d = (double) v;
        coordinates.add(d);
      }
    }
  }

  private List<Geometry> parseGeometries(final JsonParser jsonParser,
      final DeserializationContext deserializationContext)
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
          && "type".equals(jsonParser.getCurrentName())) {

        type = jsonParser.getText();

      } else if (JsonToken.START_ARRAY.equals(currentToken)
          && "coordinates".equals(jsonParser.getCurrentName())) {

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
  private Geometry createGeometry(final String type, final List<Object> coordinates) { // NOSONAR
    if ("Point".equals(type)) {
      return convertHelper.createPoint(coordinates);
    }
    if ("LineString".equals(type)) {
      return convertHelper.createLineString(coordinates);
    }
    if ("Polygon".equals(type)) {
      return convertHelper.createPolygon(coordinates);
    }
    if ("MultiPoint".equals(type)) {
      return convertHelper.createMultiPoint(coordinates);
    }
    if ("MultiLineString".equals(type)) {
      return convertHelper.createMultiLineString(coordinates);
    }
    if ("MultiPolygon".equals(type)) {
      return convertHelper.createMultiPolygon(coordinates);
    }
    throw new IllegalArgumentException("Geometry type [" + type + "] is unsupported.");
  }

}

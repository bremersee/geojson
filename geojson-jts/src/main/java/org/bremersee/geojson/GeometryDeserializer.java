/*
 * Copyright 2015 the original author or authors.
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
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A Jackson deserializer for a {@link Geometry}.
 *
 * @author Christian Bremer
 */
@SuppressWarnings("WeakerAccess")
public class GeometryDeserializer extends StdDeserializer<Geometry> {

  private static final long serialVersionUID = 1L;

  private GeometryFactory geometryFactory = new GeometryFactory();

  /**
   * Default constructor.
   */
  public GeometryDeserializer() {
    super(Geometry.class);
  }

  /**
   * Constructs a deserializer that uses the specified geometry factory.
   *
   * @param geometryFactory the geometry factory
   */
  public GeometryDeserializer(final GeometryFactory geometryFactory) {
    this();
    setGeometryFactory(geometryFactory);
  }

  /**
   * Gets the geometry factory.
   *
   * @return the geometry factory
   */
  protected GeometryFactory getGeometryFactory() {
    return geometryFactory;
  }

  /**
   * Returns the geometry factory.
   *
   * @param geometryFactory the geometry factory
   */
  public void setGeometryFactory(final GeometryFactory geometryFactory) {
    if (geometryFactory != null) {
      this.geometryFactory = geometryFactory;
    }
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
      return createGeometryCollection(geometries);
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

  private Coordinate createCoordinate(final List<Object> coordinates) {
    Coordinate coordinate = new Coordinate();
    coordinate.x = (!coordinates.isEmpty()) ? (double) coordinates.get(0) : Double.NaN;
    coordinate.y = (coordinates.size() > 1) ? (double) coordinates.get(1) : Double.NaN;
    coordinate.z = (coordinates.size() > 2) ? (double) coordinates.get(2) : Double.NaN;
    return coordinate;
  }

  private Coordinate[] createCoordinates(final List<Object> coordinates) {
    Coordinate[] coords = new Coordinate[coordinates.size()];
    int i = 0;
    for (Object obj : coordinates) {
      @SuppressWarnings("unchecked")
      List<Object> list = (List<Object>) obj;
      coords[i] = createCoordinate(list);
      i++;
    }
    return coords;
  }

  private Point createPoint(final List<Object> coordinates) {
    return getGeometryFactory().createPoint(createCoordinate(coordinates));
  }

  private LineString createLineString(final List<Object> coordinates) { // List<List<Double>>
    return getGeometryFactory().createLineString(createCoordinates(coordinates));
  }

  private Polygon createPolygon(final List<Object> coordinates) { // List<List<List<Double>>>
    List<Coordinate[]> list = new ArrayList<>();
    for (Object obj : coordinates) {
      @SuppressWarnings("unchecked")
      List<Object> coords = (List<Object>) obj;
      list.add(createCoordinates(coords));
    }
    if (list.size() == 1) {
      return getGeometryFactory().createPolygon(list.get(0));
    } else {
      GeometryFactory gf = getGeometryFactory();
      LinearRing[] holes = new LinearRing[list.size() - 1];
      for (int i = 1; i < list.size(); i++) {
        holes[i - 1] = gf.createLinearRing(list.get(i));
      }
      return gf.createPolygon(gf.createLinearRing(list.get(0)), holes);
    }
  }

  private MultiPoint createMultiPoint(final List<Object> coordinates) {
    Point[] points = new Point[coordinates.size()];
    int i = 0;
    for (Object obj : coordinates) {
      @SuppressWarnings("unchecked")
      List<Object> list = (List<Object>) obj;
      points[i] = createPoint(list);
      i++;
    }
    return getGeometryFactory().createMultiPoint(points);
  }

  private MultiLineString createMultiLineString(final List<Object> coordinates) {
    LineString[] lineStrings = new LineString[coordinates.size()];
    int i = 0;
    for (Object obj : coordinates) {
      @SuppressWarnings("unchecked")
      List<Object> list = (List<Object>) obj;
      lineStrings[i] = createLineString(list);
      i++;
    }
    return getGeometryFactory().createMultiLineString(lineStrings);
  }

  private MultiPolygon createMultiPolygon(final List<Object> coordinates) {
    Polygon[] polygons = new Polygon[coordinates.size()];
    int i = 0;
    for (Object obj : coordinates) {
      @SuppressWarnings("unchecked")
      List<Object> list = (List<Object>) obj;
      polygons[i] = createPolygon(list);
      i++;
    }
    return getGeometryFactory().createMultiPolygon(polygons);
  }

  private GeometryCollection createGeometryCollection(final List<Geometry> geometries) {
    final Geometry[] geoms;
    if (geometries == null) {
      geoms = new Geometry[0];
    } else {
      geoms = geometries.toArray(new Geometry[0]);
    }
    GeometryFactory gf = getGeometryFactory();
    return gf.createGeometryCollection(geoms);
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
   * @param type        the geometry type
   * @param coordinates the coordinates of the geometry
   * @return the created geometry
   */
  private Geometry createGeometry(final String type, final List<Object> coordinates) { // NOSONAR
    if ("Point".equals(type)) {
      return createPoint(coordinates);
    }
    if ("LineString".equals(type)) {
      return createLineString(coordinates);
    }
    if ("Polygon".equals(type)) {
      return createPolygon(coordinates);
    }
    if ("MultiPoint".equals(type)) {
      return createMultiPoint(coordinates);
    }
    if ("MultiLineString".equals(type)) {
      return createMultiLineString(coordinates);
    }
    if ("MultiPolygon".equals(type)) {
      return createMultiPolygon(coordinates);
    }
    throw new IllegalArgumentException("Geometry type [" + type + "] is unsupported.");
  }

}

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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A Jackson serializer for a {@link Geometry}.
 *
 * @author Christian Bremer
 */
@SuppressWarnings("WeakerAccess")
public class GeometrySerializer extends StdSerializer<Geometry> implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final String TYPE_ATTRIBUTE_NAME = "type";

  private static final String COORDINATES_ATTRIBUTE_NAME = "coordinates";

  private static final String GEOMETRIES_ATTRIBUTE_NAME = "geometries";

  private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);

  static {
    NUMBER_FORMAT.setMaximumFractionDigits(9);
    NUMBER_FORMAT.setMaximumIntegerDigits(17);
    NUMBER_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
    NUMBER_FORMAT.setGroupingUsed(false);
  }


  /**
   * Default constructor.
   */
  public GeometrySerializer() {
    super(Geometry.class, false);
  }

  @Override
  public void serialize(
      final Geometry value,
      final JsonGenerator jgen,
      final SerializerProvider provider)
      throws IOException {

    if (value == null) {
      jgen.writeNull();
    } else {
      jgen.writeObject(create(value));
    }
  }

  private Map<String, Object> create(final Geometry geometry) {
    if (geometry instanceof Point) {
      return createPoint((Point) geometry);
    }

    if (geometry instanceof LineString) {
      return createLine((LineString) geometry);
    }

    if (geometry instanceof Polygon) {
      return createPolygon((Polygon) geometry);
    }

    if (geometry instanceof MultiPoint) {
      return createMultiPoint((MultiPoint) geometry);
    }

    if (geometry instanceof MultiLineString) {
      return createMultiLine((MultiLineString) geometry);
    }

    if (geometry instanceof MultiPolygon) {
      return createMultiPolygon((MultiPolygon) geometry);
    }

    if (geometry instanceof GeometryCollection) {
      return createGeometryCollection((GeometryCollection) geometry);
    }

    throw new IllegalArgumentException("Geometry [" + geometry + "] is unsupported. It must be "
        + "an instance of Point, LineString, Polygon, MultiPoint, MultiLineString, MultiPolygon "
        + "or GeometryCollection.");
  }

  private Map<String, Object> createPoint(final Point point) {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    map.put(TYPE_ATTRIBUTE_NAME, "Point");
    map.put(COORDINATES_ATTRIBUTE_NAME, createCoordinates(point.getCoordinate()));
    return map;
  }

  private Map<String, Object> createLine(final LineString line) {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    map.put(TYPE_ATTRIBUTE_NAME, "LineString");
    map.put(COORDINATES_ATTRIBUTE_NAME, createCoordinates(line.getCoordinateSequence()));
    return map;
  }

  private Map<String, Object> createPolygon(final Polygon polygon) {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    map.put(TYPE_ATTRIBUTE_NAME, "Polygon");
    map.put(COORDINATES_ATTRIBUTE_NAME, createCoordinates(polygon));
    return map;
  }

  private Map<String, Object> createMultiPoint(final MultiPoint multiPoint) {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    map.put(TYPE_ATTRIBUTE_NAME, "MultiPoint");
    map.put(COORDINATES_ATTRIBUTE_NAME, createCoordinates(multiPoint));
    return map;
  }

  private Map<String, Object> createMultiLine(final MultiLineString multiLine) {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    map.put(TYPE_ATTRIBUTE_NAME, "MultiLineString");
    map.put(COORDINATES_ATTRIBUTE_NAME, createCoordinates(multiLine));
    return map;
  }

  private Map<String, Object> createMultiPolygon(final MultiPolygon multiPolygon) {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    map.put(TYPE_ATTRIBUTE_NAME, "MultiPolygon");
    map.put(COORDINATES_ATTRIBUTE_NAME, createCoordinates(multiPolygon));
    return map;
  }

  private Map<String, Object> createGeometryCollection(
      final GeometryCollection geometryColllection) {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    List<Map<String, Object>> geometries = new ArrayList<>(geometryColllection.getNumGeometries());
    for (int i = 0; i < geometryColllection.getNumGeometries(); i++) {
      geometries.add(create(geometryColllection.getGeometryN(i)));
    }

    map.put(TYPE_ATTRIBUTE_NAME, "GeometryCollection");
    map.put(GEOMETRIES_ATTRIBUTE_NAME, geometries);
    return map;
  }

  private double round(final double value) {
    if (Double.isNaN(value)) {
      return value;
    }
    final String strValue = NUMBER_FORMAT.format(value);
    return new BigDecimal(strValue).doubleValue();
  }

  private List<Object> createCoordinates(final Coordinate coordinate) {
    if (coordinate == null) {
      return Collections.emptyList();
    }
    final List<Object> list = new ArrayList<>(3);

    final double dx = coordinate.x;
    list.add(round(dx));

    final double dy = coordinate.y;
    list.add(round(dy));

    if (!Double.isNaN(coordinate.z)) {
      final double dz = coordinate.z;
      list.add(round(dz));
    }
    return list;
  }

  private List<List<Object>> createCoordinates(final CoordinateSequence coordinateSequence) {
    if (coordinateSequence == null || coordinateSequence.size() == 0) {
      return Collections.emptyList();
    }
    List<List<Object>> list = new ArrayList<>(coordinateSequence.size());
    for (int n = 0; n < coordinateSequence.size(); n++) {
      list.add(createCoordinates(coordinateSequence.getCoordinate(n)));
    }
    return list;
  }

  private List<List<List<Object>>> createCoordinates(final Polygon polygon) {
    List<List<List<Object>>> list = new ArrayList<>();
    list.add(createCoordinates(polygon.getExteriorRing().getCoordinateSequence()));
    for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
      list.add(createCoordinates(polygon.getInteriorRingN(i).getCoordinateSequence()));
    }
    return list;
  }

  private List<Object> createCoordinates(final GeometryCollection geometryCollection) {
    List<Object> list = new ArrayList<>(geometryCollection.getNumGeometries());
    for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
      Geometry g = geometryCollection.getGeometryN(i);
      if (g instanceof Polygon) {
        list.add(createCoordinates((Polygon) g));
      } else if (g instanceof LineString) {
        list.add(createCoordinates(((LineString) g).getCoordinateSequence()));
      } else if (g instanceof Point) {
        list.add(createCoordinates((g).getCoordinate()));
      }
    }
    return list;
  }

}

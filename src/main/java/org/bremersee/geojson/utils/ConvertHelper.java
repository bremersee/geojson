/*
 * Copyright 2018 the original author or authors.
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

package org.bremersee.geojson.utils;

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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * The convert helper.
 *
 * @author Christian Bremer
 */
public class ConvertHelper implements Serializable {

  private static final long serialVersionUID = 2L;

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

  private final GeometryFactory geometryFactory;

  /**
   * Instantiates a new convert helper.
   */
  public ConvertHelper() {
    this(null);
  }

  /**
   * Instantiates a new convert helper.
   *
   * @param geometryFactory the geometry factory
   */
  public ConvertHelper(final GeometryFactory geometryFactory) {
    this.geometryFactory = geometryFactory != null ? geometryFactory : new GeometryFactory();
  }

  /**
   * Gets geometry factory.
   *
   * @return the geometry factory
   */
  @SuppressWarnings("WeakerAccess")
  public GeometryFactory getGeometryFactory() {
    return geometryFactory;
  }

  private double toPrimitiveDoubleValue(Object value) {
    if (value == null) {
      throw new IllegalArgumentException("Argument must be a Number.");
    }
    if (value instanceof Number) {
      return ((Number) value).doubleValue();
    }
    return new BigDecimal(String.valueOf(value)).doubleValue();
  }

  private Coordinate createCoordinate(final List<Object> coordinates) {
    Coordinate coordinate = new Coordinate();
    coordinate.x =
        (!coordinates.isEmpty()) ? toPrimitiveDoubleValue(coordinates.get(0)) : Double.NaN;
    coordinate.y =
        (coordinates.size() > 1) ? toPrimitiveDoubleValue(coordinates.get(1)) : Double.NaN;
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

  /**
   * Create point.
   *
   * @param coordinates the coordinates
   * @return the point
   */
  public Point createPoint(final List<Object> coordinates) {
    return getGeometryFactory().createPoint(createCoordinate(coordinates));
  }

  /**
   * Create line string.
   *
   * @param coordinates the coordinates
   * @return the line string
   */
  public LineString createLineString(final List<Object> coordinates) {
    return getGeometryFactory().createLineString(createCoordinates(coordinates));
  }

  /**
   * Create polygon.
   *
   * @param coordinates the coordinates
   * @return the polygon
   */
  public Polygon createPolygon(final List<Object> coordinates) {
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

  /**
   * Create multi point.
   *
   * @param coordinates the coordinates
   * @return the multi point
   */
  public MultiPoint createMultiPoint(final List<Object> coordinates) {
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

  /**
   * Create multi line string.
   *
   * @param coordinates the coordinates
   * @return the multi line string
   */
  public MultiLineString createMultiLineString(final List<Object> coordinates) {
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

  /**
   * Create multi polygon.
   *
   * @param coordinates the coordinates
   * @return the multi polygon
   */
  public MultiPolygon createMultiPolygon(final List<Object> coordinates) {
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

  /**
   * Create geometry collection.
   *
   * @param geometries the geometries
   * @return the geometry collection
   */
  public GeometryCollection createGeometryCollection(final List<Geometry> geometries) {
    final Geometry[] geoms;
    if (geometries == null) {
      geoms = new Geometry[0];
    } else {
      geoms = geometries.toArray(new Geometry[0]);
    }
    GeometryFactory gf = getGeometryFactory();
    return gf.createGeometryCollection(geoms);
  }

  /**
   * Create json map.
   *
   * @param geometry the geometry
   * @return the map
   */
  public Map<String, Object> create(final Geometry geometry) {
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

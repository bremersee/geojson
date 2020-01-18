/*
 * Copyright 2018-2020 the original author or authors.
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
import java.util.regex.Pattern;
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

  private static final int MAXIMUM_INTEGER_DIGITS = 17;

  private static final int MAXIMUM_FRACTION_DIGITS = 9;

  private static final String TYPE_ATTRIBUTE_NAME = "type";

  private static final String COORDINATES_ATTRIBUTE_NAME = "coordinates";

  private static final String GEOMETRIES_ATTRIBUTE_NAME = "geometries";

  private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);

  private static final NumberFormat TEST_NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);

  static {
    NUMBER_FORMAT.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
    NUMBER_FORMAT.setMaximumIntegerDigits(MAXIMUM_INTEGER_DIGITS);
    NUMBER_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
    NUMBER_FORMAT.setGroupingUsed(false);

    TEST_NUMBER_FORMAT.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS + 1);
    TEST_NUMBER_FORMAT.setMaximumIntegerDigits(MAXIMUM_INTEGER_DIGITS + 1);
    TEST_NUMBER_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
    TEST_NUMBER_FORMAT.setGroupingUsed(false);
  }

  private final GeometryFactory geometryFactory;

  private final boolean useBigDecimal;

  /**
   * Instantiates a new convert helper.
   */
  public ConvertHelper() {
    this(null);
  }

  /**
   * Instantiates a new convert helper.
   *
   * @param useBigDecimal use {@link BigDecimal} (recommended for JSON, set to false for
   *     MongoDB)
   */
  @SuppressWarnings("unused")
  public ConvertHelper(boolean useBigDecimal) {
    this(null, useBigDecimal);
  }

  /**
   * Instantiates a new convert helper.
   *
   * @param geometryFactory the geometry factory
   */
  public ConvertHelper(final GeometryFactory geometryFactory) {
    this(geometryFactory, true);
  }

  /**
   * Instantiates a new convert helper.
   *
   * @param geometryFactory the geometry factory
   * @param useBigDecimal use {@link BigDecimal} (recommended for JSON, set to false for
   *     MongoDB)
   */
  public ConvertHelper(final GeometryFactory geometryFactory, boolean useBigDecimal) {
    this.geometryFactory = geometryFactory != null ? geometryFactory : new GeometryFactory();
    this.useBigDecimal = useBigDecimal;
  }

  /**
   * Gets geometry factory.
   *
   * @return the geometry factory
   */
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
        !coordinates.isEmpty() ? toPrimitiveDoubleValue(coordinates.get(0)) : Double.NaN;
    coordinate.y =
        coordinates.size() > 1 ? toPrimitiveDoubleValue(coordinates.get(1)) : Double.NaN;
    return coordinate;
  }

  private Coordinate[] coordinatesToArray(final List<Object> coordinates) {
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
    return getGeometryFactory().createLineString(coordinatesToArray(coordinates));
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
      list.add(coordinatesToArray(coords));
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
      return pointToJsonMap((Point) geometry);
    }

    if (geometry instanceof LineString) {
      return lineToJsonMap((LineString) geometry);
    }

    if (geometry instanceof Polygon) {
      return polygonToJsonMap((Polygon) geometry);
    }

    if (geometry instanceof MultiPoint) {
      return multiPointToJsonMap((MultiPoint) geometry);
    }

    if (geometry instanceof MultiLineString) {
      return multiLineToJsonMap((MultiLineString) geometry);
    }

    if (geometry instanceof MultiPolygon) {
      return multiPolygonToJsonMap((MultiPolygon) geometry);
    }

    if (geometry instanceof GeometryCollection) {
      return geometryCollectionToJsonMap((GeometryCollection) geometry);
    }

    throw new IllegalArgumentException("Geometry [" + geometry + "] is unsupported. It must be "
        + "an instance of Point, LineString, Polygon, MultiPoint, MultiLineString, MultiPolygon "
        + "or GeometryCollection.");
  }

  private Map<String, Object> pointToJsonMap(final Point point) {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    map.put(TYPE_ATTRIBUTE_NAME, "Point");
    map.put(COORDINATES_ATTRIBUTE_NAME, coordinateToList(point.getCoordinate()));
    return map;
  }

  private Map<String, Object> lineToJsonMap(final LineString line) {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    map.put(TYPE_ATTRIBUTE_NAME, "LineString");
    map.put(COORDINATES_ATTRIBUTE_NAME, coordinatesToList(line.getCoordinateSequence()));
    return map;
  }

  private Map<String, Object> polygonToJsonMap(final Polygon polygon) {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    map.put(TYPE_ATTRIBUTE_NAME, "Polygon");
    map.put(COORDINATES_ATTRIBUTE_NAME, polygonToList(polygon));
    return map;
  }

  private Map<String, Object> multiPointToJsonMap(final MultiPoint multiPoint) {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    map.put(TYPE_ATTRIBUTE_NAME, "MultiPoint");
    map.put(COORDINATES_ATTRIBUTE_NAME, geometryCollectionToList(multiPoint));
    return map;
  }

  private Map<String, Object> multiLineToJsonMap(final MultiLineString multiLine) {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    map.put(TYPE_ATTRIBUTE_NAME, "MultiLineString");
    map.put(COORDINATES_ATTRIBUTE_NAME, geometryCollectionToList(multiLine));
    return map;
  }

  private Map<String, Object> multiPolygonToJsonMap(final MultiPolygon multiPolygon) {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    map.put(TYPE_ATTRIBUTE_NAME, "MultiPolygon");
    map.put(COORDINATES_ATTRIBUTE_NAME, geometryCollectionToList(multiPolygon));
    return map;
  }

  private Map<String, Object> geometryCollectionToJsonMap(
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

  private Number round(final double value) {
    if (Double.isNaN(value)) {
      return null;
    }
    if (formatValue(value)) {
      final String strValue = NUMBER_FORMAT.format(value);
      return useBigDecimal ? new BigDecimal(strValue) : new BigDecimal(strValue).doubleValue();
    }
    return useBigDecimal ? BigDecimal.valueOf(value) : value;
  }

  private boolean formatValue(final double value) {
    final String[] testValues = TEST_NUMBER_FORMAT.format(value).split(Pattern.quote("."));
    return testValues[0].length() > MAXIMUM_INTEGER_DIGITS
        || (testValues.length > 1 && testValues[1].length() > MAXIMUM_FRACTION_DIGITS);
  }

  private List<Object> coordinateToList(final Coordinate coordinate) {
    if (coordinate == null) {
      return Collections.emptyList();
    }
    final List<Object> list = new ArrayList<>(2);

    final double dx = coordinate.getX();
    list.add(round(dx));

    final double dy = coordinate.getY();
    list.add(round(dy));

    return list;
  }

  private List<List<Object>> coordinatesToList(final CoordinateSequence coordinateSequence) {
    if (coordinateSequence == null || coordinateSequence.size() == 0) {
      return Collections.emptyList();
    }
    List<List<Object>> list = new ArrayList<>(coordinateSequence.size());
    for (int n = 0; n < coordinateSequence.size(); n++) {
      list.add(coordinateToList(coordinateSequence.getCoordinate(n)));
    }
    return list;
  }

  private List<List<List<Object>>> polygonToList(final Polygon polygon) {
    List<List<List<Object>>> list = new ArrayList<>();
    list.add(coordinatesToList(polygon.getExteriorRing().getCoordinateSequence()));
    for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
      list.add(coordinatesToList(polygon.getInteriorRingN(i).getCoordinateSequence()));
    }
    return list;
  }

  private List<Object> geometryCollectionToList(final GeometryCollection geometryCollection) {
    List<Object> list = new ArrayList<>(geometryCollection.getNumGeometries());
    for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
      Geometry g = geometryCollection.getGeometryN(i);
      if (g instanceof Polygon) {
        list.add(polygonToList((Polygon) g));
      } else if (g instanceof LineString) {
        list.add(coordinatesToList(((LineString) g).getCoordinateSequence()));
      } else if (g instanceof Point) {
        list.add(coordinateToList(g.getCoordinate()));
      }
    }
    return list;
  }

}

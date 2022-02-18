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

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static org.bremersee.geojson.GeoJsonConstants.BBOX;
import static org.bremersee.geojson.GeoJsonConstants.GEOMETRIES;
import static org.bremersee.geojson.GeoJsonConstants.GEOMETRY_COLLECTION;
import static org.bremersee.geojson.GeoJsonConstants.TYPE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

/**
 * The geometry to json converter.
 *
 * @author Christian Bremer
 */
public class GeometryToJsonConverter
    implements Converter<Geometry, Map<String, Object>>, Serializable {

  private static final long serialVersionUID = 1L;

  private final PointToJsonConverter pointConverter;

  private final LineStringToJsonConverter lineStringConverter;

  private final PolygonToJsonConverter polygonConverter;

  private final MultiPointToJsonConverter multiPointConverter;

  private final MultiLineStringToJsonConverter multiLineStringConverter;

  private final MultiPolygonToJsonConverter multiPolygonConverter;

  private final boolean withBoundingBox;

  /**
   * Instantiates a new geometry to json converter.
   */
  public GeometryToJsonConverter() {
    this(false, false);
  }

  /**
   * Instantiates a new geometry to json converter.
   *
   * @param withBoundingBox with bounding box
   * @param useBigDecimal use big decimal
   */
  public GeometryToJsonConverter(boolean withBoundingBox, boolean useBigDecimal) {
    CoordinateToListConverter coordinateConverter = new CoordinateToListConverter(useBigDecimal);
    CoordinateSequenceToListConverter coordinateSequenceConverter
        = new CoordinateSequenceToListConverter(coordinateConverter);

    pointConverter = new PointToJsonConverter(coordinateConverter, withBoundingBox);
    lineStringConverter = new LineStringToJsonConverter(
        coordinateSequenceConverter,
        withBoundingBox);
    polygonConverter = new PolygonToJsonConverter(coordinateSequenceConverter, withBoundingBox);

    multiPointConverter = new MultiPointToJsonConverter(pointConverter, withBoundingBox);
    multiLineStringConverter = new MultiLineStringToJsonConverter(
        lineStringConverter,
        withBoundingBox);
    multiPolygonConverter = new MultiPolygonToJsonConverter(polygonConverter, withBoundingBox);

    this.withBoundingBox = withBoundingBox;
  }

  @Override
  public Map<String, Object> convert(@NonNull Geometry source) {
    if (source instanceof Point) {
      return pointConverter.convert((Point) source);
    }

    if (source instanceof LineString) {
      return lineStringConverter.convert((LineString) source);
    }

    if (source instanceof Polygon) {
      return polygonConverter.convert((Polygon) source);
    }

    if (source instanceof MultiPoint) {
      return multiPointConverter.convert((MultiPoint) source);
    }

    if (source instanceof MultiLineString) {
      return multiLineStringConverter.convert((MultiLineString) source);
    }

    if (source instanceof MultiPolygon) {
      return multiPolygonConverter.convert((MultiPolygon) source);
    }

    if (source instanceof GeometryCollection) {
      List<Map<String, Object>> geometries = new ArrayList<>(source.getNumGeometries());
      for (int i = 0; i < source.getNumGeometries(); i++) {
        geometries.add(convert(source.getGeometryN(i)));
      }
      Map<String, Object> map = new LinkedHashMap<>();
      map.put(TYPE, GEOMETRY_COLLECTION);
      if (withBoundingBox) {
        Optional.ofNullable(GeoJsonGeometryFactory.getBoundingBox(source))
            .map(bbox -> Arrays.stream(bbox).boxed().collect(Collectors.toList()))
            .ifPresent(bbox -> map.put(BBOX, bbox));
      }
      map.put(GEOMETRIES, unmodifiableList(geometries));
      return unmodifiableMap(map);
    }

    throw new IllegalArgumentException("Geometry [" + source + "] is unsupported. It must be "
        + "an instance of Point, LineString, Polygon, MultiPoint, MultiLineString, MultiPolygon "
        + "or GeometryCollection.");
  }
}

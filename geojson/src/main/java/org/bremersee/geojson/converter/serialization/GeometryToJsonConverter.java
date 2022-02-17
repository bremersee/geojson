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

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static org.bremersee.geojson.GeoJsonConstants.GEOMETRIES;
import static org.bremersee.geojson.GeoJsonConstants.TYPE;
import static org.bremersee.geojson.GeoJsonConstants.GEOMETRY_COLLECTION;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

  public GeometryToJsonConverter() {
    this(false);
  }

  public GeometryToJsonConverter(boolean useBigDecimal) {
    CoordinateToListConverter coordinateConverter = new CoordinateToListConverter(useBigDecimal);
    CoordinateSequenceToListConverter coordinateSequenceConverter
        = new CoordinateSequenceToListConverter(coordinateConverter);

    pointConverter = new PointToJsonConverter(coordinateConverter);
    lineStringConverter = new LineStringToJsonConverter(coordinateSequenceConverter);
    polygonConverter = new PolygonToJsonConverter(coordinateSequenceConverter);

    multiPointConverter = new MultiPointToJsonConverter(pointConverter);
    multiLineStringConverter = new MultiLineStringToJsonConverter(lineStringConverter);
    multiPolygonConverter = new MultiPolygonToJsonConverter(polygonConverter);
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
      map.put(GEOMETRIES, unmodifiableList(geometries));
      return unmodifiableMap(map);
    }

    throw new IllegalArgumentException("Geometry [" + source + "] is unsupported. It must be "
        + "an instance of Point, LineString, Polygon, MultiPoint, MultiLineString, MultiPolygon "
        + "or GeometryCollection.");
  }
}

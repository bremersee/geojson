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

package org.bremersee.geojson.converter.deserialization;

import static org.bremersee.geojson.GeoJsonConstants.JSON_GEOMETRIES_ATTRIBUTE_NAME;
import static org.bremersee.geojson.GeoJsonConstants.JSON_TYPE_ATTRIBUTE;
import static org.bremersee.geojson.GeoJsonConstants.JSON_TYPE_GEOMETRY_COLLECTION;
import static org.bremersee.geojson.GeoJsonConstants.JSON_TYPE_LINESTRING;
import static org.bremersee.geojson.GeoJsonConstants.JSON_TYPE_MULTI_LINESTRING;
import static org.bremersee.geojson.GeoJsonConstants.JSON_TYPE_MULTI_POINT;
import static org.bremersee.geojson.GeoJsonConstants.JSON_TYPE_MULTI_POLYGON;
import static org.bremersee.geojson.GeoJsonConstants.JSON_TYPE_POINT;
import static org.bremersee.geojson.GeoJsonConstants.JSON_TYPE_POLYGON;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * The json to geometry converter.
 *
 * @author Christian Bremer
 */
public class JsonToGeometryConverter extends AbstractJsonToGeometryConverter {

  private static final long serialVersionUID = 1L;

  private final JsonToPointConverter pointConverter;

  private final JsonToLineStringConverter lineStringConverter;

  private final JsonToPolygonConverter polygonConverter;

  private final JsonToMultiPointConverter multiPointConverter;

  private final JsonToMultiLineStringConverter multiLineStringConverter;

  private final JsonToMultiPolygonConverter multiPolygonConverter;

  /**
   * Instantiates a new json to geometry converter.
   */
  public JsonToGeometryConverter() {
    this(new GeometryFactory());
  }

  /**
   * Instantiates a new json to geometry converter.
   *
   * @param geometryFactory the geometry factory
   */
  public JsonToGeometryConverter(GeometryFactory geometryFactory) {
    super(geometryFactory);
    ObjectToCoordinateConverter coordinateConverter = new ObjectToCoordinateConverter();
    ObjectToCoordinateSequenceConverter coordinateSequenceConverter
        = new ObjectToCoordinateSequenceConverter(coordinateConverter);

    pointConverter = new JsonToPointConverter(getGeometryFactory(), coordinateConverter);
    lineStringConverter = new JsonToLineStringConverter(
        getGeometryFactory(),
        coordinateSequenceConverter);
    polygonConverter = new JsonToPolygonConverter(
        getGeometryFactory(),
        coordinateSequenceConverter);
    multiPointConverter = new JsonToMultiPointConverter(
        getGeometryFactory(),
        pointConverter);
    multiLineStringConverter = new JsonToMultiLineStringConverter(
        getGeometryFactory(),
        lineStringConverter);
    multiPolygonConverter = new JsonToMultiPolygonConverter(
        getGeometryFactory(),
        polygonConverter);
  }

  /**
   * Convert geometry.
   *
   * @param source the source
   * @return the geometry
   */
  public Geometry convert(Map<String, Object> source) {
    if (isEmpty(source)) {
      return null;
    }
    String type = String.valueOf(source.get(JSON_TYPE_ATTRIBUTE));
    if (JSON_TYPE_POINT.equals(type)) {
      return pointConverter.convert(source);

    } else if (JSON_TYPE_LINESTRING.equals(type)) {
      return lineStringConverter.convert(source);

    } else if (JSON_TYPE_POLYGON.equals(type)) {
      return polygonConverter.convert(source);

    } else if (JSON_TYPE_MULTI_POINT.equals(type)) {
      return multiPointConverter.convert(source);

    } else if (JSON_TYPE_MULTI_LINESTRING.equals(type)) {
      return multiLineStringConverter.convert(source);

    } else if (JSON_TYPE_MULTI_POLYGON.equals(type)) {
      return multiPolygonConverter.convert(source);

    } else if (JSON_TYPE_GEOMETRY_COLLECTION.equals(type)) {
      Geometry[] geometries;
      Object value = source.get(JSON_GEOMETRIES_ATTRIBUTE_NAME);
      if (isEmpty(value)) {
        geometries = new Geometry[0];
      } else {
        //noinspection unchecked
        geometries = ((List<Map<String, Object>>) source.get(JSON_GEOMETRIES_ATTRIBUTE_NAME))
            .stream()
            .map(this::convert)
            .filter(Objects::nonNull)
            .toArray(Geometry[]::new);
      }
      return getGeometryFactory().createGeometryCollection(geometries);
    }

    throw new IllegalArgumentException(String.format("Illegal geometry: %s", source));
  }

}

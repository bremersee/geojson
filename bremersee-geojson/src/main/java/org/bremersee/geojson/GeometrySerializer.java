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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
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

/**
 * <p>
 * A Jackson serializer for a {@link Geometry}.
 * </p>
 * 
 * @author Christian Bremer
 */
public class GeometrySerializer extends StdSerializer<Geometry> {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public GeometrySerializer() {
        super(Geometry.class, false);
    }

    //@formatter:off
    /* (non-Javadoc)
     * @see com.fasterxml.jackson.databind.ser.std.StdSerializer#serialize(java.lang.Object, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
     */
    //@formatter:on
    @Override
    public void serialize(Geometry value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        if (value == null) {
            jgen.writeNull();
        } else {
            jgen.writeObject(create(value));
        }
    }

    private Map<String, Object> create(Geometry geometry) {
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

        throw new IllegalArgumentException("Geometry [" + geometry
                + "] is unsupported. It must be an instance of Point, LineString, Polygon, MultiPoint, MultiLineString, MultiPolygon or GeometryCollection.");
    }

    private Map<String, Object> createPoint(Point point) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

        map.put("type", "Point");
        map.put("coordinates", createCoordinates(point.getCoordinate()));
        return map;
    }

    private Map<String, Object> createLine(LineString line) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

        map.put("type", "LineString");
        map.put("coordinates", createCoordinates(line.getCoordinateSequence()));
        return map;
    }

    private Map<String, Object> createPolygon(Polygon polygon) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

        map.put("type", "Polygon");
        map.put("coordinates", createCoordinates(polygon));
        return map;
    }

    private Map<String, Object> createMultiPoint(MultiPoint multiPoint) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

        map.put("type", "MultiPoint");
        map.put("coordinates", createCoordinates(multiPoint));
        return map;
    }

    private Map<String, Object> createMultiLine(MultiLineString multiLine) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

        map.put("type", "MultiLineString");
        map.put("coordinates", createCoordinates(multiLine));
        return map;
    }

    private Map<String, Object> createMultiPolygon(MultiPolygon multiPolygon) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

        map.put("type", "MultiPolygon");
        map.put("coordinates", createCoordinates(multiPolygon));
        return map;
    }

    private Map<String, Object> createGeometryCollection(GeometryCollection geometryColllection) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

        List<Map<String, Object>> geoms = new ArrayList<Map<String, Object>>(geometryColllection.getNumGeometries());
        for (int i = 0; i < geometryColllection.getNumGeometries(); i++) {
            geoms.add(create(geometryColllection.getGeometryN(i)));
        }

        map.put("type", "GeometryCollection");
        map.put("geometries", geoms);
        return map;
    }

    private List<Object> createCoordinates(Coordinate coordinate) {
        if (coordinate == null) {
            return Collections.emptyList();
        }
        List<Object> list = new ArrayList<Object>(3);

        double dx = coordinate.x;
        long x = Double.valueOf(dx).longValue();
        if (dx == x) {
            list.add(x);
        } else {
            list.add(dx);
        }

        double dy = coordinate.y;
        long y = Double.valueOf(dy).longValue();
        if (dy == y) {
            list.add(y);
        } else {
            list.add(dy);
        }

        if (!Double.isNaN(coordinate.z)) {
            double dz = coordinate.z;
            long z = Double.valueOf(dz).longValue();
            if (dz == z) {
                list.add(z);
            } else {
                list.add(dz);
            }
        }
        return list;
    }

    private List<List<Object>> createCoordinates(CoordinateSequence coordinateSequence) {
        if (coordinateSequence == null || coordinateSequence.size() == 0) {
            return Collections.emptyList();
        }
        List<List<Object>> list = new ArrayList<List<Object>>(coordinateSequence.size());
        for (int n = 0; n < coordinateSequence.size(); n++) {
            list.add(createCoordinates(coordinateSequence.getCoordinate(n)));
        }
        return list;
    }

    private List<List<List<Object>>> createCoordinates(Polygon polygon) {
        List<List<List<Object>>> list = new ArrayList<List<List<Object>>>();
        list.add(createCoordinates(polygon.getExteriorRing().getCoordinateSequence()));
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            list.add(createCoordinates(polygon.getInteriorRingN(i).getCoordinateSequence()));
        }
        return list;
    }

    private List<Object> createCoordinates(GeometryCollection geometryCollection) {
        List<Object> list = new ArrayList<Object>(geometryCollection.getNumGeometries());
        for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
            Geometry g = geometryCollection.getGeometryN(i);
            if (g instanceof Polygon) {
                list.add(createCoordinates((Polygon) g));
            } else if (g instanceof LineString) {
                list.add(createCoordinates(((LineString) g).getCoordinateSequence()));
            } else if (g instanceof Point) {
                list.add(createCoordinates(((Point) g).getCoordinate()));
            }
        }
        return list;
    }

}

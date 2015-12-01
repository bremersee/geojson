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

package org.bremersee.geojson.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
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
import com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * <p>
 * Utility methods for geometry objects.
 * </p>
 * 
 * @author Christian Bremer
 */
public abstract class GeometryUtils {

	/**
	 * Never construct.
	 */
    private GeometryUtils() {
    }

    /**
     * The earth radius in meters.
     */
    public static final double EARTH_RADIUS_METERS = 6378137.;

    /**
     * Maximum latitude for mercator projection.
     */
    public static final double MERCATOR_MAX_LAT = 85.05112878;

    /**
     * Minimum latitude for mercator projection.
     */
    public static final double MERCATOR_MIN_LAT = -85.05112878;
    
    /**
     * Default spatial authority: 'EPSG'.
     */
    public static final String DEFAULT_SPATIAL_AUTHORITY = "EPSG";
    
    /**
     * Reference ID of WGS84: '4326'.
     */
    public static final int WGS84_SPATIAL_REFERENCE_ID = 4326;
    
    /**
     * CRS (Coordinate reference system) of WGS84: 'EPSG:4326'.
     */
    public static final String WGS84_CRS = DEFAULT_SPATIAL_AUTHORITY + ":" + WGS84_SPATIAL_REFERENCE_ID;
    
    /**
     * Reference ID of mercator projection: '3857'.
     */
    public static final int MERCATOR_SPATIAL_REFERENCE_ID = 3857;
    
    /**
     * Alternative reference ID of mercator: '900913'.
     */
    public static final int MERCATOR_SPATIAL_REFERENCE_ID_ALT = 900913;
    
    /**
     * CRS (Coordinate reference system) of mercator: 'EPSG:3857'.
     */
    public static final String MERCATOR_CRS = DEFAULT_SPATIAL_AUTHORITY + ":" + MERCATOR_SPATIAL_REFERENCE_ID;
    
    /**
     * Alternative CRS (Coordinate reference system) of mercator: 'EPSG:900913'.
     */
    public static final String MERCATOR_CRS_ALT = DEFAULT_SPATIAL_AUTHORITY + ":" + MERCATOR_SPATIAL_REFERENCE_ID_ALT;
    
    /**
     * Default geometry factory.
     */
    private static final GeometryFactory DEFAULT_GEOMETRY_FACTORY = new GeometryFactory();
    
    /**
     * Checks whether two geometry objects are equal.<br/>
     * Because the {@link GeometryCollection#equals(Geometry)} method throws an
     * exception, this method is used in the GeoJSON classes.
     * 
     * @param g1
     *            one geometry
     * @param g2
     *            another geometry
     * @return <code>true</code> if the geometries are equal otherwise
     *         <code>false</code>
     */
    public static boolean equals(Geometry g1, Geometry g2) {
        if (g1 == null && g2 == null) {
            return true;
        }
        if (g1 == null || g2 == null) {
            return false;
        }
        if (g1 == g2) {
            return true;
        }
        if (g1 instanceof GeometryCollection
                && g2 instanceof GeometryCollection) {
            return equals((GeometryCollection) g1, (GeometryCollection) g2);
        }
        try {
            return g1.equals(g2);
        } catch (Throwable t) {
            return false;
        }
    }

    private static boolean equals(GeometryCollection gc1, GeometryCollection gc2) {
        if (gc1 == null && gc2 == null) {
            return true;
        }
        if (gc1 == null || gc2 == null) {
            return false;
        }
        if (gc1.getNumGeometries() != gc2.getNumGeometries()) {
            return false;
        }
        for (int i = 0; i < gc1.getNumGeometries(); i++) {
            Geometry g1 = gc1.getGeometryN(i);
            Geometry g2 = gc2.getGeometryN(i);
            if (!equals(g1, g2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates the dimension of the specified geometry.<br/>
     * Because some geometry objects (e. g. {@link Polygon#getDimension()}
     * return a static value, this method is used in
     * {@link GeometryUtils#getBoundingBox(Geometry)}.
     * 
     * @param geometry
     *            the geometry
     * @return <code>null</code> if the dimension can not be calculated,
     *         otherwise the dimension of the geometry
     */
    public static Integer getDimension(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        Coordinate[] coords = geometry.getCoordinates();
        if (coords == null || coords.length == 0) {
            return null;
        }
        int maxDim = -1;
        for (Coordinate c : coords) {
            if (maxDim < 2 && !Double.isNaN(c.z)) {
                maxDim = 2;
            } else if (maxDim < 1 && !Double.isNaN(c.y)) {
                maxDim = 1;
            } else if (maxDim < 0 && !Double.isNaN(c.x)) {
                maxDim = 0;
            }
        }
        return maxDim > -1 ? maxDim : null;
    }

    //@formatter:off
    /**
     * Calculate the bounding box of the specified geometry (see 
     * <a href="http://geojson.org/geojson-spec.html#bounding-boxes">http://geojson.org/geojson-spec.html#bounding-boxes</a>
     * ).
     * 
     * @param geometry
     *            the geometry
     * @return <code>null</code> if the bounding box can not be calculated,
     *         otherwise the bounding box of the geometry
     */
    //@formatter:on
    public static double[] getBoundingBox(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        //@formatter:off
        // geometry.getDimension() = 0 || 1 || 2 (Polygon has always a dimension = 2)
        //@formatter:on
        Integer dimension = getDimension(geometry);
        if (dimension == null) {
            return null;
        }
        double[] values = new double[2 * (dimension + 1)];
        Coordinate[] coords = geometry.getCoordinates();
        if (coords == null || coords.length == 0) {
            return null;
        }
        Coordinate c0 = coords[0];
        for (int dim = 0; dim <= dimension; dim++) {
            values[dim] = c0.getOrdinate(dim);
            values[dim + dimension + 1] = c0.getOrdinate(dim);
        }
        for (int i = 1; i < coords.length; i++) {
            for (int dim = 0; dim <= dimension; dim++) {
                double value = coords[i].getOrdinate(dim);
                values[dim] = Math.min(value, values[dim]);
                values[dim + dimension + 1] = Math.max(value, values[dim
                        + dimension + 1]);
            }
        }
        return values;
    }

    public static Coordinate getSouthWest(double[] boundingBox) {
        if (boundingBox == null || !(boundingBox.length == 4 || boundingBox.length == 6)) {
            return null;
        }
        return createCoordinate(boundingBox[0], boundingBox[1]); // southWest
    }
    
    public static Coordinate getNorthWest(double[] boundingBox) {
        if (boundingBox == null || !(boundingBox.length == 4 || boundingBox.length == 6)) {
            return null;
        }
        if (boundingBox.length == 6) {
            //  0   1   2   3   4   5
            // x0, y0, z0, x1, y1, z1
            return createCoordinate(boundingBox[0], boundingBox[4]);
        } else {
            //  0   1   2   3
            // x0, y0, x1, y1
            return createCoordinate(boundingBox[0], boundingBox[3]);
        }
    }
    
    public static Coordinate getNorthEast(double[] boundingBox) {
        if (boundingBox == null || !(boundingBox.length == 4 || boundingBox.length == 6)) {
            return null;
        }
        if (boundingBox.length == 6) {
            //  0   1   2   3   4   5
            // x0, y0, z0, x1, y1, z1
            return createCoordinate(boundingBox[3], boundingBox[4]);
        } else {
            //  0   1   2   3
            // x0, y0, x1, y1
            return createCoordinate(boundingBox[2], boundingBox[3]);
        }
    }
    
    public static Coordinate getSouthEast(double[] boundingBox) {
        if (boundingBox == null || !(boundingBox.length == 4 || boundingBox.length == 6)) {
            return null;
        }
        if (boundingBox.length == 6) {
            //  0   1   2   3   4   5
            // x0, y0, z0, x1, y1, z1
            return createCoordinate(boundingBox[3], boundingBox[1]);
        } else {
            //  0   1   2   3
            // x0, y0, x1, y1
            return createCoordinate(boundingBox[2], boundingBox[1]);
        }
    }
    
    public static Polygon getBoundingBoxAsPolygon2D(Geometry geometry) {
        return getBoundingBoxAsPolygon2D(geometry, null);
    }
    
    public static Polygon getBoundingBoxAsPolygon2D(Geometry geometry, GeometryFactory geometryFactory) {
        final double[] bbox = getBoundingBox(geometry);
        if (bbox == null || !(bbox.length == 4 || bbox.length == 6)) {
            return null;
        }
        
        int add = bbox.length == 6 ? 1 : 0;
        if (bbox[0] == bbox[2 + add] || bbox[1] == bbox[3 + add]) {
            return null;
        }
        
        Coordinate c0 = getSouthWest(bbox);
        Coordinate c1 = getNorthWest(bbox);
        Coordinate c2 = getNorthEast(bbox);
        Coordinate c3 = getSouthEast(bbox);
        
        if (geometryFactory == null) {
            geometryFactory = DEFAULT_GEOMETRY_FACTORY;
        }
        return geometryFactory.createPolygon(new Coordinate[] { c0, c1, c2, c3, c0 });
    }

    public static String toWKT(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.toText();
    }
    
    public static Geometry fromWKT(String wkt) throws RuntimeException {
        return fromWKT(wkt, null);
    }
    
    public static Geometry fromWKT(String wkt, GeometryFactory geometryFactory) throws RuntimeException {
        if (wkt == null || wkt.trim().length() == 0) {
            return null;
        }
        if (geometryFactory == null) {
            geometryFactory = DEFAULT_GEOMETRY_FACTORY;
        }
        WKTReader wktReader = new WKTReader(geometryFactory);
        try {
            return wktReader.read(wkt);
        } catch (ParseException e) {
            throw new RuntimeException("Parsing WKT [" + wkt + "] failed.", e);
        }
    }

    public static Geometry fromWKT(Reader reader) throws RuntimeException {
        return fromWKT(reader, null);
    }
    
    public static Geometry fromWKT(Reader reader, GeometryFactory geometryFactory) throws RuntimeException {
        if (reader == null) {
            return null;
        }
        if (geometryFactory == null) {
            geometryFactory = DEFAULT_GEOMETRY_FACTORY;
        }
        WKTReader wktReader = new WKTReader(geometryFactory);
        try {
            return wktReader.read(reader);
        } catch (ParseException e) {
            throw new RuntimeException("Parsing WKT failed.", e);
        }
    }
    
    public static Geometry fromWKT(InputStream inputStream, String charsetName) throws RuntimeException {
        return fromWKT(inputStream, charsetName, null);
    }

    public static Geometry fromWKT(InputStream inputStream, String charsetName, GeometryFactory geometryFactory) {
        if (inputStream == null) {
            return null;
        }
        if (charsetName == null || charsetName.trim().length() == 0) {
            charsetName = StandardCharsets.UTF_8.name();
        }
        if (geometryFactory == null) {
            geometryFactory = DEFAULT_GEOMETRY_FACTORY;
        }
        try {
            return fromWKT(new InputStreamReader(inputStream, charsetName));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Parsing WKT failed.", e);
        }
    }

    public static Coordinate createCoordinate(double x, double y) {
        return createCoordinate(x, y, null);
    }

    public static Coordinate createCoordinate(double x, double y, Double z) {
        return z == null || z.isNaN() ? new Coordinate(x, y)
                : new Coordinate(x, y, z);
    }
    
    public static Point createPoint(double x, double y) {
        return createPoint(x, y, null, null);
    }

    public static Point createPoint(double x, double y, GeometryFactory geometryFactory) {
        return createPoint(x, y, null, geometryFactory);
    }
    
    public static Point createPoint(double x, double y, Double z) {
        return createPoint(x, y, z, null);
    }

    public static Point createPoint(double x, double y, Double z, GeometryFactory geometryFactory) {
        Coordinate coordinate = createCoordinate(x, y, z);
        if (geometryFactory == null) {
            geometryFactory = DEFAULT_GEOMETRY_FACTORY;
        }
        return geometryFactory.createPoint(coordinate);
    }
    
    public static Point createPoint(Coordinate coordinate) {
        return createPoint(coordinate, null);
    }
    
    public static Point createPoint(Coordinate coordinate, GeometryFactory geometryFactory) {
        if (geometryFactory == null) {
            geometryFactory = DEFAULT_GEOMETRY_FACTORY;
        }
        return geometryFactory.createPoint(coordinate);
    }
    
    public static MultiPoint createMultiPoint(Collection<? extends Point> points) {
        return createMultiPoint(points, null);
    }
    
    public static MultiPoint createMultiPoint(Collection<? extends Point> points, GeometryFactory geometryFactory) {
        Point[] ps;
        if (points == null) {
            ps = null;
        } else {
            ps = points.toArray(new Point[points.size()]);
        }
        if (geometryFactory == null) {
            geometryFactory = DEFAULT_GEOMETRY_FACTORY;
        }
        return geometryFactory.createMultiPoint(ps);
    }
    
    public static LineString createLineString(Collection<? extends Coordinate> coordinates) {
        return createLineString(coordinates, null);
    }
    
    public static LineString createLineString(Collection<? extends Coordinate> coordinates, GeometryFactory geometryFactory) {
        CoordinateSequence points;
        if (coordinates == null) {
            points = null;
        } else {
            Coordinate[] coords = coordinates.toArray(new Coordinate[coordinates.size()]);
            points = CoordinateArraySequenceFactory.instance().create(coords);
        }
        if (geometryFactory == null) {
            geometryFactory = DEFAULT_GEOMETRY_FACTORY;
        }
        return geometryFactory.createLinearRing(points);
    }
    
    public static MultiLineString createMultiLineString(Collection<? extends LineString> lineStrings) {
        return createMultiLineString(lineStrings, null);
    }
    
    public static MultiLineString createMultiLineString(Collection<? extends LineString> lineStrings, GeometryFactory geometryFactory) {
        LineString[] lines;
        if (lineStrings == null) {
            lines = null;
        } else {
            lines = lineStrings.toArray(new LineString[lineStrings.size()]);
        }
        if (geometryFactory == null) {
            geometryFactory = DEFAULT_GEOMETRY_FACTORY;
        }
        return geometryFactory.createMultiLineString(lines);
    }
    
    public static LinearRing createLinearRing(Collection<? extends Coordinate> coordinates) {
        return createLinearRing(coordinates, null);
    }
    
    public static LinearRing createLinearRing(Collection<? extends Coordinate> coordinates, GeometryFactory geometryFactory) {
        CoordinateSequence points;
        if (coordinates == null) {
            points = null;
        } else {
            Coordinate[] coords = coordinates.toArray(new Coordinate[coordinates.size()]);
            if (coords.length > 0) {
                Coordinate coord0 = coords[0];
                Coordinate coordN = coords[coords.length - 1];
                if (!coord0.equals3D(coordN)) {
                    coords = Arrays.copyOf(coords, coords.length + 1);
                    coords[coords.length - 1] = coord0;
                }
            }
            points = CoordinateArraySequenceFactory.instance().create(coords);
        }
        if (geometryFactory == null) {
            geometryFactory = DEFAULT_GEOMETRY_FACTORY;
        }
        return geometryFactory.createLinearRing(points);
    }
    
    public static Polygon createPolygon(LinearRing shell) {
        return createPolygon(shell, null, null);
    }
    
    public static Polygon createPolygon(LinearRing shell, GeometryFactory geometryFactory) {
        return createPolygon(shell, null, geometryFactory);
    }
    
    public static Polygon createPolygon(LinearRing shell, Collection<? extends LinearRing> holes) {
        return createPolygon(shell, holes, null);
    }
    
    public static Polygon createPolygon(LinearRing shell, Collection<? extends LinearRing> holes, GeometryFactory geometryFactory) {
        LinearRing[] hs;
        if (holes == null) {
            hs = null;
        } else {
            hs = holes.toArray(new LinearRing[holes.size()]);
        }
        if (geometryFactory == null) {
            geometryFactory = DEFAULT_GEOMETRY_FACTORY;
        }
        return geometryFactory.createPolygon(shell, hs);
    }
    
    public static MultiPolygon createMultiPolygon(Collection<? extends Polygon> polygons) {
        return createMultiPolygon(polygons, null);
    }
    
    public static MultiPolygon createMultiPolygon(Collection<? extends Polygon> polygons, GeometryFactory geometryFactory) {
        Polygon[] ps;
        if (polygons == null) {
            ps = null;
        } else {
            ps = polygons.toArray(new Polygon[polygons.size()]);
        }
        if (geometryFactory == null) {
            geometryFactory = DEFAULT_GEOMETRY_FACTORY;
        }
        if (geometryFactory == null) {
            geometryFactory = DEFAULT_GEOMETRY_FACTORY;
        }
        return geometryFactory.createMultiPolygon(ps);
    }
    
    public static Geometry transformWgs84ToMercator(Geometry geometry, boolean clone, boolean removeZ) {
        if (geometry == null) {
            return null;
        }
        Geometry result = clone ? (Geometry)geometry.clone() : geometry;
        result.apply(new Wgs84ToMercatorCoordinateFilter(removeZ));
        return result;
    }
    
    public static Geometry transformMercatorToWgs84(Geometry geometry, boolean clone, boolean removeZ) {
        if (geometry == null) {
            return null;
        }
        Geometry result = clone ? (Geometry)geometry.clone() : geometry;
        result.apply(new MercatorToWgs84CoordinateFilter(removeZ));
        return result;
    }
    
}

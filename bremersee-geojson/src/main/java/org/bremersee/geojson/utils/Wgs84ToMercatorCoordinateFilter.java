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

import java.io.Serializable;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;

/**
 * @author Christian Bremer
 *
 */
public class Wgs84ToMercatorCoordinateFilter
        implements CoordinateFilter, Serializable {

    private static final long serialVersionUID = 1L;

    private double earthRadiusInMeters = GeometryUtils.EARTH_RADIUS_METERS;

    private boolean removingZ = false;

    public Wgs84ToMercatorCoordinateFilter() {
    }

    public Wgs84ToMercatorCoordinateFilter(double earthRadiusInMeters) {
        this.earthRadiusInMeters = earthRadiusInMeters;
    }

    public Wgs84ToMercatorCoordinateFilter(boolean removingZ) {
        this.removingZ = removingZ;
    }

    public Wgs84ToMercatorCoordinateFilter(double earthRadiusInMeters,
            boolean removingZ) {
        this.earthRadiusInMeters = earthRadiusInMeters;
        this.removingZ = removingZ;
    }

    public double getEarthRadiusInMeters() {
        return earthRadiusInMeters;
    }

    public void setEarthRadiusInMeters(double earthRadiusInMeters) {
        this.earthRadiusInMeters = earthRadiusInMeters;
    }

    public boolean isRemovingZ() {
        return removingZ;
    }

    public void setRemovingZ(boolean removingZ) {
        this.removingZ = removingZ;
    }

    //@formatter:off
    /* (non-Javadoc)
     * @see com.vividsolutions.jts.geom.CoordinateFilter#filter(com.vividsolutions.jts.geom.Coordinate)
     */
    //@formatter:on
    @Override
    public void filter(Coordinate coord) {

        if (coord != null) {
            if (!Double.isNaN(coord.x)) {
                coord.x = coord.x * getEarthRadiusInMeters() * Math.PI / 180.;
            }
            if (!Double.isNaN(coord.y)) {
                if (coord.y > GeometryUtils.MERCATOR_MAX_LAT) {
                    coord.y = GeometryUtils.MERCATOR_MAX_LAT;
                } else if (coord.y < GeometryUtils.MERCATOR_MIN_LAT) {
                    coord.y = GeometryUtils.MERCATOR_MIN_LAT;
                }
                coord.y = Math
                        .log(Math
                                .tan(Math.PI / 4 + Math.toRadians(coord.y) / 2))
                        * getEarthRadiusInMeters();
            }
            if (removingZ) {
                coord.z = Double.NaN;
            }
        }
    }

}

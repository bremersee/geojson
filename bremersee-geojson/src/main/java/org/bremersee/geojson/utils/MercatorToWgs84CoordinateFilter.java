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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;

/**
 * @author Christian Bremer
 */
public class MercatorToWgs84CoordinateFilter implements CoordinateFilter {

    private double earthRadiusInMeters = GeometryUtils.EARTH_RADIUS_METERS;

    private boolean removingZ = false;

    public MercatorToWgs84CoordinateFilter() {
    }

    public MercatorToWgs84CoordinateFilter(double earthRadiusInMeters) {
        this.earthRadiusInMeters = earthRadiusInMeters;
    }

    public MercatorToWgs84CoordinateFilter(boolean removingZ) {
        this.removingZ = removingZ;
    }

    public MercatorToWgs84CoordinateFilter(double earthRadiusInMeters,
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
                coord.x = (coord.x * 180.)
                        / (getEarthRadiusInMeters() * Math.PI);
            }
            if (!Double.isNaN(coord.y)) {
                coord.y = Math
                        .toDegrees(2
                                * Math.atan(Math.exp(
                                        coord.y / getEarthRadiusInMeters()))
                        - Math.PI / 2);
            }
            if (removingZ) {
                coord.z = Double.NaN;
            }
        }
    }

}

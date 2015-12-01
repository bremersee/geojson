/*
 * Copyright 2012-2015 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * <p>
 * A refernce to the coordinate reference system (CRS) of a GeoJSON object by
 * it's name (see 
 * <a href="http://geojson.org/geojson-spec.html#named-crs">http://geojson.org/geojson-spec.html#named-crs</a>
 * ).
 * </p>
 * 
 * @author Christian Bremer <a href="mailto:christian@bremersee.org">christian@bremersee.org</a>
 */
public class GeoJsonNamedCrs extends AbstractGeoJsonCrs {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public GeoJsonNamedCrs() {
    }

    /**
     * Construct a coordinate reference system (CRS) with it's name.
     * 
     * @param crsName
     *            the name of the coordinate reference system (CRS)
     */
    public GeoJsonNamedCrs(String crsName) {
        setCrs(crsName);
    }

    /**
     * Return the name of the coordinate reference system (CRS).
     * 
     * @return the name of the coordinate reference system (CRS)
     */
    @JsonIgnore
    public String getCrs() {
        Object tmp = getProperties().get("name");
        return tmp == null ? null : tmp.toString();
    }

    /**
     * Set the name of the coordinate reference system (CRS).
     * 
     * @param crs
     *            the name
     */
    @JsonIgnore
    public void setCrs(String crs) {
        if (crs == null || crs.trim().length() == 0) {
            getProperties().remove("name");
        } else {
            getProperties().put("name", crs);
        }
    }

}

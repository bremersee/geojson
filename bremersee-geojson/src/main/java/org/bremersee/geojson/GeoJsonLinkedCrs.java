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

import io.swagger.annotations.ApiModel;

//@formatter:off
/**
 * <p>
 * A link to the coordinate reference system (CRS) of a GeoJSON object (see 
 * <a href="http://geojson.org/geojson-spec.html#linked-crs">http://geojson.org/geojson-spec.html#linked-crs</a>
 * ).
 * </p>
 * 
 * @author Christian Bremer
 */
@ApiModel(
        value = "GeoJsonLinkedCrs",
        description = "A link to the coordinate reference system (CRS) of a GeoJSON object.",
        parent = AbstractGeoJsonCrs.class)
//@formatter:on
public class GeoJsonLinkedCrs extends AbstractGeoJsonCrs {

    private static final long serialVersionUID = 1L;

    /**
     * The coordinate reference system (CRS) with type <code>proj4</code>.
     */
    public static final String TYPE_PROJ4;

    /**
     * The coordinate reference system (CRS) with type <code>ogcwkt</code>.
     */
    public static final String TYPE_OGC_WKT;


    /**
     * The coordinate reference system (CRS) with type <code>esriwkt</code>.
     */
    public static final String TYPE_ESRI_WKT;

    static {
        TYPE_PROJ4 = "proj4";
        TYPE_OGC_WKT = "ogcwkt";
        TYPE_ESRI_WKT = "esriwkt";
    }

    /**
     * Default constructor.
     */
    public GeoJsonLinkedCrs() {
        super();
    }

    /**
     * Constructs the coordinate reference system (CRS) with the specified link.
     * 
     * @param href
     *            the link to the coordinate reference system (CRS)
     */
    public GeoJsonLinkedCrs(String href) {
        setHref(href);
    }

    /**
     * Constructs the coordinate reference system (CRS) with the specified link
     * and type.
     * 
     * @param href
     *            the link to the coordinate reference system (CRS)
     * @param type
     *            the type of the coordinate reference system (CRS)
     */
    public GeoJsonLinkedCrs(String href, String type) {
        setHref(href);
        setType(type);
    }

    /**
     * Return the link to the coordinate reference system (CRS).
     * 
     * @return the link to the coordinate reference system (CRS)
     */
    @JsonIgnore
    public String getHref() {
        Object tmp = getProperties().get("href");
        return tmp == null ? null : tmp.toString();
    }

    /**
     * Set the link to the coordinate reference system (CRS)
     * 
     * @param href
     *            the link to the coordinate reference system (CRS)
     */
    @JsonIgnore
    public void setHref(String href) {
        if (href == null || href.trim().length() == 0) {
            getProperties().remove("href");
        } else {
            getProperties().put("href", href);
        }
    }

    /**
     * Return the type of the coordinate reference system (CRS)
     * 
     * @return the type of the coordinate reference system (CRS)
     */
    @JsonIgnore
    public String getType() {
        Object tmp = getProperties().get("type");
        return tmp == null ? null : tmp.toString();
    }

    /**
     * Set the type of the coordinate reference system (CRS)
     * 
     * @param type
     *            the type of the coordinate reference system (CRS)
     */
    @JsonIgnore
    public void setType(String type) {
        if (type == null || type.trim().length() == 0) {
            getProperties().remove("type");
        } else {
            getProperties().put("type", type);
        }
    }

}

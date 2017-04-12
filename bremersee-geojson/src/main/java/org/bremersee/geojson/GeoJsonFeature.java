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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Geometry;
import io.swagger.annotations.ApiModel;
import org.bremersee.geojson.utils.GeometryUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

//@formatter:off
/**
 * <p>
 * A GeoJSON object with the type <code>Feature</code> (see
 * <a href="http://geojson.org/geojson-spec.html#feature-objects">http://geojson.org/geojson-spec.html#feature-objects</a>
 * ).
 * </p>
 * 
 * @author Christian Bremer
 */
@ApiModel(
        value = "GeoJsonFeature",
        description = "A GeoJSON object with the type Feature.")
//@formatter:on
public class GeoJsonFeature implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty
    private AbstractGeoJsonCrs crs = null;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty
    private double[] bbox = null;

    @JsonInclude(Include.ALWAYS)
    @JsonProperty(required = true)
    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    private Geometry geometry = null;

    @JsonProperty(required = true)
    private Map<String, Object> properties = new LinkedHashMap<>(); //NOSONAR

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty
    private String id = null;

    /**
     * Default constructor.
     */
    public GeoJsonFeature() {
        super();
    }

    /**
     * Constructs a GeoJSON feature with the specified parameters.
     * 
     * @param id
     *            an optional id
     * @param crs
     *            an optional coordinate reference system
     * @param geometry
     *            the geometry of the GeoJson feature
     * @param setBounds
     *            if <code>true</code> the bounding box will be calculated
     *            otherwise the bounding box will be <code>null</code>
     * @param properties
     *            a map with named objects that are associated with the GeoJSON
     *            feature
     */
    public GeoJsonFeature(final String id, final AbstractGeoJsonCrs crs, final Geometry geometry,
                          final boolean setBounds, final Map<String, Object> properties) {
        if (id != null && id.trim().length() > 0) {
            setId(id);
        }
        setCrs(crs);
        setGeometry(geometry);
        if (setBounds && geometry != null) {
            setBbox(GeometryUtils.getBoundingBox(geometry));
        }
        if (properties != null) {
            getProperties().putAll(properties);
        }
    }

    /**
     * Constructs a GeoJSON feature with the specified parameters.
     * 
     * @param id
     *            an optional id
     * @param crsName
     *            the name of the CRS (may be {@code null})
     * @param geometry
     *            the geometry of the GeoJson feature
     * @param setBounds
     *            if <code>true</code> the bounding box will be calculated
     *            otherwise the bounding box will be <code>null</code>
     * @param properties
     *            a map with named objects that are associated with the GeoJSON
     *            feature
     */
    public GeoJsonFeature(final String id, final String crsName, final Geometry geometry, final boolean setBounds,
                          final Map<String, Object> properties) {
        this(id, crsName != null && crsName.trim().length() > 0 ? new GeoJsonNamedCrs(crsName) : null, geometry,
                setBounds, properties);
    }

    @Override
    public String toString() {
        return String.format("%s [id = %s, crs = %s, bbox = %s, geometry = %s, properties = %s]",
                getClass().getSimpleName(), id, crs, Arrays.toString(bbox), geometry, properties);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(bbox);
        result = prime * result + ((crs == null) ? 0 : crs.hashCode());
        result = prime * result + ((geometry == null) ? 0 : geometry.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) { //NOSONAR
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GeoJsonFeature other = (GeoJsonFeature) obj;
        if (!Arrays.equals(bbox, other.bbox))
            return false;
        if (crs == null) {
            if (other.crs != null)
                return false;
        } else if (!crs.equals(other.crs))
            return false;
        if (geometry == null) {
            if (other.geometry != null)
                return false;
        } else if (!GeometryUtils.equals(geometry, other.geometry))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        return true;
    }

    //@formatter:off
    /**
     * Return the coordinate reference system (CRS) of this GeoJSON object or
     * <code>null</code> if there is no such object (see 
     * <a href="http://geojson.org/geojson-spec.html#coordinate-reference-system-objects">http://geojson.org/geojson-spec.html#coordinate-reference-system-objects</a>
     * ).
     * 
     * @return the coordinate reference system (CRS)
     */
    //@formatter:on
    public AbstractGeoJsonCrs getCrs() {
        return crs;
    }

    /**
     * Set the coordinate reference system (CRS).
     * 
     * @param crs
     *            the crs to set
     */
    public void setCrs(final AbstractGeoJsonCrs crs) {
        this.crs = crs;
    }

    //@formatter:off
    /**
     * Return the bounding box of the GeoJSON object or <code>null</code> if
     * there is no such object (see
     * <a href="http://geojson.org/geojson-spec.html#bounding-boxes">http://geojson.org/geojson-spec.html#bounding-boxes</a>
     * ).
     * 
     * @return the bounding box
     */
    //@formatter:on
    public double[] getBbox() {
        return bbox;
    }

    /**
     * Set the bounding box of the GeoJSON object.
     * 
     * @param bbox
     *            the bounding box to set
     */
    public void setBbox(final double[] bbox) {
        this.bbox = bbox;
    }

    /**
     * Return the geometry object of this GeoJSON feature.
     * 
     * @return the geometry
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * Set the geometry object of this GeoJSON feature.
     * 
     * @param geometry
     *            the geometry to set
     */
    public void setGeometry(final Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * Return a map of named objects that are associated with this GeoJSON
     * feature.
     * 
     * @return a map of named objects
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Set a map of named objects that are associated with this GeoJSON feature.
     * <br>
     * Be aware that each object must be serializable with the Jackson JSON
     * processor.
     * 
     * @param properties
     *            a map of named objects
     */
    public void setProperties(final Map<String, Object> properties) {
        if (properties == null) {
            this.properties = new LinkedHashMap<>();
        } else {
            this.properties = properties;
        }
    }

    /**
     * Return the id of this GeoJSON feature or <code>null</code> if there is no
     * id available.
     * 
     * @return the id of this GeoJSON feature
     */
    public String getId() {
        return id;
    }

    /**
     * Set the id of this GeoJSON feature.
     * 
     * @param id
     *            the id of this GeoJSON feature
     */
    public void setId(final String id) {
        this.id = id;
    }

}

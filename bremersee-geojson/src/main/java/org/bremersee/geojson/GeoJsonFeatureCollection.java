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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bremersee.geojson.utils.GeometryUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

//@formatter:off
/**
 * <p>
 * A GeoJSON object with the type <code>FeatureCollection</code> is a feature
 * collection object (see
 * <a href="http://geojson.org/geojson-spec.html#feature-collection-objects">http://geojson.org/geojson-spec.html#feature-collection-objects</a>
 * ).
 * </p>
 * 
 * @author Christian Bremer
 */
//@formatter:on
public class GeoJsonFeatureCollection implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty(required = false)
    private AbstractGeoJsonCrs crs = null;

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty(required = false)
    private double[] bbox = null;

    @JsonInclude(Include.ALWAYS)
    @JsonProperty(required = true)
    private List<GeoJsonFeature> features = new ArrayList<GeoJsonFeature>();

    @JsonInclude(Include.ALWAYS)
    @JsonProperty(required = true)
    private Map<String, Object> properties = new LinkedHashMap<String, Object>();

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty(required = false)
    private String id = null;

    /**
     * Default constructor.
     */
    public GeoJsonFeatureCollection() {
    }

    /**
     * Constructs a GeoJSON feature collection with the specified parameters.
     * 
     * @param id
     *            an optional id
     * @param crs
     *            an optional coordinate reference system
     * @param features
     *            the GeoJSON features of the collection
     * @param setBounds
     *            if <code>true</code> the bounding box will be calculated
     *            otherwise the bounding box will be <code>null</code>
     * @param properties
     *            a map with named objects that are associated with the GeoJSON
     *            feature collection
     */
    public GeoJsonFeatureCollection(String id, AbstractGeoJsonCrs crs,
            Collection<? extends GeoJsonFeature> features, boolean setBounds,
            Map<String, Object> properties) {
        if (id != null && id.trim().length() > 0) {
            setId(id);
        }
        setCrs(crs);
        if (features != null) {
            getFeatures().addAll(features);
            if (setBounds) {
                List<Geometry> glist = new ArrayList<Geometry>();
                for (GeoJsonFeature f : features) {
                    if (f.getGeometry() != null) {
                        glist.add(f.getGeometry());
                    }
                }
                Geometry[] geometries = glist
                        .toArray(new Geometry[glist.size()]);
                GeometryCollection gc = new GeometryCollection(geometries,
                        new GeometryFactory());
                setBbox(GeometryUtils.getBoundingBox(gc));
            }
        }
        if (properties != null) {
            getProperties().putAll(properties);
        }
    }

    /**
     * Constructs a GeoJSON feature collection with the specified parameters.
     * 
     * @param id
     *            an optional id
     * @param crsName
     *            the name of the CRS (may be {@code null})
     * @param features
     *            the GeoJSON features of the collection
     * @param setBounds
     *            if <code>true</code> the bounding box will be calculated
     *            otherwise the bounding box will be <code>null</code>
     * @param properties
     *            a map with named objects that are associated with the GeoJSON
     *            feature collection
     */
    public GeoJsonFeatureCollection(String id, String crsName,
            Collection<? extends GeoJsonFeature> features, boolean setBounds,
            Map<String, Object> properties) {
        this(id, crsName != null && crsName.trim().length() > 0
                ? new GeoJsonNamedCrs(crsName) : null, features, setBounds,
                properties);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format(
                "%s [id = %s, crs = %s, bbox = %s, features = %s, properties = %s]",
                getClass().getSimpleName(), id, crs, bbox, features,
                properties);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(bbox);
        result = prime * result + ((crs == null) ? 0 : crs.hashCode());
        result = prime * result
                + ((features == null) ? 0 : features.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((properties == null) ? 0 : properties.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GeoJsonFeatureCollection other = (GeoJsonFeatureCollection) obj;
        if (!Arrays.equals(bbox, other.bbox))
            return false;
        if (crs == null) {
            if (other.crs != null)
                return false;
        } else if (!crs.equals(other.crs))
            return false;
        if (features == null) {
            if (other.features != null)
                return false;
        } else if (!features.equals(other.features))
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
    public void setCrs(AbstractGeoJsonCrs crs) {
        this.crs = crs;
    }

    //@formatter:off
    /**
     * Return the bounding box of the GeoJSON object or <code>null</code> if
     * there is no such object (see
     * <a href="http://geojson.org/geojson-spec.html#bounding-boxes" >http://geojson.org/geojson-spec.html#bounding-boxes</a>
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
    public void setBbox(double[] bbox) {
        this.bbox = bbox;
    }

    /**
     * Return the features of this GeoJSON feature collection.
     * 
     * @return the features of this GeoJSON feature collection
     */
    public List<GeoJsonFeature> getFeatures() {
        return features;
    }

    /**
     * Set the features of this GeoJSON feature collection.
     * 
     * @param features
     *            the features to set
     */
    public void setFeatures(List<GeoJsonFeature> features) {
        if (features == null) {
            features = new ArrayList<GeoJsonFeature>();
        }
        this.features = features;
    }

    /**
     * Return a map of named objects that are associated with this GeoJSON
     * feature collection.
     * 
     * @return a map of named objects
     */
    @JsonInclude(Include.ALWAYS)
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Set a map of named objects that are associated with this GeoJSON feature
     * collection.<br/>
     * Be aware that each object must be serializable with the Jackson JSON
     * processor.
     * 
     * @param properties
     *            a map of named objects
     */
    public void setProperties(Map<String, Object> properties) {
        if (properties == null) {
            properties = new LinkedHashMap<String, Object>();
        }
        this.properties = properties;
    }

    /**
     * Return the id of this GeoJSON feature collection or <code>null</code> if
     * there is no id available.
     * 
     * @return the id of this GeoJSON feature collection
     */
    public String getId() {
        return id;
    }

    /**
     * Set the id of this GeoJSON feature collection.
     * 
     * @param id
     *            the id of this GeoJSON feature collection
     */
    public void setId(String id) {
        this.id = id;
    }

}

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
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import io.swagger.annotations.ApiModel;

//@formatter:off
/**
 * <p>
 * Abstract base class for the coordinate reference system (CRS) of a GeoJSON
 * object (see
 * <a href="http://geojson.org/geojson-spec.html#coordinate-reference-system-objects">http://geojson.org/geojson-spec.html#coordinate-reference-system-objects</a>
 * ).
 * </p>
 * 
 * @author Christian Bremer
 */
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({ 
	@Type(value = GeoJsonLinkedCrs.class, name = "link"),
	@Type(value = GeoJsonNamedCrs.class, name = "name") 
})
@ApiModel(
        description = "Abstract base class for the coordinate reference system (CRS) "
                + "of a GeoJSON object.", 
        subTypes = { GeoJsonNamedCrs.class, GeoJsonLinkedCrs.class }, 
        discriminator = "type")
//@formatter:on
public class AbstractGeoJsonCrs implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonInclude(Include.ALWAYS)
    @JsonProperty(value = "properties", required = true)
    private Map<String, Object> properties = new LinkedHashMap<String, Object>();

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " [properties=" + properties + "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
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
        AbstractGeoJsonCrs other = (AbstractGeoJsonCrs) obj;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        return true;
    }

    /**
     * Properties that store information about the coordinate reference system
     * (CRS).
     * 
     * @return the properties that store information about the coordinate
     *         reference system (CRS)
     */
    protected final Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Set the properties the properties that store information about the
     * coordinate reference system (CRS).
     * 
     * @param properties
     *            the properties that store information about the coordinate
     *            reference system (CRS)
     */
    protected final void setProperties(Map<String, Object> properties) {
        if (properties == null) {
            properties = new LinkedHashMap<String, Object>();
        }
        this.properties = properties;
    }

}

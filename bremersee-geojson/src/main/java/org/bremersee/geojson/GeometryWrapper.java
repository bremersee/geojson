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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.vividsolutions.jts.geom.Geometry;
import org.bremersee.geojson.utils.GeometryUtils;

import java.io.IOException;
import java.io.Serializable;

//@formatter:off
/**
 * <p>
 * This class wraps a geometry and can be processed by the Jackson JSON
 * processor without registering the {@link GeoJsonObjectMapperModule}.</br>
 * The geometry is an instance of the JTS Topology Suite (see
 * <a href="http://www.vividsolutions.com/jts/JTSHome.htm">http://www.vividsolutions.com/jts/JTSHome.htm</a>
 * ).
 * </p>
 * 
 * @author Christian Bremer
 */
//@formatter:on
@JsonSerialize(using = GeometryWrapper.Serializer.class)
@JsonDeserialize(using = GeometryWrapper.Deserializer.class)
public class GeometryWrapper implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    /**
     * The geometry object that should be serialized or deserialized.
     */
    @JsonIgnore
    private Geometry geometry;

    /**
     * Default constructor.
     */
    public GeometryWrapper() {
        super();
    }

    /**
     * Constructs a GeoJSON geometry wrapper with the specified geometry.
     * 
     * @param geometry
     *            the geometry
     */
    public GeometryWrapper(Geometry geometry) {
        setGeometry(geometry);
    }

    //@formatter:off
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    //@formatter:on
    @Override
    public String toString() {
        return "GeoJsonGeometry [geometry=" + geometry + "]";
    }

    //@formatter:off
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    //@formatter:on
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((geometry == null) ? 0 : geometry.hashCode());
        return result;
    }

    //@formatter:off
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    //@formatter:on
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GeometryWrapper other = (GeometryWrapper) obj;
        if (geometry == null) {
            if (other.geometry != null)
                return false;
        } else if (!GeometryUtils.equals(geometry, other.geometry))
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public GeometryWrapper clone() throws CloneNotSupportedException { // NOSONAR
        if (geometry == null) {
            return new GeometryWrapper();
        } else {
            return new GeometryWrapper((Geometry) geometry.clone());
        }
    }

    /**
     * Gets the geometry.
     * 
     * @return the geometry
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * Sets the geometry.
     * 
     * @param geometry
     *            the geometry
     */
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * <p>
     * Serializer for the GeoJSON geometry wrapper class.
     * </p>
     * 
     * @author Christian Bremer
     */
    protected static class Serializer extends JsonSerializer<GeometryWrapper> {

        private GeometrySerializer geometrySerializer = new GeometrySerializer();

        //@formatter:off
        /* (non-Javadoc)
         * @see com.fasterxml.jackson.databind.JsonSerializer#serializeWithType(java.lang.Object, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider, com.fasterxml.jackson.databind.jsontype.TypeSerializer)
         */
        //@formatter:on
        @Override
        public void serializeWithType(GeometryWrapper value, JsonGenerator gen, SerializerProvider provider,
                TypeSerializer typeSer) throws IOException {

            serialize(value, gen, provider);
        }

        //@formatter:off
        /* (non-Javadoc)
         * @see com.fasterxml.jackson.databind.JsonSerializer#serialize(java.lang.Object, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
         */
        //@formatter:on
        @Override
        public void serialize(GeometryWrapper value, JsonGenerator jgen, SerializerProvider prov)
                throws IOException {

            if (value == null) {
                jgen.writeNull();
            } else {
                geometrySerializer.serialize(value.geometry, jgen, prov);
            }
        }
    }

    /**
     * <p>
     * Deserializer for the GeoJSON geometry wrapper class.
     * </p>
     * 
     * @author Christian Bremer
     */
    protected static class Deserializer extends JsonDeserializer<GeometryWrapper> {

        private GeometryDeserializer geometryDeserializer = new GeometryDeserializer();

        //@formatter:off
        /* (non-Javadoc)
         * @see com.fasterxml.jackson.databind.JsonDeserializer#deserializeWithType(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext, com.fasterxml.jackson.databind.jsontype.TypeDeserializer)
         */
        //@formatter:on
        @Override
        public GeometryWrapper deserializeWithType(JsonParser jp, DeserializationContext ctxt,
                TypeDeserializer typeDeserializer) throws IOException {

            return deserialize(jp, ctxt);
        }

        //@formatter:off
        /* (non-Javadoc)
         * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
         */
        //@formatter:on
        @Override
        public GeometryWrapper deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException {

            Geometry geometry = geometryDeserializer.deserialize(jp, ctxt);
            return new GeometryWrapper(geometry);
        }
    }

}

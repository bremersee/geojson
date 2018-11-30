/*
 * Copyright 2015-2018 the original author or authors.
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

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bremersee.geojson.utils.GeometryUtils;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * A GeoJSON object with the type {@code FeatureCollection} is a feature collection object (see
 * <a href="https://tools.ietf.org/html/rfc7946#section-3.3">rfc7946 section 3.3</a>).
 *
 * @author Christian Bremer
 */
@ApiModel(
    value = "GeoJsonFeatureCollection",
    description = "A GeoJSON object with the type FeatureCollection is a feature collection object.")
public class GeoJsonFeatureCollection
    extends AbstractGeoJsonFeatureCollection<GeoJsonFeature, Map<String, Object>>
    implements Serializable {

  private static final long serialVersionUID = 2L;

  /**
   * Default constructor.
   */
  public GeoJsonFeatureCollection() {
    super();
  }

  /**
   * Constructs a GeoJSON feature collection with the specified parameters.
   *
   * @param id              an optional id
   * @param features        the GeoJSON features of the collection
   * @param calculateBounds if <code>true</code> the bounding box will be calculated otherwise the
   *                        bounding box will be <code>null</code>
   * @param properties      a map with named objects that are associated with the GeoJSON feature
   *                        collection
   */
  public GeoJsonFeatureCollection(
      final String id,
      final Collection<? extends GeoJsonFeature> features,
      final boolean calculateBounds,
      final Map<String, Object> properties) {

    if (id != null && id.trim().length() > 0) {
      setId(id);
    }
    if (features != null) {
      getFeatures().addAll(features);
      if (calculateBounds) {
        List<Geometry> glist = new ArrayList<>();
        for (GeoJsonFeature f : features) {
          glist.add(f.getGeometry());
        }
        Geometry[] geometries = glist.toArray(new Geometry[0]);
        GeometryCollection gc = new GeometryCollection(geometries, new GeometryFactory());
        setBbox(GeometryUtils.getBoundingBox(gc));
      }
    }
    if (properties != null) {
      getProperties().putAll(properties);
    }
  }

  @Override
  public Map<String, Object> getProperties() {
    if (super.getProperties() == null) {
      super.setProperties(new LinkedHashMap<>());
    }
    return super.getProperties();
  }

}

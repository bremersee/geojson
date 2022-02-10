/*
 * Copyright 2015-2020 the original author or authors.
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
import java.util.Collection;
import java.util.List;
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
public class GeoJsonFeatureCollection
    extends AbstractGeoJsonFeatureCollection<GeoJsonFeature>
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
   * @param features the GeoJSON features of the collection
   * @param calculateBounds if <code>true</code> the bounding box will be calculated otherwise
   *     the bounding box will be <code>null</code>
   */
  public GeoJsonFeatureCollection(
      final Collection<? extends GeoJsonFeature> features,
      final boolean calculateBounds) {

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
  }

}

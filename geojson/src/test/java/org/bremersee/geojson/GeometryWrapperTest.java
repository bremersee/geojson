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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.bremersee.geojson.utils.GeometryUtils;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;

/**
 * The geometry wrapper test.
 *
 * @author Christian Bremer
 */
class GeometryWrapperTest {

  /**
   * Gets geometry.
   */
  @Test
  void getGeometry() {
    GeometryWrapper wrapper = new GeometryWrapper();

    assertEquals(wrapper, wrapper);
    assertEquals(wrapper, new GeometryWrapper());
    assertEquals(wrapper.hashCode(), new GeometryWrapper().hashCode());
    assertEquals(wrapper.toString(), new GeometryWrapper().toString());
    assertNotEquals(wrapper, null);
    assertNotEquals(wrapper, new Object());

    Geometry geometry = GeometryUtils.createPoint(1., 2.);

    assertNotEquals(wrapper, new GeometryWrapper(geometry));

    wrapper.setGeometry(geometry);
    assertEquals(geometry, wrapper.getGeometry());
    assertEquals(wrapper, new GeometryWrapper(geometry));
    assertEquals(wrapper.hashCode(), new GeometryWrapper(geometry).hashCode());
    assertEquals(wrapper.toString(), new GeometryWrapper(geometry).toString());
  }

  /**
   * Test clone.
   */
  @Test
  void testClone() {
    GeometryWrapper wrapper = new GeometryWrapper();
    GeometryWrapper clone = wrapper.clone();
    assertEquals(wrapper, clone);

    wrapper = new GeometryWrapper(GeometryUtils.createPoint(2., 3.));
    clone = wrapper.clone();
    assertEquals(wrapper, clone);
  }
}
/*
 * Copyright 2020 the original author or authors.
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

package org.bremersee.geojson.converter.deserialization;

import java.io.Serializable;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.util.Assert;

/**
 * The object to coordinate converter.
 *
 * @author Christian Bremer
 */
public class ObjectToCoordinateConverter implements Serializable {

  private static final long serialVersionUID = 1L;

  private final ObjectToDoubleConverter doubleConverter = new ObjectToDoubleConverter();

  /**
   * Convert coordinate.
   *
   * @param source the source (must not be {@code null})
   * @return the coordinate
   */
  Coordinate convert(Object source) {
    Assert.notNull(source, "Source of coordinate must be present.");
    //noinspection unchecked
    List<Object> list = (List<Object>) source;
    Coordinate coordinate;
    if (list.size() >= 3) {
      coordinate = new Coordinate(
          doubleConverter.convert(list.get(0)),
          doubleConverter.convert(list.get(1)),
          doubleConverter.convert(list.get(2)));
    } else if (list.size() == 2) {
      coordinate = new Coordinate(
          doubleConverter.convert(list.get(0)),
          doubleConverter.convert(list.get(1)));
    } else if (list.size() == 1) {
      coordinate = new Coordinate(
          doubleConverter.convert(list.get(0)),
          Double.NaN);
    } else {
      coordinate = new Coordinate(Double.NaN, Double.NaN);
    }
    return coordinate;
  }

}

/*
 * Copyright 2022 the original author or authors.
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

import static java.util.Objects.isNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The object to double converter.
 *
 * @author Christian Bremer
 */
class ObjectToDoubleConverter implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Convert double.
   *
   * @param source the source
   * @return the double
   */
  double convert(Object source) {
    if (source instanceof Number) {
      return ((Number) source).doubleValue();
    }
    return isNull(source)
        ? Double.NaN
        : new BigDecimal(String.valueOf(source)).doubleValue();
  }
}

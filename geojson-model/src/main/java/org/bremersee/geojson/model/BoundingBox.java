/*
 * Copyright 2018-2022 the original author or authors.
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

package org.bremersee.geojson.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * The bounding box of a geometry, feature or feature collection.
 *
 * @author Christian Bremer
 */
@Schema(description = "The bounding box of a geometry, feature or feature collection.")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class BoundingBox extends ArrayList<BigDecimal> implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new bounding box.
   *
   * @param coordinates the coordinates
   */
  public BoundingBox(Collection<? extends BigDecimal> coordinates) {
    super(coordinates);
  }

  @Schema(hidden = true)
  @Override
  public boolean isEmpty() {
    return super.isEmpty();
  }

  /**
   * The bounding box as double array.
   *
   * @return the bounding box as double array
   */
  public double[] toDoubleArray() {
    if (size() == 4 || size() == 6) {
      double[] bbox = new double[size()];
      int i = 0;
      for (BigDecimal value : this) {
        bbox[i] = value.doubleValue();
        i++;
      }
      return bbox;
    }
    return null;
  }

}


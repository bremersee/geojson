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

import static java.util.Objects.isNull;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The first two elements are longitude and latitude.
 *
 * @author Christian Bremer
 */
@Schema(description = "The first two elements are longitude and latitude.")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Position extends ArrayList<BigDecimal> implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new empty (illegal) position.
   */
  public Position() {
    super(3);
  }

  /**
   * Instantiates a new position.
   *
   * @param x the x (aka longitude)
   * @param y the y (aka latitude)
   */
  public Position(BigDecimal x, BigDecimal y) {
    this(x, y, null);
  }

  /**
   * Instantiates a new position.
   *
   * @param x the x (aka longitude)
   * @param y the y (aka latitude)
   * @param z the z (the z coordinate)
   */
  public Position(BigDecimal x, BigDecimal y, BigDecimal z) {
    super(isNull(z) ? 2 : 3);
    if (isNull(x)) {
      throw new IllegalArgumentException("X (longitude) must not be null.");
    }
    if (isNull(y)) {
      throw new IllegalArgumentException("Y (latitude) must not be null.");
    }
    add(x);
    add(y);
    if (!isNull(z)) {
      add(z);
    }
  }

}


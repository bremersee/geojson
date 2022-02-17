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

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.math.BigDecimal;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The position test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class PositionTest {

  /**
   * Test model.
   *
   * @param softly the soft assertions
   */
  @Test
  void testModel(SoftAssertions softly) {
    Position model = new Position();
    softly.assertThat(model).isNotEqualTo(null);
    softly.assertThat(model).isNotEqualTo(new Object());
    softly.assertThat(model).isEqualTo(model);
    softly.assertThat(model).isEqualTo(new Position());
    softly.assertThat(model.toString()).isNotEmpty();

    BigDecimal x = new BigDecimal("123.4");
    BigDecimal y = new BigDecimal("567.8");
    model = new Position(x, y);
    softly.assertThat(model).isEqualTo(new Position(x, y));
    softly.assertThat(model.toString()).contains(x.toString());
  }

  /**
   * Test constructor with illegal y.
   */
  @Test
  void testConstructorWithIllegalY() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new Position(BigDecimal.ONE, null));
  }

  /**
   * Test constructor with illegal x.
   */
  @Test
  void testConstructorWithIllegalX() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new Position(null, BigDecimal.ONE));
  }

  /**
   * Test constructor with xyz.
   */
  @Test
  void testConstructorWithXyz() {
    assertThatNoException()
        .isThrownBy(() -> new Position(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.TEN));
  }

}
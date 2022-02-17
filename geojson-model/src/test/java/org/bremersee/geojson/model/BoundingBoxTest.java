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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The bounding box test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class BoundingBoxTest {

  /**
   * Test model.
   *
   * @param softly the soft assertions
   */
  @Test
  void testModel(SoftAssertions softly) {
    BoundingBox model = new BoundingBox();
    softly.assertThat(model).isNotEqualTo(null);
    softly.assertThat(model).isNotEqualTo(new Object());
    softly.assertThat(model).isEqualTo(model);
    softly.assertThat(model).isEqualTo(new BoundingBox());
    softly.assertThat(model.toString()).hasSizeGreaterThan(0);

    BigDecimal value = new BigDecimal("123.4");
    model = new BoundingBox(Collections.singleton(value));
    softly.assertThat(model).isNotEqualTo(null);
    softly.assertThat(model).isNotEqualTo(new Object());
    softly.assertThat(model).isEqualTo(model);
    softly.assertThat(model).isEqualTo(new BoundingBox(Collections.singleton(value)));
    softly.assertThat(model.toString()).contains(value.toString());

    softly.assertThat(model.toDoubleArray()).isNull();

    double[] values = new double[]{0., 0., 1., 1.};
    model = new BoundingBox(Arrays.stream(values)
        .mapToObj(BigDecimal::valueOf)
        .collect(Collectors.toList()));
    softly.assertThat(model.toDoubleArray()).containsExactly(values);
  }

}
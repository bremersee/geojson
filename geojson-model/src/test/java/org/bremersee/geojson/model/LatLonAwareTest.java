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
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The lat lon aware test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class LatLonAwareTest {

  /**
   * Builder.
   *
   * @param softly the softly
   */
  @Test
  void builder(SoftAssertions softly) {
    LatLonAware model = LatLonAware.builder().build();
    softly.assertThat(model.hasValues())
        .isFalse();
    softly.assertThat(model.toLatLonString())
        .isEqualTo("");
    softly.assertThat(model.toLonLatString())
        .isEqualTo("");

    model = LatLonAware.builder()
        .longitude(1.)
        .build();
    softly.assertThat(model.hasValues())
        .isFalse();

    model = LatLonAware.builder()
        .latitude(1.)
        .build();
    softly.assertThat(model.hasValues())
        .isFalse();

    model = LatLonAware.builder()
        .from(null)
        .build();
    softly.assertThat(model.hasValues())
        .isFalse();

    model = LatLonAware.builder()
        .latitude(1.2345)
        .longitude(4.5678)
        .build();
    softly.assertThat(model.hasValues())
        .isTrue();
    softly.assertThat(model.getLatitude())
        .isEqualTo(new BigDecimal("1.2345"));
    softly.assertThat(model.getLongitude())
        .isEqualTo(new BigDecimal("4.5678"));
    softly.assertThat(model.toLatLonString())
        .isEqualTo("1.2345,4.5678");
    softly.assertThat(model.toLonLatString())
        .isEqualTo("4.5678,1.2345");

    model = LatLonAware.builder()
        .latitude(new BigDecimal("1.2345"))
        .longitude(new BigDecimal("4.5678"))
        .build();
    softly.assertThat(model.hasValues())
        .isTrue();
    softly.assertThat(model.getLatitude())
        .isEqualTo(new BigDecimal("1.2345"));
    softly.assertThat(model.getLongitude())
        .isEqualTo(new BigDecimal("4.5678"));
    softly.assertThat(model.toLatLonString())
        .isEqualTo("1.2345,4.5678");
    softly.assertThat(model.toLonLatString())
        .isEqualTo("4.5678,1.2345");

    model = LatLonAware.builder()
        .from(model)
        .build();
    softly.assertThat(model.hasValues())
        .isTrue();
    softly.assertThat(model.getLatitude())
        .isEqualTo(new BigDecimal("1.2345"));
    softly.assertThat(model.getLongitude())
        .isEqualTo(new BigDecimal("4.5678"));
    softly.assertThat(model.toLatLonString())
        .isEqualTo("1.2345,4.5678");
    softly.assertThat(model.toLonLatString())
        .isEqualTo("4.5678,1.2345");
  }

}
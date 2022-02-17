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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test unknown aware class.
 *
 * @author Christian Bremer
 */
@ExtendWith({SoftAssertionsExtension.class})
class UnknownAwareTest {

  /**
   * Test model.
   *
   * @param softly the soft assertions
   */
  @Test
  void testModel(SoftAssertions softly) {
    ConcreteUnknown model = new ConcreteUnknown();
    softly.assertThat(model.unknown()).isNull();
    softly.assertThat(model.hasUnknown()).isFalse();
    softly.assertThat(model.findUnknown("something", Object.class)).isEmpty();
    softly.assertThat(model.findUnknownList("something", Object.class)).isEmpty();
    softly.assertThat(model.findUnknownMap("something")).isEmpty();

    softly.assertThat(model.toString()).isNotEmpty();

    model.unknown(null);
    softly.assertThat(model.unknown()).isNull();
    softly.assertThat(model.hasUnknown()).isFalse();

    model.unknown(Collections.emptyMap());
    softly.assertThat(model.unknown()).isNull();
    softly.assertThat(model.hasUnknown()).isFalse();

    softly.assertThat(model).isNotEqualTo(null);
    softly.assertThat(model).isNotEqualTo(new Object());
    softly.assertThat(model).isEqualTo(model);
    softly.assertThat(model).isEqualTo(new ConcreteUnknown());
    softly.assertThat(model.hashCode()).isEqualTo(new ConcreteUnknown().hashCode());

    model.unknown(Collections.singletonMap("something", "value"));
    softly.assertThat(model.unknown()).isNotNull();
    softly.assertThat(model.hasUnknown()).isTrue();

    model = new ConcreteUnknown();
    model.unknown(null, "value");
    softly.assertThat(model.unknown()).isNull();
    softly.assertThat(model.hasUnknown()).isFalse();

    model.unknown("", "value");
    softly.assertThat(model.unknown()).isNull();
    softly.assertThat(model.hasUnknown()).isFalse();

    model.unknown("something", "value");
    softly.assertThat(model).isEqualTo(new ConcreteUnknown(Map.of("something", "value")));
    softly.assertThat(model.toString()).contains("something");
  }

  /**
   * Find from root.
   */
  @Test
  void findFromRoot() {
    ConcreteUnknown unknown = new ConcreteUnknown();
    unknown.unknown("test", "expected");
    Optional<String> actual = unknown.findUnknown("$.test", String.class);
    assertThat(actual)
        .hasValue("expected");
  }

  /**
   * Find not from root.
   */
  @Test
  void findNotFromRoot() {
    ConcreteUnknown unknown = new ConcreteUnknown();
    unknown.unknown("test", "expected");
    Optional<String> actual = unknown.findUnknown("$.foo", String.class);
    assertThat(actual)
        .isEmpty();
  }

  /**
   * Find not from root class cast exception.
   */
  @Test
  void findNotFromRootClassCastException() {
    ConcreteUnknown unknown = new ConcreteUnknown();
    unknown.unknown("test", "expected");
    Optional<Integer> actual = unknown.findUnknown("$.test", Integer.class);
    assertThat(actual)
        .isEmpty();
  }

  /**
   * Find map from root.
   *
   * @param softly the soft assertions
   */
  @Test
  void findMapFromRoot(SoftAssertions softly) {
    Map<String, Object> expected = new LinkedHashMap<>();
    expected.put("sub", "expected");

    ConcreteUnknown unknown = new ConcreteUnknown();
    unknown.unknown("test", expected);

    Optional<Map<String, Object>> actualMap = unknown.findUnknownMap("$.test");
    softly.assertThat(actualMap)
        .hasValue(expected);

    Optional<String> actual = unknown.findUnknown("$.test.sub", String.class);
    softly.assertThat(actual)
        .hasValue("expected");

    softly.assertThat(unknown.findUnknown("$.test.foo", Map.class)).isEmpty();
    softly.assertThat(unknown.findUnknown("$.test.sub.foo", Map.class)).isEmpty();
    softly.assertThat(unknown.findUnknown("$.test", String.class)).isEmpty();
  }

  /**
   * Find list from root.
   */
  @Test
  void findListFromRoot() {
    List<String> expected = List.of("one", "two");

    ConcreteUnknown unknown = new ConcreteUnknown();
    unknown.unknown("test", expected);

    Optional<List<String>> actualList = unknown.findUnknownList("$.test", String.class);
    assertThat(actualList)
        .hasValue(expected);
  }

  /**
   * Find bad list from root.
   */
  @Test
  void findBadListFromRoot() {
    assertThatExceptionOfType(ClassCastException.class)
        .isThrownBy(() -> {
          List<String> expected = List.of("one", "two");

          ConcreteUnknown unknown = new ConcreteUnknown();
          unknown.unknown("test", expected);

          unknown.findUnknownList("$.test", Integer.class)
              .stream()
              .flatMap(Collection::stream)
              .forEach(System.out::println);
        });
  }

  private static class ConcreteUnknown extends UnknownAware {

    /**
     * Instantiates a new Concrete unknown.
     */
    public ConcreteUnknown() {
    }

    /**
     * Instantiates a new Concrete unknown.
     *
     * @param unknown the unknown
     */
    public ConcreteUnknown(Map<String, Object> unknown) {
      super(unknown);
    }
  }

}

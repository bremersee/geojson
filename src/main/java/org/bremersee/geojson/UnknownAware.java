/*
 * Copyright 2018 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;

/**
 * This base class allows to keep unknown json properties.
 *
 * @author Christian Bremer
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class UnknownAware {

  @JsonIgnore
  private Map<String, Object> unknown;

  /**
   * Gets the unknown json properties (can be {@code null}).
   *
   * @return the unknown
   */
  @JsonAnyGetter
  public Map<String, Object> unknown() {
    return unknown;
  }

  /**
   * Sets the unknown json properties.
   *
   * @param unknown the unknown json properties
   */
  public void unknown(Map<String, Object> unknown) {
    if (unknown != null && !unknown.isEmpty()) {
      this.unknown = unknown;
    }
  }

  /**
   * Any json setter.
   *
   * @param name the name
   * @param value the value
   */
  @JsonAnySetter
  public void unknown(String name, Object value) {
    if (name == null || name.trim().length() == 0) {
      return;
    }
    if (unknown == null) {
      unknown = new LinkedHashMap<>();
    }
    unknown.put(name, value);
  }

  /**
   * Returns {@code true} if there are unknown properties, otherwise {@code false}.
   *
   * @return {@code true} if there are unknown properties, otherwise {@code false}
   */
  public boolean hasUnknown() {
    return unknown != null && !unknown.isEmpty();
  }

  /**
   * Find a value from the unknown map.
   *
   * @param jsonPath the json path, e. g. {@code $.firstKey.secondKey.thirdKey}
   * @param clazz the expected result class
   * @param <T> the class type
   * @return an empty optional if the value was not found or can not be casted, otherwise the value
   */
  public <T> Optional<T> findUnknown(final String jsonPath, final Class<T> clazz) {
    if (!hasUnknown() || !isJsonPath(jsonPath) || clazz == null) {
      return Optional.empty();
    }
    Object value = null;
    Map<String, Object> tmpUnknown = unknown;
    final StringTokenizer tokenizer = new StringTokenizer(jsonPath.substring(2), ".");
    while (tokenizer.hasMoreTokens()) {
      final String token = tokenizer.nextToken();
      value = tmpUnknown.get(token);
      if (value == null) {
        break;
      }
      if ((value instanceof Map) && tokenizer.hasMoreTokens()) {
        try {
          //noinspection unchecked
          tmpUnknown = (Map) value;
        } catch (Exception e) {
          return Optional.empty();
        }
      }
    }
    if (value == null) {
      return Optional.empty();
    }
    try {
      return Optional.of(clazz.cast(value));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private boolean isJsonPath(final String jsonPath) {
    return jsonPath != null && jsonPath.startsWith("$.") && jsonPath.length() > 2;
  }

}

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

package org.bremersee.geojson.converter.serialization;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.nonNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.locationtech.jts.geom.Coordinate;

/**
 * The type Coordinate to list converter.
 *
 * @author Christian Bremer
 */
class CoordinateToListConverter implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private static final int MAXIMUM_INTEGER_DIGITS = 17;

  private static final int MAXIMUM_FRACTION_DIGITS = 9;

  private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);

  private static final NumberFormat TEST_NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);

  static {
    NUMBER_FORMAT.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
    NUMBER_FORMAT.setMaximumIntegerDigits(MAXIMUM_INTEGER_DIGITS);
    NUMBER_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
    NUMBER_FORMAT.setGroupingUsed(false);

    TEST_NUMBER_FORMAT.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS + 1);
    TEST_NUMBER_FORMAT.setMaximumIntegerDigits(MAXIMUM_INTEGER_DIGITS + 1);
    TEST_NUMBER_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
    TEST_NUMBER_FORMAT.setGroupingUsed(false);
  }

  private final boolean useBigDecimal;

  /**
   * Instantiates a new Coordinate to list converter.
   *
   * @param useBigDecimal the use big decimal
   */
  CoordinateToListConverter(boolean useBigDecimal) {
    this.useBigDecimal = useBigDecimal;
  }

  /**
   * Convert list.
   *
   * @param source the source
   * @return the list
   */
  List<Number> convert(Coordinate source) {
    int size = nonNull(source) ? 2 : 0;
    List<Number> list = new ArrayList<>(size);
    if (size > 0) {
      Number x = round(source.getX());
      Number y = round(source.getY());
      if (nonNull(x) && nonNull(y)) {
        list.add(x);
        list.add(y);
      }
    }
    return unmodifiableList(list);
  }

  private Number round(double value) {
    if (Double.isNaN(value)) {
      return null;
    }
    if (formatValue(value)) {
      String strValue = NUMBER_FORMAT.format(value);
      return useBigDecimal ? new BigDecimal(strValue) : new BigDecimal(strValue).doubleValue();
    }
    return useBigDecimal ? BigDecimal.valueOf(value) : value;
  }

  private boolean formatValue(final double value) {
    String[] testValues = TEST_NUMBER_FORMAT.format(value).split(Pattern.quote("."));
    return testValues[0].length() > MAXIMUM_INTEGER_DIGITS
        || (testValues.length > 1 && testValues[1].length() > MAXIMUM_FRACTION_DIGITS);
  }

}

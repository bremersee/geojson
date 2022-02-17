package org.bremersee.geojson.converter.deserialization;

import java.math.BigDecimal;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class ObjectToDoubleConverterTest {

  @Test
  void convert(SoftAssertions softly) {
    ObjectToDoubleConverter target = new ObjectToDoubleConverter();
    softly.assertThat(target.convert(1.234))
        .isEqualTo(1.234);
    softly.assertThat(target.convert(new BigDecimal("1.234")))
        .isEqualTo(1.234);
    softly.assertThat(target.convert("1.234"))
        .isEqualTo(1.234);
  }
}
package org.bremersee.geojson.converter.deserialization;

import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;

@ExtendWith({SoftAssertionsExtension.class})
class ObjectToCoordinateConverterTest {

  @Test
  void convert(SoftAssertions softly) {
    ObjectToCoordinateConverter target = new ObjectToCoordinateConverter();
    softly.assertThat(target.convert(List.of(1., 2., 3.)))
        .isEqualTo(new Coordinate(1., 2., 3.));

    softly.assertThat(target.convert(List.of(1., 2.)))
        .isEqualTo(new Coordinate(1., 2.));

    softly.assertThat(target.convert(List.of(1.)))
        .extracting(Coordinate::getX)
        .isEqualTo(1.);
    softly.assertThat(target.convert(List.of(1.)))
        .extracting(Coordinate::getY)
        .isEqualTo(Double.NaN);

    softly.assertThat(target.convert(List.of()))
        .extracting(Coordinate::getX)
        .isEqualTo(Double.NaN);
    softly.assertThat(target.convert(List.of()))
        .extracting(Coordinate::getY)
        .isEqualTo(Double.NaN);
  }
}
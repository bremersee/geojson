package org.bremersee.geojson.converter.deserialization;

import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

@ExtendWith(SoftAssertionsExtension.class)
class JsonToPointConverterTest {

  @Test
  void convertCoordinates(SoftAssertions softly) {
    GeometryFactory geometryFactory = new GeometryFactory();
    JsonToPointConverter target = new JsonToPointConverter(
        geometryFactory,
        new ObjectToCoordinateConverter());

    Point actual = target.convertCoordinates(List.of(1., 2.));
    //noinspection unchecked
    softly.assertThat(actual)
        .isEqualTo(geometryFactory.createPoint(new Coordinate(1., 2.)));
    actual = target.convertCoordinates(List.of());
    //noinspection unchecked
    softly.assertThat(actual)
        .isEqualTo(geometryFactory.createPoint((Coordinate) null));
  }
}
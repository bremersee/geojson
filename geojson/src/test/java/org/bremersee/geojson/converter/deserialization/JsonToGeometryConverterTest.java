package org.bremersee.geojson.converter.deserialization;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.bremersee.geojson.GeoJsonConstants.GEOMETRY_COLLECTION;
import static org.bremersee.geojson.GeoJsonConstants.TYPE;

import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Geometry;

@ExtendWith(SoftAssertionsExtension.class)
class JsonToGeometryConverterTest {

  @Test
  void convertAndExpectEmptyGeometry(SoftAssertions softly) {
    JsonToGeometryConverter target = new JsonToGeometryConverter();
    Geometry actual = target.convert(Map.of(TYPE, GEOMETRY_COLLECTION));
    //noinspection unchecked
    softly.assertThat(actual)
        .isNotNull();
    softly.assertThat(actual.getCoordinates())
        .isEmpty();
  }

  @Test
  void convertAndExpectIllegalArgumentException() {
    JsonToGeometryConverter target = new JsonToGeometryConverter();
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> target.convert(Map.of("IllegalGeometry", new Object())));
  }
}
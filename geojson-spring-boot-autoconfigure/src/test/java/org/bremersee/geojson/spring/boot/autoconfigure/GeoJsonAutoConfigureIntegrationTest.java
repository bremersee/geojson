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

package org.bremersee.geojson.spring.boot.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bremersee.geojson.GeoJsonFeature;
import org.bremersee.geojson.GeoJsonFeatureCollection;
import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.bremersee.geojson.spring.boot.autoconfigure.app.GeometryEntity;
import org.bremersee.geojson.spring.boot.autoconfigure.app.GeometryEntityRepository;
import org.bremersee.geojson.spring.boot.autoconfigure.app.TestConfiguration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;

/**
 * The geo json autoconfigure integration test.
 *
 * @author Christian Bremer
 */
@Testcontainers
@SpringBootTest(
    classes = {TestConfiguration.class},
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.main.web-application-type=reactive",
        "spring.data.mongodb.auto-index-creation=true"
    })
@ComponentScan(basePackageClasses = {GeometryEntityRepository.class})
@EnableMongoRepositories(basePackageClasses = {GeometryEntityRepository.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GeoJsonAutoConfigureIntegrationTest {

  @Container
  @ServiceConnection
  static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName
      .parse("mongo:4.0.10"));

  /**
   * The Geometry factory.
   */
  @Autowired
  GeoJsonGeometryFactory geometryFactory;

  /**
   * The Repository.
   */
  @Autowired
  GeometryEntityRepository repository;

  /**
   * The test web client.
   */
  @Autowired
  WebTestClient webClient;

  /**
   * Transform.
   */
  @Order(10)
  @Test
  void transform() {
    MultiPoint geometry = geometryFactory.createMultiPoint(List.of(
        geometryFactory.createPoint(1., 2.),
        geometryFactory.createPoint(6., 7.)
    ));
    GeoJsonFeature<Geometry, Object> expected = new GeoJsonFeature<>("987", geometry, true, null);
    webClient
        .get()
        .uri("/geo/transform?id={id}&geometry={geometry}&withBoundingBox={withBoundingBox}",
            expected.getId(), expected.getGeometry().toText(), true)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(new ParameterizedTypeReference<GeoJsonFeature<Geometry, Object>>() {
        })
        .value(response -> assertThat(response).isEqualTo(expected));
  }

  /**
   * Find features.
   */
  @Order(20)
  @Test
  void findFeatures() {
    LinearRing ring = geometryFactory.createLinearRing(List.of(
        new Coordinate(2., 3.),
        new Coordinate(6., 4.),
        new Coordinate(6., 8.),
        new Coordinate(2., 3.)));
    Polygon polygon = geometryFactory.createPolygon(ring);
    LineString lineString = geometryFactory.createLineString(Arrays.asList(
        new Coordinate(2., 3.),
        new Coordinate(6., 7.)));
    Point point = geometryFactory.createPoint(7., 8.);
    List<Geometry> geometries = List.of(point, lineString, polygon);

    GeoJsonFeatureCollection<Geometry, Object> expected = save(geometries).block();

    webClient
        .get()
        .uri("/geo")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(new ParameterizedTypeReference<GeoJsonFeatureCollection<Geometry, Object>>() {
        })
        .value(response -> assertThat(response).isEqualTo(expected));
  }

  /**
   * Save feature.
   */
  @Order(30)
  @Test
  void saveFeature() {
    LineString lineString = geometryFactory.createLineString(Arrays.asList(
        new Coordinate(2., 3.),
        new Coordinate(6., 7.)));
    //noinspection unchecked
    webClient.post()
        .uri("/geo")
        .body(Mono.just(lineString), LineString.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(new ParameterizedTypeReference<GeoJsonFeature<Geometry, Object>>() {
        })
        .value(response -> assertThat(response.getGeometry()).isEqualTo(lineString));

  }

  private Mono<GeoJsonFeatureCollection<Geometry, Object>> save(List<Geometry> geometries) {
    return repository.saveAll(geometries.stream()
            .map(GeometryEntity::new)
            .collect(Collectors.toList()))
        .map(entity -> new GeoJsonFeature<>(entity.getId(), entity.getGeometry(), false, null))
        .collect(
            () -> new GeoJsonFeatureCollection<>(
                true,
                (o1, o2) -> o1.getId().compareToIgnoreCase(o2.getId())),
            GeoJsonFeatureCollection::add);

  }

}

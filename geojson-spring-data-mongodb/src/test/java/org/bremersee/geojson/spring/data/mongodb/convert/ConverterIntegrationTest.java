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

package org.bremersee.geojson.spring.data.mongodb.convert;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.bremersee.geojson.spring.data.mongodb.convert.app.GeometryCollectionEntity;
import org.bremersee.geojson.spring.data.mongodb.convert.app.GeometryCollectionEntityRepository;
import org.bremersee.geojson.spring.data.mongodb.convert.app.GeometryEntity;
import org.bremersee.geojson.spring.data.mongodb.convert.app.GeometryEntityRepository;
import org.bremersee.geojson.spring.data.mongodb.convert.app.TestConfiguration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * The converter test with embedded mongo.
 *
 * @author Christian Bremer
 */
@Testcontainers
@SpringBootTest(
    classes = {TestConfiguration.class},
    webEnvironment = WebEnvironment.NONE)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName.class)
@ExtendWith(SoftAssertionsExtension.class)
@Slf4j
public class ConverterIntegrationTest {

  private static final GeoJsonGeometryFactory factory = new GeoJsonGeometryFactory();

  @Container
  @ServiceConnection
  static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName
      .parse("mongo:4.0.10"));

  /**
   * The repository.
   */
  @Autowired
  GeometryEntityRepository repository;

  /**
   * The collection repository.
   */
  @Autowired
  GeometryCollectionEntityRepository colRepository;

  /**
   * The mongo template.
   */
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  MongoTemplate mongoTemplate;

  /**
   * The mongo mapping context.
   */
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  MongoMappingContext mongoMappingContext;

  /**
   * Create index.
   */
  @Test
  void createIndex() {
    IndexOperations indexOps = mongoTemplate.indexOps(GeometryEntity.class);
    IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
    resolver.resolveIndexFor(GeometryEntity.class).forEach(indexOps::ensureIndex);
  }

  /**
   * Test geometry collection.
   *
   * @param softly the softly
   */
  @Test
  void testGeometryCollection(SoftAssertions softly) {
    Point model0 = factory.createPoint(7., 8.);
    LineString model1 = factory.createLineString(Arrays.asList(
        new Coordinate(2., 3.),
        new Coordinate(6., 7.)));
    GeometryCollection geometry = factory.createGeometryCollection(List.of(model0, model1));
    GeometryCollectionEntity entity = colRepository.save(new GeometryCollectionEntity(geometry));
    log.info("Saved: {}", entity);
    softly.assertThat(entity)
        .isNotNull()
        .extracting(GeometryCollectionEntity::getId)
        .isNotNull();

    Optional<GeometryCollectionEntity> actual = colRepository.findById(entity.getId());
    softly.assertThat(actual)
        .hasValue(entity);
  }

  /**
   * Test line string.
   *
   * @param softly the softly
   */
  @Test
  void testLineString(SoftAssertions softly) {
    LineString geometry = factory.createLineString(Arrays.asList(
        new Coordinate(2., 3.),
        new Coordinate(6., 7.)));
    GeometryEntity entity = repository.save(new GeometryEntity(geometry));
    log.info("Saved: {}", entity);
    softly.assertThat(entity)
        .isNotNull()
        .extracting(GeometryEntity::getId)
        .isNotNull();

    Optional<GeometryEntity> actual = repository.findById(entity.getId());
    softly.assertThat(actual)
        .hasValue(entity);
  }

  /**
   * Test multi line string.
   *
   * @param softly the softly
   */
  @Test
  void testMultiLineString(SoftAssertions softly) {
    LineString model0 = factory.createLineString(Arrays.asList(
        new Coordinate(2., 3.),
        new Coordinate(6., 7.)));
    LineString model1 = factory.createLineString(Arrays.asList(
        new Coordinate(12., 13.),
        new Coordinate(16., 17.)));
    MultiLineString geometry = factory.createMultiLineString(Arrays.asList(
        model0,
        model1));
    GeometryEntity entity = repository.save(new GeometryEntity(geometry));
    log.info("Saved: {}", entity);
    softly.assertThat(entity)
        .isNotNull()
        .extracting(GeometryEntity::getId)
        .isNotNull();

    Optional<GeometryEntity> actual = repository.findById(entity.getId());
    softly.assertThat(actual)
        .hasValue(entity);
  }

  /**
   * Test multi point.
   *
   * @param softly the softly
   */
  @Test
  void testMultiPoint(SoftAssertions softly) {
    Point model0 = factory.createPoint(7., 8.);
    Point model1 = factory.createPoint(17., 18.);
    MultiPoint geometry = factory.createMultiPoint(Arrays.asList(model0, model1));
    GeometryEntity entity = repository.save(new GeometryEntity(geometry));
    log.info("Saved: {}", entity);
    softly.assertThat(entity)
        .isNotNull()
        .extracting(GeometryEntity::getId)
        .isNotNull();

    Optional<GeometryEntity> actual = repository.findById(entity.getId());
    softly.assertThat(actual)
        .hasValue(entity);
  }

  /**
   * Test multi polygon.
   *
   * @param softly the softly
   */
  @Test
  void testMultiPolygon(SoftAssertions softly) {
    LinearRing ring0 = factory.createLinearRing(Arrays.asList(
        new Coordinate(2., 3.),
        new Coordinate(6., 4.),
        new Coordinate(6., 8.),
        new Coordinate(2., 3.)));
    Polygon model0 = factory.createPolygon(ring0);
    LinearRing ring1 = factory.createLinearRing(Arrays.asList(
        new Coordinate(12., 13.),
        new Coordinate(16., 14.),
        new Coordinate(16., 18.),
        new Coordinate(12., 13.)));
    Polygon model1 = factory.createPolygon(ring1);
    MultiPolygon geometry = factory.createMultiPolygon(Arrays.asList(model0, model1));
    GeometryEntity entity = repository.save(new GeometryEntity(geometry));
    log.info("Saved: {}", entity);
    softly.assertThat(entity)
        .isNotNull()
        .extracting(GeometryEntity::getId)
        .isNotNull();

    Optional<GeometryEntity> actual = repository.findById(entity.getId());
    softly.assertThat(actual)
        .hasValue(entity);
  }

  /**
   * Test point.
   *
   * @param softly the softly
   */
  @Test
  void testPoint(SoftAssertions softly) {
    Point geometry = factory.createPoint(2., 4.);
    GeometryEntity entity = repository.save(new GeometryEntity(geometry));
    log.info("Saved: {}", entity);
    softly.assertThat(entity)
        .isNotNull()
        .extracting(GeometryEntity::getId)
        .isNotNull();

    Optional<GeometryEntity> actual = repository.findById(entity.getId());
    softly.assertThat(actual)
        .hasValue(entity);
  }

  /**
   * Test polygon.
   *
   * @param softly the softly
   */
  @Test
  void testPolygon(SoftAssertions softly) {
    LinearRing ring = factory.createLinearRing(Arrays.asList(
        new Coordinate(2., 3.),
        new Coordinate(6., 4.),
        new Coordinate(6., 8.),
        new Coordinate(2., 3.)));
    Polygon geometry = factory.createPolygon(ring);
    GeometryEntity entity = repository.save(new GeometryEntity(geometry));
    log.info("Saved: {}", entity);
    softly.assertThat(entity)
        .isNotNull()
        .extracting(GeometryEntity::getId)
        .isNotNull();

    Optional<GeometryEntity> actual = repository.findById(entity.getId());
    softly.assertThat(actual)
        .hasValue(entity);
  }

}

/*
 * Copyright 2020 the original author or authors.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.bremersee.geojson.spring.data.mongodb.convert.app.GeometryCollectionEntity;
import org.bremersee.geojson.spring.data.mongodb.convert.app.GeometryCollectionEntityRepository;
import org.bremersee.geojson.spring.data.mongodb.convert.app.GeometryEntity;
import org.bremersee.geojson.spring.data.mongodb.convert.app.GeometryEntityRepository;
import org.bremersee.geojson.spring.data.mongodb.convert.app.TestConfiguration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * The converter test with embedded mongo.
 *
 * @author Christian Bremer
 */
@SpringBootTest(
    classes = {TestConfiguration.class},
    webEnvironment = WebEnvironment.NONE,
    properties = {
        "security.basic.enabled=false",
        "spring.data.mongodb.uri=mongodb://localhost:27017/test",
        "spring.data.mongodb.auto-index-creation=false",
        "spring.mongodb.embedded.version=3.6.2"
    })
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ConverterTest {

  private static final Logger log = LoggerFactory.getLogger(ConverterTest.class);

  private static final GeoJsonGeometryFactory factory = new GeoJsonGeometryFactory();

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
   */
  @Test
  void testGeometryCollection() {
    Point model0 = factory.createPoint(7., 8.);
    LineString model1 = factory.createLineString(Arrays.asList(
        new Coordinate(2., 3.),
        new Coordinate(6., 7.)));
    GeometryCollection geometry = factory.createGeometryCollection(List.of(model0, model1));
    GeometryCollectionEntity entity = colRepository.save(new GeometryCollectionEntity(geometry));
    log.info("Saved: {}", entity);
    assertNotNull(entity);
    assertNotNull(entity.getId());
    Optional<GeometryCollectionEntity> actual = colRepository.findById(entity.getId());
    assertTrue(actual.isPresent());
    assertEquals(entity, actual.get());
  }

  /**
   * Test line string.
   */
  @Test
  void testLineString() {
    LineString geometry = factory.createLineString(Arrays.asList(
        new Coordinate(2., 3.),
        new Coordinate(6., 7.)));
    GeometryEntity entity = repository.save(new GeometryEntity(geometry));
    log.info("Saved: {}", entity);
    assertNotNull(entity);
    assertNotNull(entity.getId());
    Optional<GeometryEntity> actual = repository.findById(entity.getId());
    assertTrue(actual.isPresent());
    assertEquals(entity, actual.get());
  }

  /**
   * Test multi line string.
   */
  @Test
  void testMultiLineString() {
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
    assertNotNull(entity);
    assertNotNull(entity.getId());
    Optional<GeometryEntity> actual = repository.findById(entity.getId());
    assertTrue(actual.isPresent());
    assertEquals(entity, actual.get());
  }

  /**
   * Test multi point.
   */
  @Test
  void testMultiPoint() {
    Point model0 = factory.createPoint(7., 8.);
    Point model1 = factory.createPoint(17., 18.);
    MultiPoint geometry = factory.createMultiPoint(Arrays.asList(model0, model1));
    GeometryEntity entity = repository.save(new GeometryEntity(geometry));
    log.info("Saved: {}", entity);
    assertNotNull(entity);
    assertNotNull(entity.getId());
    Optional<GeometryEntity> actual = repository.findById(entity.getId());
    assertTrue(actual.isPresent());
    assertEquals(entity, actual.get());
  }

  /**
   * Test multi polygon.
   */
  @Test
  void testMultiPolygon() {
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
    assertNotNull(entity);
    assertNotNull(entity.getId());
    Optional<GeometryEntity> actual = repository.findById(entity.getId());
    assertTrue(actual.isPresent());
    assertEquals(entity, actual.get());
  }

  /**
   * Test point.
   */
  @Test
  void testPoint() {
    Point geometry = factory.createPoint(2., 4.);
    GeometryEntity entity = repository.save(new GeometryEntity(geometry));
    log.info("Saved: {}", entity);
    assertNotNull(entity);
    assertNotNull(entity.getId());
    Optional<GeometryEntity> actual = repository.findById(entity.getId());
    assertTrue(actual.isPresent());
    assertEquals(entity, actual.get());
  }

  /**
   * Test polygon.
   */
  @Test
  void testPolygon() {
    LinearRing ring = factory.createLinearRing(Arrays.asList(
        new Coordinate(2., 3.),
        new Coordinate(6., 4.),
        new Coordinate(6., 8.),
        new Coordinate(2., 3.)));
    Polygon geometry = factory.createPolygon(ring);
    GeometryEntity entity = repository.save(new GeometryEntity(geometry));
    log.info("Saved: {}", entity);
    assertNotNull(entity);
    assertNotNull(entity.getId());
    Optional<GeometryEntity> actual = repository.findById(entity.getId());
    assertTrue(actual.isPresent());
    assertEquals(entity, actual.get());
  }

}

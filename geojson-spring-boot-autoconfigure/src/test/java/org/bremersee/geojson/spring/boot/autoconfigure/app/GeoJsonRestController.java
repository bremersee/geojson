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

package org.bremersee.geojson.spring.boot.autoconfigure.app;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.geojson.GeoJsonFeature;
import org.bremersee.geojson.GeoJsonFeatureCollection;
import org.bremersee.geojson.model.Feature;
import org.bremersee.geojson.model.FeatureCollection;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * The geo json rest controller.
 *
 * @author Christian Bremer
 */
@RestController
@Tag(name = "geo-controller", description = "The GeoJSON API.")
@Slf4j
public class GeoJsonRestController {

  @Autowired
  private GeometryEntityRepository repository;

  /**
   * Transform mono.
   *
   * @param id the id
   * @param geometry the geometry
   * @param withBoundingBox the with bounding box
   * @return the mono
   */
  @Operation(
      summary = "Transforms a geometry into a feature.",
      operationId = "transform",
      tags = {"geo-controller"},
      parameters = {
          @Parameter(name = "geometry",
              description = "The geometry you want to transform into a feature",
              in = ParameterIn.QUERY,
              schema = @Schema(type = "string"))
      },
      responses = @ApiResponse(
          responseCode = "200",
          description = "The feature of the given geometry.",
          content = @Content(
              schema = @Schema(implementation = Feature.class))
      )
  )
  @GetMapping(path = "/geo/transform", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<GeoJsonFeature<Geometry, Object>> transform(
      @RequestParam(name = "id") String id,
      @RequestParam(name = "geometry") @Parameter(hidden = true) Geometry geometry,
      @RequestParam(name = "withBoundingBox") boolean withBoundingBox) {

    log.info("Got geometry: {}", geometry.toText());
    GeoJsonFeature<Geometry, Object> feature = new GeoJsonFeature<>(
        id, geometry, withBoundingBox, null);
    log.info("Transformed feature: {}", feature);
    return Mono.just(feature);
  }

  /**
   * Find features mono.
   *
   * @return the mono
   */
  @Operation(
      summary = "Find feature.",
      operationId = "findFeatures",
      tags = {"geo-controller"},
      responses = @ApiResponse(
          responseCode = "200",
          description = "The test feature from the database.",
          content = @Content(
              schema = @Schema(implementation = FeatureCollection.class))
      )
  )
  @GetMapping(path = "/geo", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<GeoJsonFeatureCollection<Geometry, Object>> findFeatures() {
    return repository.findAll()
        .map(entity -> new GeoJsonFeature<>(entity.getId(), entity.getGeometry(), false, null))
        .collect(
            () -> new GeoJsonFeatureCollection<>(
                true,
                (o1, o2) -> o1.getId().compareToIgnoreCase(o2.getId())),
            GeoJsonFeatureCollection::add);
  }

  /**
   * Save feature mono.
   *
   * @param geometry the geometry
   * @return the mono
   */
  @Operation(
      summary = "Saves a geometry.",
      operationId = "saveFeature",
      tags = {"geo-controller"},
      responses = @ApiResponse(
          responseCode = "200",
          description = "The saved geometry as feature.",
          content = @Content(
              schema = @Schema(implementation = Feature.class))
      )
  )
  @PostMapping(path = "/geo")
  public Mono<GeoJsonFeature<Geometry, Object>> saveFeature(
      @Schema(implementation = org.bremersee.geojson.model.Geometry.class)
      @RequestBody Geometry geometry) {

    GeometryEntity entity = new GeometryEntity();
    entity.setGeometry(geometry);
    return repository.save(entity)
        .map(e -> new GeoJsonFeature<>(e.getId(), e.getGeometry(), true, null));
  }

}

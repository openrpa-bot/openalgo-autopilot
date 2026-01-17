package com.nigam.openalgo.autopilot.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/autopilot/v1")
@Tag(name = "OpenAlgo Autopilot API", description = "REST API endpoints for OpenAlgo Autopilot")
public class ApiController {
    
    @GetMapping("/health")
    @Operation(summary = "Health check endpoint", description = "Returns the health status of the API service")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service is healthy",
            content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "OpenAlgo Autopilot API");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/info")
    @Operation(summary = "API information", description = "Returns information about the API including name and version")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API information retrieved successfully",
            content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "OpenAlgo Autopilot API");
        response.put("version", "1.0.0-SNAPSHOT");
        return ResponseEntity.ok(response);
    }
}

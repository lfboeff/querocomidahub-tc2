package br.com.fiap.querocomidahub.restaurant.infrastructure.web.controller;

import br.com.fiap.querocomidahub.restaurant.infrastructure.web.json.RestaurantRequestJson;
import br.com.fiap.querocomidahub.restaurant.infrastructure.web.json.RestaurantResponseJson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Tag(name = "Restaurants", description = "Restaurant management endpoints")
public interface RestaurantApi {

    @Operation(summary = "List all restaurants",
            description = "Returns all restaurants ordered by id. Menu items are not embedded in the list — use GET /{id} to get menu items.")
    @ApiResponse(
            responseCode = "200",
            description = "List of restaurants retrieved successfully",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = RestaurantResponseJson.class)))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class))
    )
    ResponseEntity<List<RestaurantResponseJson>> findAll();

    @Operation(summary = "Get a restaurant by ID",
            description = "Returns the restaurant with its menu items embedded.")
    @ApiResponse(
            responseCode = "200",
            description = "Restaurant found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RestaurantResponseJson.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid ID (non-numeric)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Invalid value 'abc' for this endpoint",
                              "instance": "/api/v1/restaurants/abc"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Restaurant not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Not Found",
                              "status": 404,
                              "detail": "Restaurant with id='99' was not found",
                              "instance": "/api/v1/restaurants/99"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class))
    )
    ResponseEntity<RestaurantResponseJson> findById(
            @Parameter(name = "id", description = "ID of the restaurant", example = "1", required = true)
            @PathVariable Long id);

    @Operation(summary = "Create a restaurant",
            description = "Requires the header 'X-User-Id' identifying a user with 'canManageRestaurants=true'. The caller becomes the owner of the created restaurant.")
    @ApiResponse(
            responseCode = "201",
            description = "Restaurant created successfully",
            headers = @Header(name = "Location", description = "URL of the created restaurant",
                    schema = @Schema(example = "/api/v1/restaurants/3"))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Blank field", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Bad Request",
                                      "status": 400,
                                      "detail": "Request validation failed",
                                      "instance": "/api/v1/restaurants",
                                      "errors": ["name: must not be blank"]
                                    }
                                    """),
                            @ExampleObject(name = "Missing body", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Bad Request",
                                      "status": 400,
                                      "detail": "The request body is malformed or missing",
                                      "instance": "/api/v1/restaurants"
                                    }
                                    """)
                    })
    )
    @ApiResponse(
            responseCode = "401",
            description = "Missing or invalid X-User-Id header",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Missing header", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Unauthorized",
                                      "status": 401,
                                      "detail": "Header 'X-User-Id' is required",
                                      "instance": "/api/v1/restaurants"
                                    }
                                    """),
                            @ExampleObject(name = "Non-numeric header", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Unauthorized",
                                      "status": 401,
                                      "detail": "Header 'X-User-Id' must be a valid numeric user id",
                                      "instance": "/api/v1/restaurants"
                                    }
                                    """)
                    })
    )
    @ApiResponse(
            responseCode = "403",
            description = "Caller user does not have permission to manage restaurants",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Forbidden",
                              "status": 403,
                              "detail": "User with id='1' does not have permission to manage restaurants",
                              "instance": "/api/v1/restaurants"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Caller user not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Not Found",
                              "status": 404,
                              "detail": "User with id='99' was not found",
                              "instance": "/api/v1/restaurants"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class))
    )
    ResponseEntity<Void> create(
            @RequestBody(description = "Restaurant data to create", required = true)
            RestaurantRequestJson request,
            @Parameter(name = "X-User-Id", description = "ID of the calling user", example = "2", required = true, in = ParameterIn.HEADER)
            @RequestHeader(value = "X-User-Id", required = false) String userId);

    @Operation(summary = "Update a restaurant",
            description = "Requires the header 'X-User-Id' identifying the owner of the restaurant. Only the owner can update it.")
    @ApiResponse(responseCode = "204", description = "Restaurant updated successfully")
    @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Blank field", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Bad Request",
                                      "status": 400,
                                      "detail": "Request validation failed",
                                      "instance": "/api/v1/restaurants/1",
                                      "errors": ["name: must not be blank"]
                                    }
                                    """),
                            @ExampleObject(name = "Invalid ID", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Bad Request",
                                      "status": 400,
                                      "detail": "Invalid value 'abc' for this endpoint",
                                      "instance": "/api/v1/restaurants/abc"
                                    }
                                    """)
                    })
    )
    @ApiResponse(
            responseCode = "401",
            description = "Missing or invalid X-User-Id header",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Unauthorized",
                              "status": 401,
                              "detail": "Header 'X-User-Id' is required",
                              "instance": "/api/v1/restaurants/1"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "403",
            description = "Caller is not the owner of the restaurant",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Forbidden",
                              "status": 403,
                              "detail": "User with id='3' is not the owner of restaurant with id='1'",
                              "instance": "/api/v1/restaurants/1"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Restaurant or caller user not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Not Found",
                              "status": 404,
                              "detail": "Restaurant with id='99' was not found",
                              "instance": "/api/v1/restaurants/99"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class))
    )
    ResponseEntity<Void> update(
            @Parameter(name = "id", description = "ID of the restaurant to update", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody(description = "Updated restaurant data", required = true)
            RestaurantRequestJson request,
            @Parameter(name = "X-User-Id", description = "ID of the calling user", example = "2", required = true, in = ParameterIn.HEADER)
            @RequestHeader(value = "X-User-Id", required = false) String userId);

    @Operation(summary = "Delete a restaurant",
            description = "Requires the header 'X-User-Id' identifying the owner of the restaurant. Deleting a restaurant cascades to its menu items.")
    @ApiResponse(responseCode = "204", description = "Restaurant deleted successfully")
    @ApiResponse(
            responseCode = "400",
            description = "Invalid ID (non-numeric)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Missing or invalid X-User-Id header",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Unauthorized",
                              "status": 401,
                              "detail": "Header 'X-User-Id' is required",
                              "instance": "/api/v1/restaurants/1"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "403",
            description = "Caller is not the owner of the restaurant",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Forbidden",
                              "status": 403,
                              "detail": "User with id='3' is not the owner of restaurant with id='1'",
                              "instance": "/api/v1/restaurants/1"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Restaurant or caller user not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Not Found",
                              "status": 404,
                              "detail": "Restaurant with id='99' was not found",
                              "instance": "/api/v1/restaurants/99"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class))
    )
    ResponseEntity<Void> delete(
            @Parameter(name = "id", description = "ID of the restaurant to delete", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(name = "X-User-Id", description = "ID of the calling user", example = "2", required = true, in = ParameterIn.HEADER)
            @RequestHeader(value = "X-User-Id", required = false) String userId);
}

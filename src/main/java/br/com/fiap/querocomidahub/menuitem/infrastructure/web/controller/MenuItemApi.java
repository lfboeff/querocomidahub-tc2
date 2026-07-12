package br.com.fiap.querocomidahub.menuitem.infrastructure.web.controller;

import br.com.fiap.querocomidahub.menuitem.infrastructure.web.json.MenuItemRequestJson;
import br.com.fiap.querocomidahub.menuitem.infrastructure.web.json.MenuItemResponseJson;
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

@Tag(name = "Menu Items", description = "Menu item management endpoints")
public interface MenuItemApi {

    @Operation(summary = "List all menu items of a restaurant",
            description = "Returns all menu items of the given restaurant ordered by id.")
    @ApiResponse(
            responseCode = "200",
            description = "List of menu items retrieved successfully",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = MenuItemResponseJson.class)))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Restaurant not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class))
    )
    ResponseEntity<List<MenuItemResponseJson>> findAll(
            @Parameter(name = "restaurantId", description = "ID of the parent restaurant",
                    example = "1", required = true)
            @PathVariable Long restaurantId);

    @Operation(summary = "Get a menu item by ID",
            description = "Returns the menu item belonging to the given restaurant.")
    @ApiResponse(
            responseCode = "200",
            description = "Menu item found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MenuItemResponseJson.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid ID (non-numeric)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Restaurant or menu item not found (also returned when the item exists but belongs to a different restaurant)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Restaurant not found", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Not Found",
                                      "status": 404,
                                      "detail": "Restaurant with id='99' was not found",
                                      "instance": "/api/v1/restaurants/99/menu-items/1"
                                    }
                                    """),
                            @ExampleObject(name = "Menu item not found", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Not Found",
                                      "status": 404,
                                      "detail": "Menu item with id='99' was not found",
                                      "instance": "/api/v1/restaurants/1/menu-items/99"
                                    }
                                    """)
                    })
    )
    @ApiResponse(
            responseCode = "500",
            description = "Server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class))
    )
    ResponseEntity<MenuItemResponseJson> findById(
            @Parameter(name = "restaurantId", description = "ID of the parent restaurant",
                    example = "1", required = true)
            @PathVariable Long restaurantId,
            @Parameter(name = "id", description = "ID of the menu item",
                    example = "1", required = true)
            @PathVariable Long id);

    @Operation(summary = "Create a menu item",
            description = "Requires the header 'X-User-Id' identifying the owner of the parent restaurant. Only the owner can add menu items.")
    @ApiResponse(
            responseCode = "201",
            description = "Menu item created successfully",
            headers = @Header(name = "Location", description = "URL of the created menu item",
                    schema = @Schema(example = "/api/v1/restaurants/1/menu-items/3"))
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
                                      "instance": "/api/v1/restaurants/1/menu-items",
                                      "errors": ["name: must not be blank"]
                                    }
                                    """),
                            @ExampleObject(name = "Price too low", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Bad Request",
                                      "status": 400,
                                      "detail": "Request validation failed",
                                      "instance": "/api/v1/restaurants/1/menu-items",
                                      "errors": ["price: must be greater than or equal to 0.01"]
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
                              "instance": "/api/v1/restaurants/1/menu-items"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "403",
            description = "Caller is not the owner of the parent restaurant",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Forbidden",
                              "status": 403,
                              "detail": "User with id='3' is not the owner of restaurant with id='1'",
                              "instance": "/api/v1/restaurants/1/menu-items"
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
                              "instance": "/api/v1/restaurants/99/menu-items"
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
            @Parameter(name = "restaurantId", description = "ID of the parent restaurant",
                    example = "1", required = true)
            @PathVariable Long restaurantId,
            @RequestBody(description = "Menu item data to create", required = true)
            MenuItemRequestJson request,
            @Parameter(name = "X-User-Id", description = "ID of the calling user", example = "2", required = true, in = ParameterIn.HEADER)
            @RequestHeader(value = "X-User-Id", required = false) String userId);

    @Operation(summary = "Update a menu item",
            description = "Requires the header 'X-User-Id' identifying the owner of the parent restaurant. Only the owner can update its menu items.")
    @ApiResponse(responseCode = "204", description = "Menu item updated successfully")
    @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Bad Request",
                              "status": 400,
                              "detail": "Request validation failed",
                              "instance": "/api/v1/restaurants/1/menu-items/1",
                              "errors": ["price: must be greater than or equal to 0.01"]
                            }
                            """))
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
                              "instance": "/api/v1/restaurants/1/menu-items/1"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "403",
            description = "Caller is not the owner of the parent restaurant",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Forbidden",
                              "status": 403,
                              "detail": "User with id='3' is not the owner of restaurant with id='1'",
                              "instance": "/api/v1/restaurants/1/menu-items/1"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Restaurant, menu item, or caller user not found (also when the item belongs to a different restaurant)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Not Found",
                              "status": 404,
                              "detail": "Menu item with id='99' was not found",
                              "instance": "/api/v1/restaurants/1/menu-items/99"
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
            @Parameter(name = "restaurantId", description = "ID of the parent restaurant",
                    example = "1", required = true)
            @PathVariable Long restaurantId,
            @Parameter(name = "id", description = "ID of the menu item to update",
                    example = "1", required = true)
            @PathVariable Long id,
            @RequestBody(description = "Updated menu item data", required = true)
            MenuItemRequestJson request,
            @Parameter(name = "X-User-Id", description = "ID of the calling user", example = "2", required = true, in = ParameterIn.HEADER)
            @RequestHeader(value = "X-User-Id", required = false) String userId);

    @Operation(summary = "Delete a menu item",
            description = "Requires the header 'X-User-Id' identifying the owner of the parent restaurant. Only the owner can delete its menu items.")
    @ApiResponse(responseCode = "204", description = "Menu item deleted successfully")
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
                              "instance": "/api/v1/restaurants/1/menu-items/1"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "403",
            description = "Caller is not the owner of the parent restaurant",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Forbidden",
                              "status": 403,
                              "detail": "User with id='3' is not the owner of restaurant with id='1'",
                              "instance": "/api/v1/restaurants/1/menu-items/1"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Restaurant, menu item, or caller user not found (also when the item belongs to a different restaurant)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Not Found",
                              "status": 404,
                              "detail": "Menu item with id='99' was not found",
                              "instance": "/api/v1/restaurants/1/menu-items/99"
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
            @Parameter(name = "restaurantId", description = "ID of the parent restaurant",
                    example = "1", required = true)
            @PathVariable Long restaurantId,
            @Parameter(name = "id", description = "ID of the menu item to delete",
                    example = "1", required = true)
            @PathVariable Long id,
            @Parameter(name = "X-User-Id", description = "ID of the calling user", example = "2", required = true, in = ParameterIn.HEADER)
            @RequestHeader(value = "X-User-Id", required = false) String userId);
}

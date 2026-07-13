package br.com.fiap.querocomidahub.usertype.infrastructure.web.controller;

import br.com.fiap.querocomidahub.usertype.infrastructure.web.json.UserTypeRequestJson;
import br.com.fiap.querocomidahub.usertype.infrastructure.web.json.UserTypeResponseJson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.util.List;

@Tag(name = "User Types", description = "User type management endpoints")
public interface IUserTypeSwaggerDoc {

    // -------------------------------------------------------------------------
    // GET /api/v1/user-types
    // -------------------------------------------------------------------------
    @Operation(summary = "List user types")
    @ApiResponse(
            responseCode = "200",
            description = "List of user types retrieved successfully",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserTypeResponseJson.class)))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "An unexpected error occurred",
                              "instance": "/api/v1/user-types"
                            }
                            """))
    )
    ResponseEntity<List<UserTypeResponseJson>> findAll();

    // -------------------------------------------------------------------------
    // GET /api/v1/user-types/{id}
    // -------------------------------------------------------------------------
    @Operation(summary = "Get a user type by ID")
    @ApiResponse(
            responseCode = "200",
            description = "User type found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserTypeResponseJson.class))
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
                              "instance": "/api/v1/user-types/abc"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "404",
            description = "User type not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Not Found",
                              "status": 404,
                              "detail": "User type with id='99' was not found",
                              "instance": "/api/v1/user-types/99"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "An unexpected error occurred",
                              "instance": "/api/v1/user-types/1"
                            }
                            """))
    )
    ResponseEntity<UserTypeResponseJson> findById(
            @Parameter(name = "id", description = "ID of the user type", example = "1", required = true)
            @PathVariable Long id);

    // -------------------------------------------------------------------------
    // POST /api/v1/user-types
    // -------------------------------------------------------------------------
    @Operation(summary = "Create a user type")
    @ApiResponse(
            responseCode = "201",
            description = "User type created successfully",
            headers = @Header(name = "Location", description = "URL of the created user type",
                    schema = @Schema(example = "/api/v1/user-types/4"))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Blank name", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Bad Request",
                                      "status": 400,
                                      "detail": "Request validation failed",
                                      "instance": "/api/v1/user-types",
                                      "errors": ["name: must not be blank"]
                                    }
                                    """),
                            @ExampleObject(name = "Name exceeds 50 characters", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Bad Request",
                                      "status": 400,
                                      "detail": "Request validation failed",
                                      "instance": "/api/v1/user-types",
                                      "errors": ["name: size must be between 0 and 50"]
                                    }
                                    """),
                            @ExampleObject(name = "Missing body", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Bad Request",
                                      "status": 400,
                                      "detail": "The request body is malformed or missing",
                                      "instance": "/api/v1/user-types"
                                    }
                                    """)
                    })
    )
    @ApiResponse(
            responseCode = "409",
            description = "Name already in use",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Conflict",
                              "status": 409,
                              "detail": "User type with name='Motoboy' already exists",
                              "instance": "/api/v1/user-types"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "An unexpected error occurred",
                              "instance": "/api/v1/user-types"
                            }
                            """))
    )
    ResponseEntity<Void> create(
            @RequestBody(description = "User type data to create", required = true)
            UserTypeRequestJson request);

    // -------------------------------------------------------------------------
    // PUT /api/v1/user-types/{id}
    // -------------------------------------------------------------------------
    @Operation(summary = "Update a user type")
    @ApiResponse(responseCode = "204", description = "User type updated successfully")
    @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Blank name", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Bad Request",
                                      "status": 400,
                                      "detail": "Request validation failed",
                                      "instance": "/api/v1/user-types/3",
                                      "errors": ["name: must not be blank"]
                                    }
                                    """),
                            @ExampleObject(name = "Invalid ID", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Bad Request",
                                      "status": 400,
                                      "detail": "Invalid value 'abc' for this endpoint",
                                      "instance": "/api/v1/user-types/abc"
                                    }
                                    """)
                    })
    )
    @ApiResponse(
            responseCode = "404",
            description = "User type not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Not Found",
                              "status": 404,
                              "detail": "User type with id='99' was not found",
                              "instance": "/api/v1/user-types/99"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "409",
            description = "Conflict — system type or duplicate name",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "System type", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Conflict",
                                      "status": 409,
                                      "detail": "User type with id='2' is a system type and cannot be modified",
                                      "instance": "/api/v1/user-types/2"
                                    }
                                    """),
                            @ExampleObject(name = "Duplicate name", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Conflict",
                                      "status": 409,
                                      "detail": "User type with name='Cliente' already exists",
                                      "instance": "/api/v1/user-types/3"
                                    }
                                    """)
                    })
    )
    @ApiResponse(
            responseCode = "500",
            description = "Server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "An unexpected error occurred",
                              "instance": "/api/v1/user-types/3"
                            }
                            """))
    )
    ResponseEntity<Void> update(
            @Parameter(name = "id", description = "ID of the user type to update", example = "3", required = true)
            @PathVariable Long id,
            @RequestBody(description = "Updated user type data", required = true)
            UserTypeRequestJson request);

    // -------------------------------------------------------------------------
    // DELETE /api/v1/user-types/{id}
    // -------------------------------------------------------------------------
    @Operation(summary = "Delete a user type")
    @ApiResponse(responseCode = "204", description = "User type deleted successfully")
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
                              "instance": "/api/v1/user-types/abc"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "404",
            description = "User type not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Not Found",
                              "status": 404,
                              "detail": "User type with id='99' was not found",
                              "instance": "/api/v1/user-types/99"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "409",
            description = "Conflict — system type or type in use",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "System type", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Conflict",
                                      "status": 409,
                                      "detail": "User type with id='2' is a system type and cannot be deleted",
                                      "instance": "/api/v1/user-types/2"
                                    }
                                    """),
                            @ExampleObject(name = "Type in use", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Conflict",
                                      "status": 409,
                                      "detail": "User type with id='3' is in use and cannot be deleted",
                                      "instance": "/api/v1/user-types/3"
                                    }
                                    """)
                    })
    )
    @ApiResponse(
            responseCode = "500",
            description = "Server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Internal Server Error",
                              "status": 500,
                              "detail": "An unexpected error occurred",
                              "instance": "/api/v1/user-types/3"
                            }
                            """))
    )
    ResponseEntity<Void> delete(
            @Parameter(name = "id", description = "ID of the user type to delete", example = "3", required = true)
            @PathVariable Long id);
}

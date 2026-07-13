package br.com.fiap.querocomidahub.user.infrastructure.web.controller;

import br.com.fiap.querocomidahub.user.infrastructure.web.json.AssignUserTypeRequestJson;
import br.com.fiap.querocomidahub.user.infrastructure.web.json.CreateUserRequestJson;
import br.com.fiap.querocomidahub.user.infrastructure.web.json.UpdateUserRequestJson;
import br.com.fiap.querocomidahub.user.infrastructure.web.json.UserResponseJson;
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

@Tag(name = "Users", description = "User management endpoints")
public interface IUserSwaggerDoc {

    @Operation(summary = "List all users",
            description = "Returns all users ordered by id.")
    @ApiResponse(
            responseCode = "200",
            description = "List of users retrieved successfully",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserResponseJson.class)))
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
                              "instance": "/api/v1/users"
                            }
                            """))
    )
    ResponseEntity<List<UserResponseJson>> findAll();

    @Operation(summary = "Get a user by ID")
    @ApiResponse(
            responseCode = "200",
            description = "User found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseJson.class))
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
                              "instance": "/api/v1/users/abc"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Not Found",
                              "status": 404,
                              "detail": "User with id='99' was not found",
                              "instance": "/api/v1/users/99"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class))
    )
    ResponseEntity<UserResponseJson> findById(
            @Parameter(name = "id", description = "ID of the user", example = "1", required = true)
            @PathVariable Long id);

    @Operation(summary = "Create a user")
    @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            headers = @Header(name = "Location", description = "URL of the created user",
                    schema = @Schema(example = "/api/v1/users/4"))
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
                                      "instance": "/api/v1/users",
                                      "errors": ["name: must not be blank"]
                                    }
                                    """),
                            @ExampleObject(name = "Invalid email format", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Bad Request",
                                      "status": 400,
                                      "detail": "Request validation failed",
                                      "instance": "/api/v1/users",
                                      "errors": ["email: must be a well-formed email address"]
                                    }
                                    """),
                            @ExampleObject(name = "Missing body", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Bad Request",
                                      "status": 400,
                                      "detail": "The request body is malformed or missing",
                                      "instance": "/api/v1/users"
                                    }
                                    """),
                            @ExampleObject(name = "User type not found", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Bad Request",
                                      "status": 400,
                                      "detail": "User type with id='99' was not found",
                                      "instance": "/api/v1/users"
                                    }
                                    """)
                    })
    )
    @ApiResponse(
            responseCode = "409",
            description = "Email already in use",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Conflict",
                              "status": 409,
                              "detail": "User with email='joao.silva@email.com' already exists",
                              "instance": "/api/v1/users"
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
            @RequestBody(description = "User data to create", required = true)
            CreateUserRequestJson request);

    @Operation(summary = "Update a user's personal data",
            description = "Updates name, email and address. To change the user's type, use PATCH /api/v1/users/{id}/user-type.")
    @ApiResponse(responseCode = "204", description = "User updated successfully")
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
                                      "instance": "/api/v1/users/3",
                                      "errors": ["name: must not be blank"]
                                    }
                                    """),
                            @ExampleObject(name = "Invalid ID", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Bad Request",
                                      "status": 400,
                                      "detail": "Invalid value 'abc' for this endpoint",
                                      "instance": "/api/v1/users/abc"
                                    }
                                    """)
                    })
    )
    @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Not Found",
                              "status": 404,
                              "detail": "User with id='99' was not found",
                              "instance": "/api/v1/users/99"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "409",
            description = "Email already in use by another user",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Conflict",
                              "status": 409,
                              "detail": "User with email='joao.silva@email.com' already exists",
                              "instance": "/api/v1/users/3"
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
            @Parameter(name = "id", description = "ID of the user to update", example = "3", required = true)
            @PathVariable Long id,
            @RequestBody(description = "Updated user data", required = true)
            UpdateUserRequestJson request);

    @Operation(summary = "Delete a user")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
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
                              "instance": "/api/v1/users/abc"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Not Found",
                              "status": 404,
                              "detail": "User with id='99' was not found",
                              "instance": "/api/v1/users/99"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "409",
            description = "User owns one or more restaurants and cannot be deleted",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Conflict",
                              "status": 409,
                              "detail": "User with id='2' cannot be deleted because it is assigned to one or more restaurants",
                              "instance": "/api/v1/users/2"
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
            @Parameter(name = "id", description = "ID of the user to delete", example = "3", required = true)
            @PathVariable Long id);

    @Operation(summary = "Assign a user type to a user",
            description = "Idempotent operation. If the user already has the given type, no change is persisted.")
    @ApiResponse(responseCode = "204", description = "User type assigned successfully (or already assigned)")
    @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Missing userTypeId", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Bad Request",
                                      "status": 400,
                                      "detail": "Request validation failed",
                                      "instance": "/api/v1/users/3/user-type",
                                      "errors": ["userTypeId: must not be null"]
                                    }
                                    """),
                            @ExampleObject(name = "User type not found", value = """
                                    {
                                      "type": "about:blank",
                                      "title": "Bad Request",
                                      "status": 400,
                                      "detail": "User type with id='99' was not found",
                                      "instance": "/api/v1/users/3/user-type"
                                    }
                                    """)
                    })
    )
    @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Not Found",
                              "status": 404,
                              "detail": "User with id='99' was not found",
                              "instance": "/api/v1/users/99/user-type"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "409",
            description = "User still owns restaurants and cannot lose the ability to manage them",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                            {
                              "type": "about:blank",
                              "title": "Conflict",
                              "status": 409,
                              "detail": "User with id='2' cannot lose the ability to manage restaurants while it still owns one or more",
                              "instance": "/api/v1/users/2/user-type"
                            }
                            """))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Server error",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class))
    )
    ResponseEntity<Void> assignUserType(
            @Parameter(name = "id", description = "ID of the user", example = "3", required = true)
            @PathVariable Long id,
            @RequestBody(description = "User type to assign", required = true)
            AssignUserTypeRequestJson request);
}

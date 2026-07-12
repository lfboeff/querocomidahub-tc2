package br.com.fiap.querocomidahub.shared.infrastructure.exception;

import br.com.fiap.querocomidahub.menuitem.domain.exception.MenuItemNotFoundException;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantAccessDeniedException;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantManagementNotAllowedException;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantNotFoundException;
import br.com.fiap.querocomidahub.shared.domain.exception.DomainValidationException;
import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.shared.infrastructure.gateway.LoggerGatewayFactory;
import br.com.fiap.querocomidahub.user.domain.exception.InvalidUserTypeException;
import br.com.fiap.querocomidahub.user.domain.exception.UserDuplicateEmailException;
import br.com.fiap.querocomidahub.user.domain.exception.UserInUseInRestaurantsException;
import br.com.fiap.querocomidahub.user.domain.exception.UserNotFoundException;
import br.com.fiap.querocomidahub.user.domain.exception.UserOwnsRestaurantsException;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeDuplicateNameException;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeInUseException;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeIsSystemException;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ILoggerGateway logger = LoggerGatewayFactory.forClass(GlobalExceptionHandler.class);

    /* ----------------------------------------------------------------------------------------------------
     *   Shared Domain Exceptions
     * ---------------------------------------------------------------------------------------------------- */

    @ExceptionHandler(DomainValidationException.class)
    public ProblemDetail handleDomainValidation(DomainValidationException ex) {
        logger.warn("Domain validation failed: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /* ----------------------------------------------------------------------------------------------------
     *   General Exceptions
     * ---------------------------------------------------------------------------------------------------- */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        logger.warn("Validation failed on '{} {}': {}", request.getMethod(), request.getRequestURI(), errors);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleNotReadable(HttpServletRequest request) {
        logger.warn("Malformed request body on '{} {}'", request.getMethod(), request.getRequestURI());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "The request body is malformed or missing");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        logger.warn("Method '{}' not allowed at '{}'", request.getMethod(), request.getRequestURI());
        return ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        logger.warn("Invalid path parameter: value='{}' for parameter '{}' at '{} {}'", ex.getValue(), ex.getName(), request.getMethod(), request.getRequestURI());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid value '" + ex.getValue() + "' for this endpoint");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        logger.warn("Resource not found: '{} {}'", request.getMethod(), request.getRequestURI());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatus(ResponseStatusException ex) {
        HttpStatusCode statusCode = ex.getStatusCode();
        String reason = ex.getReason() != null ? ex.getReason() : "Request rejected";
        return ProblemDetail.forStatusAndDetail(statusCode, reason);
    }

    @ExceptionHandler(DataAccessException.class)
    public ProblemDetail handleDataAccess(DataAccessException ex) {
        logger.error("Data access error", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "A database error occurred");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        logger.error("Unexpected error", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    /* ----------------------------------------------------------------------------------------------------
     *   UserType Exceptions
     * ---------------------------------------------------------------------------------------------------- */

    @ExceptionHandler(UserTypeNotFoundException.class)
    public ProblemDetail handleUserTypeNotFound(UserTypeNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UserTypeDuplicateNameException.class)
    public ProblemDetail handleUserTypeDuplicateName(UserTypeDuplicateNameException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(UserTypeInUseException.class)
    public ProblemDetail handleUserTypeInUse(UserTypeInUseException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(UserTypeIsSystemException.class)
    public ProblemDetail handleUserTypeIsSystem(UserTypeIsSystemException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    /* ----------------------------------------------------------------------------------------------------
     *   User Exceptions
     * ---------------------------------------------------------------------------------------------------- */

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UserDuplicateEmailException.class)
    public ProblemDetail handleUserDuplicateEmail(UserDuplicateEmailException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidUserTypeException.class)
    public ProblemDetail handleInvalidUserType(InvalidUserTypeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UserInUseInRestaurantsException.class)
    public ProblemDetail handleUserInUseInRestaurants(UserInUseInRestaurantsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(UserOwnsRestaurantsException.class)
    public ProblemDetail handleUserOwnsRestaurants(UserOwnsRestaurantsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    /* ----------------------------------------------------------------------------------------------------
     *   Restaurant Exceptions
     * ---------------------------------------------------------------------------------------------------- */

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ProblemDetail handleRestaurantNotFound(RestaurantNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RestaurantAccessDeniedException.class)
    public ProblemDetail handleRestaurantAccessDenied(RestaurantAccessDeniedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(RestaurantManagementNotAllowedException.class)
    public ProblemDetail handleRestaurantManagementNotAllowed(RestaurantManagementNotAllowedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    /* ----------------------------------------------------------------------------------------------------
     *   MenuItem Exceptions
     * ---------------------------------------------------------------------------------------------------- */

    @ExceptionHandler(MenuItemNotFoundException.class)
    public ProblemDetail handleMenuItemNotFound(MenuItemNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}

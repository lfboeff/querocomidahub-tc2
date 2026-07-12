package br.com.fiap.querocomidahub.shared.infrastructure.security;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.shared.infrastructure.gateway.LoggerGatewayFactory;
import br.com.fiap.querocomidahub.user.domain.exception.UserNotFoundException;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
import br.com.fiap.querocomidahub.user.domain.model.UserBase;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserIdentityResolver {

    public static final String HEADER_USER_ID = "X-User-Id";

    private final IUserGateway userGateway;
    private final ILoggerGateway logger = LoggerGatewayFactory.forClass(UserIdentityResolver.class);

    public UserIdentityResolver(IUserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public UserBase resolve(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            logger.warn("Request is missing required header '{}'", HEADER_USER_ID);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Header '" + HEADER_USER_ID + "' is required");
        }

        Long userId;
        try {
            userId = Long.parseLong(headerValue.trim());
        } catch (NumberFormatException e) {
            logger.warn("Header '{}' contains a non-numeric value: '{}'", HEADER_USER_ID, headerValue.trim());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Header '" + HEADER_USER_ID + "' must be a valid numeric user id");
        }

        return userGateway.findById(userId)
                .orElseGet(() -> {
                    logger.warn("User with id='{}' was not found in the database", userId);
                    throw new UserNotFoundException(userId);
                });
    }
}

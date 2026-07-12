package br.com.fiap.querocomidahub.shared.infrastructure.gateway;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoggerGatewayImpl implements ILoggerGateway {

    private final Logger log;

    public LoggerGatewayImpl(Class<?> clazz) {
        this.log = LoggerFactory.getLogger(clazz);
    }

    @Override
    public void info(String message, Object... args) {
        log.info(message, args);
    }

    @Override
    public void warn(String message, Object... args) {
        log.warn(message, args);
    }

    @Override
    public void error(String message, Object... args) {
        log.error(message, args);
    }

    @Override
    public void error(String message, Throwable throwable) {
        log.error(message, throwable);
    }
}

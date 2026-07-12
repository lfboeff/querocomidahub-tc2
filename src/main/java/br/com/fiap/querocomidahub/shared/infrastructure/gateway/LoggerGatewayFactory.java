package br.com.fiap.querocomidahub.shared.infrastructure.gateway;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;

public final class LoggerGatewayFactory {

    private LoggerGatewayFactory() {
    }

    public static ILoggerGateway forClass(Class<?> clazz) {
        return new LoggerGatewayImpl(clazz);
    }
}

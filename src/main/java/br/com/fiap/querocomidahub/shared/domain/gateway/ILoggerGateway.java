package br.com.fiap.querocomidahub.shared.domain.gateway;

public interface ILoggerGateway {

    void info(String message, Object... args);

    void warn(String message, Object... args);

    void error(String message, Object... args);

    void error(String message, Throwable throwable);
}

package br.com.fiap.querocomidahub.shared.infrastructure.gateway;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest
@Transactional
@Import(IntegrationTestBase.ContainerConfig.class)
public abstract class IntegrationTestBase {

    @TestConfiguration
    static class ContainerConfig {

        @Bean
        @ServiceConnection
        @SuppressWarnings("resource")
        MySQLContainer<?> mySQLContainer() {
            return new MySQLContainer<>("mysql:8.4")
                    .withDatabaseName("querocomidahub_db");
        }
    }
}

package com.sashimi.global.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;

@Configuration
@ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "true", matchIfMissing = true)
public class FlywayConfig {

    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(
                        "classpath:db/migration/common",
                        "classpath:db/migration/be1",
                        "classpath:db/migration/be2",
                        "classpath:db/migration/be3",
                        "classpath:db/migration/be4",
                        "classpath:db/migration/be5"
                )
                .baselineOnMigrate(true)
                .outOfOrder(true)
                .encoding(StandardCharsets.UTF_8)
                .load();
        flyway.repair();
        flyway.migrate();
        return flyway;
    }
}
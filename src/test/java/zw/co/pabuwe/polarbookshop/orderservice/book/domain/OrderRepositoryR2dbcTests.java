package zw.co.pabuwe.polarbookshop.orderservice.book.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;
import zw.co.pabuwe.polarbookshop.orderservice.config.DataConfig;
import zw.co.pabuwe.polarbookshop.orderservice.order.domain.OrderRepository;
import zw.co.pabuwe.polarbookshop.orderservice.order.domain.OrderService;
import zw.co.pabuwe.polarbookshop.orderservice.order.domain.OrderStatus;

@DataR2dbcTest
@Import(DataConfig.class)
@Testcontainers
class OrderRepositoryR2dbcTests {

    @Container
    static PostgreSQLContainer<?> postgreSQL = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14.3"));

    @Autowired
    private OrderRepository orderRepository;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", OrderRepositoryR2dbcTests::r2dbcUrl);
        registry.add("spring.r2dbc.username", postgreSQL::getUsername);
        registry.add("spring.r2dbc.password", postgreSQL::getPassword);
        registry.add("spring.flyway.url", postgreSQL::getJdbcUrl);
    }

    private static String r2dbcUrl() {
        return String.format("r2dbc:postgresql://%s:%s/%s",
                postgreSQL.getHost(),
                postgreSQL.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
                postgreSQL.getDatabaseName());
    }

    @Test
    void findOrderByIdWhenNotExisting() {
        StepVerifier.create(orderRepository.findById(394L))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void createRejectedOrder() {
        var rejectedOrder = OrderService.buildRejectedOrder("1234567890", 3);
        StepVerifier
                .create(orderRepository.save(rejectedOrder))
                .expectNextMatches(order -> order.status().equals(OrderStatus.REJECTED))
                .verifyComplete();
    }

}

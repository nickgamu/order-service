package zw.co.pabuwe.polarbookshop.orderservice.book.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import zw.co.pabuwe.polarbookshop.orderservice.order.domain.Order;
import zw.co.pabuwe.polarbookshop.orderservice.order.domain.OrderService;
import zw.co.pabuwe.polarbookshop.orderservice.order.domain.OrderStatus;
import zw.co.pabuwe.polarbookshop.orderservice.web.OrderController;
import zw.co.pabuwe.polarbookshop.orderservice.web.OrderRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebFluxTest(OrderController.class)
class OrderControllerWebFluxTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private OrderService orderService;

    @Test
    void whenBookNotAvailableThenRejectOrder() {
        var orderRequest = new OrderRequest("1234567890", 3);
        var expectedOrder = OrderService.buildRejectedOrder(orderRequest.isbn(), orderRequest.quantity());

        given(orderService.submitOrder(orderRequest.isbn(), orderRequest.quantity()))
                .willReturn(Mono.just(expectedOrder));

        webTestClient
                .post()
                .uri("/orders/")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Order.class).value(actualOrder -> {
                    assertThat(actualOrder).isNotNull();
                    assertThat(actualOrder.status()).isEqualTo(OrderStatus.REJECTED);
                });
    }

}

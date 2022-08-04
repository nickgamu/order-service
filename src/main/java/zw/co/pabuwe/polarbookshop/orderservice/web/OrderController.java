package zw.co.pabuwe.polarbookshop.orderservice.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import zw.co.pabuwe.polarbookshop.orderservice.order.domain.Order;
import zw.co.pabuwe.polarbookshop.orderservice.order.domain.OrderService;

import javax.validation.Valid;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public Flux<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping
    public Mono<Order> submitOrder(@Valid @RequestBody OrderRequest orderRequest) {
        return orderService.submitOrder(
                orderRequest.isbn(), orderRequest.quantity()
        );
    }

}

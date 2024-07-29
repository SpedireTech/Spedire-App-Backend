package com.spedire.Spedire.services.payment;

import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.repositories.UserRepository;
import com.spedire.Spedire.services.order.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@Component
public class PaymentUtils {

    private static final Logger logger = LoggerFactory.getLogger(PaymentUtils.class);

    private final UserRepository userRepository;

    private final OrderService orderService;

    public PaymentUtils(UserRepository userRepository, OrderService orderService) {
        this.userRepository = userRepository;
        this.orderService = orderService;
    }

    public void validateUserInTheContext(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new SpedireException("Not a valid user");
        }
    }

    public Optional<Order> validateAndFindOrderId(String orderId) {
        return orderService.findOrderById(orderId);
    }

//    @Scheduled(fixedRate = 86400000)
//    @Scheduled(fixedRate = 300000)
    public void cleanUpPendingPayments() {
        logger.info("Running cron job to clean up pending payments.");
        List<Order> pendingOrders = orderService.findOrdersByPaymentStatus("PENDING");
        System.out.println(pendingOrders);
        for (Order order : pendingOrders) {
            if (order.getOrderPayment().getInitiatedAt().isBefore(LocalDateTime.now().minusMinutes(24))) {
                orderService.deleteOrder(order);
                logger.info("Deleted order with ID {} due to pending payment for over 24 hours.", order.getId());
            }
        }
    }


}

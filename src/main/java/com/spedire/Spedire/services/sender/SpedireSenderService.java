package com.spedire.Spedire.services.sender;

import com.spedire.Spedire.dtos.requests.CreateOrderRequest;
import com.spedire.Spedire.dtos.requests.SelectCarrierRequest;
import com.spedire.Spedire.enums.OrderType;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.MatchedOrder;
import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.models.SenderPool;
import com.spedire.Spedire.repositories.MatchedOrderRepository;
import com.spedire.Spedire.repositories.SenderPoolRepository;
import com.spedire.Spedire.services.carrier.CarrierService;
import com.spedire.Spedire.services.email.JavaMailService;
import com.spedire.Spedire.services.order.OrderService;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.spedire.Spedire.services.email.MailTemplates.getSelectCourierMailTemplate;

@Service
public class SpedireSenderService implements SenderService {

    private final SenderPoolRepository senderPoolRepository;
    private final JavaMailService javaMailService;

    private final OrderService orderService;

    private final MatchedOrderRepository matchedOrderRepository;
    private final ModelMapper modelMapper;


    public SpedireSenderService(SenderPoolRepository senderPoolRepository, ModelMapper modelMapper,
                                JavaMailService javaMailService, MatchedOrderRepository matchedOrderRepository, OrderService orderService) {
        this.senderPoolRepository = senderPoolRepository;
        this.modelMapper = modelMapper;
        this.javaMailService = javaMailService;
        this.matchedOrderRepository = matchedOrderRepository;
        this.orderService = orderService;
    }

    @Override
    public List<Object> findMatch(String orderId, CarrierService carrierService) throws Exception {
        SenderPool foundSentItemObject = senderPoolRepository.findById(orderId).orElseThrow(() -> new SpedireException("Order not found"));
        return carrierService.matchOrderRequest(foundSentItemObject.getSenderLocation(), foundSentItemObject.getSenderTown(), orderId);
    }

    @Override
    public void saveSenderRequestInAPool(CreateOrderRequest createOrderRequest, String fullName, String senderId) {
        SenderPool senderPool = modelMapper.map(createOrderRequest, SenderPool.class);
        senderPool.setSenderName(fullName);
        senderPool.setSenderId(senderId);
        senderPool.setCreatedAt(LocalDateTime.now());
        senderPoolRepository.save(senderPool);
    }

    @Override
    @Transactional
    public Object selectCarrier(SelectCarrierRequest request) throws MessagingException {
        MatchedOrder matchedOrder = matchedOrderRepository.findById(request.getOrderId()).orElseThrow(() -> new SpedireException("Order not found"));
        javaMailService.sendMail(request.getEmail(), "You have been selected", getSelectCourierMailTemplate("https://spedire.netlify.app/login"));
        Order order = orderService.findOrderById(matchedOrder.getOrderId()).orElseThrow(() -> new SpedireException("Order not found"));
        order.setOrderType(OrderType.FOUND_MATCH);
        orderService.saveOrder(order);
        matchedOrderRepository.deleteById(matchedOrder.getId());
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("senderName", order.getSenderName()); map.put("referenceId", order.getId());
        map.put("orderName", order.getItemName());
        return map;
    }

    @Override
    public List<SenderPool> findOrderBySenderTown(String carrierTown) {
        return senderPoolRepository.findOrderBySenderTown(carrierTown);
    }


}

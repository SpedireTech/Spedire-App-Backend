package com.spedire.Spedire.services.sender;

import com.spedire.Spedire.dtos.requests.CreateOrderRequest;
import com.spedire.Spedire.dtos.requests.SelectCarrierRequest;
import com.spedire.Spedire.enums.OrderStatus;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.MatchedOrder;
import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.models.SenderPool;
import com.spedire.Spedire.models.User;
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

    private final MatchedOrderRepository matchedOrderRepository;
    private final ModelMapper modelMapper;


    public SpedireSenderService(SenderPoolRepository senderPoolRepository, ModelMapper modelMapper,
                                JavaMailService javaMailService, MatchedOrderRepository matchedOrderRepository) {
        this.senderPoolRepository = senderPoolRepository;
        this.modelMapper = modelMapper;
        this.javaMailService = javaMailService;
        this.matchedOrderRepository = matchedOrderRepository;
    }

    @Override
    public void saveSenderRequestInAPool(CreateOrderRequest createOrderRequest, User user, String orderId) {
        SenderPool senderPool = modelMapper.map(createOrderRequest, SenderPool.class);
        senderPool.setSenderName(user.getFullName());
        senderPool.setSenderId(user.getId());
        senderPool.setOrderId(orderId);
        senderPool.setCreatedAt(LocalDateTime.now());
        senderPoolRepository.save(senderPool);
    }



    @Override
    public List<SenderPool> findOrderBySenderTown(String carrierTown) {
        return senderPoolRepository.findOrderBySenderTown(carrierTown);
    }


}

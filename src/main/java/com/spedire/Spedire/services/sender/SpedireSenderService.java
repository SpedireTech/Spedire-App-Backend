package com.spedire.Spedire.services.sender;

import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.models.SenderPool;
import com.spedire.Spedire.repositories.SenderPoolRepository;
import com.spedire.Spedire.services.email.JavaMailService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpedireSenderService implements SenderService {

    private final SenderPoolRepository senderPoolRepository;
    private final JavaMailService javaMailService;

    private final ModelMapper modelMapper;


    public SpedireSenderService(SenderPoolRepository senderPoolRepository, ModelMapper modelMapper,
                                JavaMailService javaMailService) {
        this.senderPoolRepository = senderPoolRepository;
        this.modelMapper = modelMapper;
        this.javaMailService = javaMailService;
    }

    @Override
    public void saveSenderRequestInAPool(Order order) {
        SenderPool senderPool = SenderPool.builder().order(order).build();
        senderPoolRepository.save(senderPool);
    }



    @Override
    public List<SenderPool> findOrderBySenderTown(String town) {
        return senderPoolRepository.findSenderPoolByOrder_SenderTown(town);
    }

    @Override
    public Optional<SenderPool> findById(String orderId) {
        return senderPoolRepository.findSenderPoolByOrder_Id(orderId);

    }


}

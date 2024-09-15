package com.spedire.Spedire.services.order.AcceptedORder;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.spedire.Spedire.dtos.responses.AcceptedOrderResponse;
import com.spedire.Spedire.dtos.responses.AcceptedOrderResponseForSender;
import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.security.JwtUtil;
import jakarta.validation.constraints.AssertFalse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AcceptedOrderUtils {

    private final JwtUtil jwtUtil;


    public static AcceptedOrderResponse convertOrderToOrderResponseForCarrier(Order foundOrder) {
        return AcceptedOrderResponse.builder()
                .senderName(foundOrder.getSenderName())
                .senderPhoneNumber(foundOrder.getSenderPhoneNumber())
                .senderPossibleLocations(foundOrder.getSenderLocation())
                .receiverName(foundOrder.getReceiverName())
                .receiverPhoneNumber(foundOrder.getReceiverPhoneNumber())
                .receiverLocation(foundOrder.getReceiverLocation())
                .itemDescription(foundOrder.getItemDescription())
                .image(foundOrder.getPicture())
                .orderId(foundOrder.getId())
                .build();
    }

    public static AcceptedOrderResponseForSender convertOrderToOrderResponseForSender(Order foundOrder) {
        return AcceptedOrderResponseForSender.builder().carrierImage(foundOrder.getCarrierImage())
                .carrierPhoneNumber(foundOrder.getCarrierPhoneNumber())
                .carrierName(foundOrder.getCarrierFullName())
                .itemDescription(foundOrder.getItemDescription())
                .costOfDelivery(String.valueOf(foundOrder.getItemValue())).build();
    }

    public DecodedJWT extractTokenDetails(String token) {
        String splitToken = token.split(" ")[1];
        return jwtUtil.verifyToken(splitToken);
    }






}

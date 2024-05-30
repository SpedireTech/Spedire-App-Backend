package com.spedire.Spedire.services.order;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.spedire.Spedire.dtos.responses.OrderListDtoResponse;
import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.security.JwtUtil;

public class OrderUtils {

    private static JwtUtil jwtUtil;
    public static String getSenderIdFromIncomingToken(String incomingToken) {
        String token = incomingToken.split(" ")[1];
        DecodedJWT decodedJWT = jwtUtil.verifyToken(token);
        return decodedJWT.getClaim("userId").asString();
    }

    public static OrderListDtoResponse convertFromOrderToOrderListDto(Order order){
        return   OrderListDtoResponse.builder()
                .orderId(order.getId())
                .image(order.getPicture())
                .senderName(order.getSenderName())
                .costOfDelivery("950.00").senderName(order.getSenderPhoneNumber())
                .itemDescription(order.getItemDescription())
                .senderLocation(order.getSenderLocation())
                .receiverLocation(order.getReceiverLocation())
                .build();
    }

}

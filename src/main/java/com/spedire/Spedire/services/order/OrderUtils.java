package com.spedire.Spedire.services.order;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.spedire.Spedire.dtos.responses.CarrierListDtoResponse;
import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.models.SenderPool;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.security.JwtUtil;
import com.spedire.Spedire.services.location.mapBox.MapBoxService;
import com.spedire.Spedire.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
@AllArgsConstructor
public class OrderUtils {

    private final MapBoxService mapBoxService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public CarrierListDtoResponse convertFromOrderToOrderListDto(SenderPool order, String carrierLocation) throws Exception {
        User user = userService.findById(order.getSenderId());
        String minutesAway = mapBoxService.getMinutesAway(order.getSenderLocation(), carrierLocation);
        return   CarrierListDtoResponse.builder().orderId(order.getId()).image(order.getPicture())
                .senderName(user.getFullName()).minutesAway(minutesAway).senderPhoneNumber(user.getPhoneNumber())
                .pickUpNote(order.getPickUpNote()).dropOffNote(order.getDropOffNote()).senderLocation(order.getSenderLocation())
                .receiverLocation(order.getReceiverLocation()).build();
    }

    public DecodedJWT extractTokenDetails(String token) {
        String splitToken = token.split(" ")[1];
        return jwtUtil.verifyToken(splitToken);
    }


}

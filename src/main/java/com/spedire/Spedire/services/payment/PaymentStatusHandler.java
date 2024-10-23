//package com.spedire.Spedire.services.payment;
//
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//

//@Component
//public class PaymentStatusHandler extends TextWebSocketHandler {
//
//    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) {
//        System.out.println("Connected to WebSocket");
//        sessions.put(session.getId(), session);
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
//        sessions.remove(session.getId());
//    }
//
//    public void sendPaymentStatusUpdate(String message) throws Exception {
//        System.out.println("WebSocket connection closed");
//        System.out.println("Message here == " + message);
//        for (WebSocketSession session : sessions.values()) {
//            if (session.isOpen()) {
//                session.sendMessage(new TextMessage(message));
//            }
//        }
//    }
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        System.out.println("Received message: " + message.getPayload());
//        session.sendMessage(new TextMessage("Message received: " + message.getPayload()));
//    }
//
//}

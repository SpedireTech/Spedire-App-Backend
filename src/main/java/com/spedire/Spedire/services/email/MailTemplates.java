package com.spedire.Spedire.services.email;

import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.Order;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class MailTemplates {

    public static String getWelcomeMailTemplate(String name) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ClassPathResource("static/welcome.jsp").getInputStream()))) {
            String template = bufferedReader.lines().collect(Collectors.joining());
            template = template.replace("{name}", name);
            return template;
        } catch (IOException exception) {
            throw new SpedireException("Fail to send mail");
        }
    }

    public static String getForgotPasswordMailTemplate(String name, String token)  {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ClassPathResource("static/forgotPassword.jsp").getInputStream()))) {
            String template = bufferedReader.lines().collect(Collectors.joining());

            template = template.replace("{name}", name);
            template = template.replace("{link}", "<a href='" + token + "'>Reset Password</a>");

            return template;
        } catch (IOException exception) {
            throw new SpedireException("Fail to send mail");
        }
    }


    @SneakyThrows
    public static String getSelectCourierMailTemplate(String link)  {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ClassPathResource("static/selectCourier.jsp").getInputStream()))) {
            String template = bufferedReader.lines().collect(Collectors.joining());

            template = template.replace("{link}", "<a href='" + link + "'>Please Login Here</a>");

            return template;
        } catch (IOException exception) {
            throw new SpedireException("Fail to send mail");
        }
    }

    public static String sendItemMailTemplate(Order order) {
        String brandColor = "#FF5733";
        return "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f9f9f9; color: #333; padding: 20px; }" +
                ".header { background-color: " + brandColor + "; color: white; padding: 10px; text-align: center; }" +
                ".order-info { margin: 20px 0; padding: 15px; border: 1px solid #ddd; border-radius: 5px; background-color: white; }" +
                ".footer { margin-top: 20px; text-align: center; font-size: 12px; color: #777; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='header'>" +
                "<h1>Order Received</h1>" +
                "</div>" +
                "<p>Hey there!</p>" +
                "<p>Your order request has been received and is being matched.</p>" +
                "<p>Your Reference ID: <strong>" + order.getId() + "</strong></p>" +
                "<div class='order-info'>" +
                "<h3>Order Information:</h3>" +
                "<p><strong>Item Name:</strong> " + order.getItemName() + "</p>" +
                "<p><strong>Sender Name:</strong> " + order.getSenderName() + "</p>" +
                "<p><strong>Receiver Name:</strong> " + order.getReceiverName() + "</p>" +
                "<p><strong>Due Date:</strong> " + order.getDueDate() + "</p>" +
                "<p><strong>Due Time:</strong> " + order.getDueDate() + "</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Thank you for choosing our service!</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    public static String matchFoundTemplate(int numberOfDeliveries, String dashboardUrl, String orderId) {
        String template = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #FFFFFF; /* White */
                        color: #0B56B8; /* Blue */
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #FFFFFF; /* White */
                        border: 1px solid #D5D5D5; /* Off-White/Grey */
                    }
                    h1 {
                        color: #0B56B8; /* Blue */
                    }
                    p {
                        color: #0B56B8; /* Blue */
                        font-size: 16px;
                        line-height: 1.6;
                    }
                    .button {
                        display: inline-block;
                        background-color: #371A5F; /* Purple */
                        color: #FFFFFF; /* White */
                        padding: 12px 25px;
                        text-decoration: none;
                        border-radius: 5px;
                        font-size: 16px;
                        margin-top: 20px;
                    }
                    .footer {
                        margin-top: 20px;
                        color: #0B56B8; /* Blue */
                        font-size: 14px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Delivery Orders Found</h1>
                    <p>Hello,</p>
                    <p>We have found <strong>%d</strong> order matching your location. You can now proceed to your dashboard to view and manage these orders.</p>
                    <p>Click the button below to continue:</p>
                    <a href="%s" class="button">Go to Dashboard</a>
                    <p><strong>Reference ID:</strong> %s</p>
                    <div class="footer">
                        <p>Thank you for trusting us!</p>
                    </div>
                </div>
            </body>
            </html>
            """;
        return String.format(template, numberOfDeliveries, dashboardUrl, orderId);
    }


    public static String noMatchFoundTemplate(String referenceId) {
        return String.format("""
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background-color: #FFFFFF; /* White */
                    color: #0B56B8; /* Blue */
                    margin: 0;
                    padding: 0;
                }
                .container {
                    max-width: 600px;
                    margin: 0 auto;
                    padding: 20px;
                    background-color: #FFFFFF; /* White */
                    border: 1px solid #D5D5D5; /* Off-White/Grey */
                }
                h1 {
                    color: #0B56B8; /* Blue */
                }
                p {
                    color: #0B56B8; /* Blue */
                    font-size: 16px;
                    line-height: 1.6;
                }
                .footer {
                    margin-top: 20px;
                    color: #0B56B8; /* Blue */
                    font-size: 14px;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <h1>No Match Found for Your Request</h1>
                <p>Hello,</p>
                <p>Thank you for submitting your delivery request. We are currently searching for orders that match your request.</p>
                <p>Please hold on while we process your request and notify you once a match is found.</p>
                <p><strong>Reference ID:</strong> %s</p>
                <div class="footer">
                    <p>We appreciate your patience and thank you for trusting us with your delivery needs.</p>
                    <p>Best regards,</p>
                    <p><strong>Spedire</strong></p>
                </div>
            </div>
        </body>
        </html>
        """, referenceId);
    }



}

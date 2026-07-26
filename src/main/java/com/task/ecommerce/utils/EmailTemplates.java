package com.task.ecommerce.utils;

import java.math.BigDecimal;

public class EmailTemplates {

    private EmailTemplates() {}

    public static String orderStatusChanged(String customerName, Integer orderId, String newStatus) {
        return """
            <div style="font-family: Arial, sans-serif;">
                <h2>Order Update</h2>
                <p>Hi %s,</p>
                <p>Your order <strong>#%d</strong> status has been updated to: <strong>%s</strong></p>
            </div>
            """.formatted(customerName, orderId, newStatus);
    }

    public static String paymentSuccess(String customerName, Integer orderId, BigDecimal amount) {
        return """
            <div style="font-family: Arial, sans-serif;">
                <h2>Payment Successful</h2>
                <p>Hi %s,</p>
                <p>We received your payment of <strong>%s EGP</strong> for order <strong>#%d</strong>.</p>
                <p>Your invoice will be sent to you shortly.</p>
            </div>
            """.formatted(customerName, amount.toPlainString(), orderId);
    }

    public static String paymentFailed(String customerName, Integer orderId) {
        return """
            <div style="font-family: Arial, sans-serif;">
                <h2>Payment Failed</h2>
                <p>Hi %s,</p>
                <p>Unfortunately, your payment for order <strong>#%d</strong> did not go through.</p>
                <p>Please try again from your order page.</p>
            </div>
            """.formatted(customerName, orderId);
    }

    public static String adminAccountCreated(String email, String temporaryPassword) {
        return """
            <div style="font-family: Arial, sans-serif;">
                <h2>Welcome, Admin</h2>
                <p>An admin account has been created for you.</p>
                <p><strong>Email:</strong> %s</p>
                <p><strong>Temporary Password:</strong> %s</p>
                <p>Please log in and change your password immediately.</p>
            </div>
            """.formatted(email, temporaryPassword);
    }
}
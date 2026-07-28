package com.task.ecommerce.payment;

import com.task.ecommerce.config.properties.PaymobPropertiesConfig;
import com.task.ecommerce.exception.BadRequestException;

import com.task.ecommerce.payment.dto.TransactionWrapperDto;
import com.task.ecommerce.utils.HmacUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymobHmacService {

    private final PaymobPropertiesConfig properties;

    public void verifyAndExtract(
            TransactionWrapperDto request,
            String receivedHmac) {

        String calculated = calculateHmac(request);

        if (receivedHmac == null || !MessageDigest.isEqual(
                calculated.getBytes(StandardCharsets.US_ASCII),
                receivedHmac.toLowerCase().getBytes(StandardCharsets.US_ASCII))) {
            throw new BadRequestException("Invalid Paymob HMAC.");
        }
    }

    private String calculateHmac(TransactionWrapperDto request) {

        TransactionWrapperDto.PaymentTransactionDto obj = request.getObj();

        String payload =
                value(obj.getAmountCents()) +
                        value(obj.getCreatedAt()) +
                        value(obj.getCurrency()) +
                        value(obj.getErrorOccurred()) +
                        value(obj.getHasParentTransaction()) +
                        value(obj.getId()) +
                        value(obj.getIntegrationId()) +
                        value(obj.getIs3dSecure()) +
                        value(obj.getIsAuth()) +
                        value(obj.getIsCapture()) +
                        value(obj.getIsRefunded()) +
                        value(obj.getIsStandalonePayment()) +
                        value(obj.getIsVoided()) +
                        value(obj.getOrder().getId()) +
                        value(obj.getOwner()) +
                        value(obj.getPending()) +
                        value(obj.getSourceData().getPan()) +
                        value(obj.getSourceData().getSubType()) +
                        value(obj.getSourceData().getType()) +
                        value(obj.getSuccess());
        return HmacUtil.hmacSha512(payload, properties.getHmac());
    }

    private String value(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}

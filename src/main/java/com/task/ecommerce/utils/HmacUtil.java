package com.task.ecommerce.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

public final class HmacUtil {
    private HmacUtil() {
    }

    public static String hmacSha512(String data, String secret) {

        try {

            Mac mac = Mac.getInstance("HmacSHA512");

            SecretKeySpec key =
                    new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                            "HmacSHA512");

            mac.init(key);

            byte[] hash =
                    mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to calculate HMAC-SHA512.", exception);
        }
    }
}

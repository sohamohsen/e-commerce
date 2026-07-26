package com.task.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingDataDto {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("street")
    private String street;

    @JsonProperty("building")
    private String building;

    @JsonProperty("floor")
    private String floor;

    @JsonProperty("apartment")
    private String apartment;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("country")
    private String country;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("postal_code")
    private String postalCode;

    @JsonProperty("extra_description")
    private String extraDescription;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("order")
    private Long order;
}

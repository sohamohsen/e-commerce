package com.task.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingData {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String email;

    @JsonProperty("phone_number")
    private String phone;

    private String country;

    private String city;

    private String street;

    @JsonProperty("building")
    private String building;

    @JsonProperty("floor")
    private String floor;

    @JsonProperty("apartment")
    private String apartment;

    @JsonProperty("postal_code")
    private String postalCode;
}
package com.task.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantDto {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("phones")
    private List<String> phones;

    @JsonProperty("company_emails")
    private List<String> companyEmails;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("state")
    private String state;

    @JsonProperty("country")
    private String country;

    @JsonProperty("city")
    private String city;

    @JsonProperty("postal_code")
    private String postalCode;

    @JsonProperty("street")
    private String street;
}

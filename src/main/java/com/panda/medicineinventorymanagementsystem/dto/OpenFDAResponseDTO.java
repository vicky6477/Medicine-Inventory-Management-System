package com.panda.medicineinventorymanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenFDAResponseDTO {
    @JsonProperty("results")
    private List<Result> results;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        @JsonProperty("openfda")
        private OpenFDA openFDA;

        @JsonProperty("description")
        private List<String> description;

        @JsonProperty("purpose")
        private List<String> purpose;

        @JsonProperty("contraindications")
        private List<String> contraindications;

        @JsonProperty("indications_and_usage")
        private List<String> indicationsAndUsage;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OpenFDA {
        @JsonProperty("brand_name")
        private List<String> brandName;
    }
}

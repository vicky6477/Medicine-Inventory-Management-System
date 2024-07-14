package com.panda.medicineinventorymanagementsystem.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.medicineinventorymanagementsystem.dto.MedicineDTO;
import com.panda.medicineinventorymanagementsystem.dto.OpenFDAResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import jakarta.validation.Valid;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class OpenFDAApiService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.key}")
    private String apiKey;

    /**
     * Constructs an OpenFDAApiService with necessary dependencies.
     * @param restTemplate the RestTemplate to handle HTTP requests
     * @param objectMapper the ObjectMapper to parse JSON responses
     */
    public OpenFDAApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Fetches data for a specific medicine by its name from the OpenFDA API.
     * @param name the name of the medicine to search for
     * @param defaultMedicineDTO the default MedicineDTO to use if no data is found
     * @return an Optional containing a filled MedicineDTO if the API call is successful and data is found, otherwise, an empty Optional
     */
    public Optional<MedicineDTO> fetchMedicineData(String name, @Valid MedicineDTO defaultMedicineDTO) {
        try {//fetch from openFDA
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
            String url = "https://api.fda.gov/drug/label.json?api_key=" + apiKey + "&search=" + encodedName;
            String result = restTemplate.getForObject(url, String.class);
            OpenFDAResponseDTO response = objectMapper.readValue(result, OpenFDAResponseDTO.class);

            //Parses the JSON result from the FDA to find a matching medicine
            if (response.getResults() != null && !response.getResults().isEmpty()) {
                return response.getResults().stream()
                        .filter(details -> details.getOpenFDA() != null && details.getOpenFDA().getBrandName() != null &&
                                details.getOpenFDA().getBrandName().stream().anyMatch(brand -> brand.equalsIgnoreCase(name)))
                        .findFirst()
                        .map(details -> parseMedicine(details, defaultMedicineDTO));
            }
        } catch (Exception e) {
            System.err.println("API call failed: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.of(defaultMedicineDTO);
    }


    /**
     * Parses the medicine details from the JSON object and updates the provided MedicineDTO.
     * @param details the JSON object containing the medicine details
     * @param medicineDTO the MedicineDTO to populate
     * @return the populated MedicineDTO
     */
    private MedicineDTO parseMedicine(OpenFDAResponseDTO.Result details, MedicineDTO medicineDTO) {
        String description = findBestMatch(details.getDescription());
        String purpose = findBestMatch(details.getPurpose());
        String contraindications = findBestMatch(details.getContraindications());
        String indicationsAndUsage = findBestMatch(details.getIndicationsAndUsage());

        List<String> parts = Arrays.asList(description, purpose, contraindications, indicationsAndUsage);
        String finalDescription = parts.stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(". "));

        if (finalDescription.length() > 255) {
            finalDescription = finalDescription.substring(0, 255);
        }

        if (finalDescription.isEmpty()) {
            finalDescription = "Default description";
        }

        medicineDTO.setDescription(finalDescription);
        return medicineDTO;
    }


    /**
     * Finds the best matching string in a list of strings.
     * @param list the list of strings to search through
     * @return the first non-empty string if available, otherwise null
     */
    private String findBestMatch(List<String> list) {
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

}

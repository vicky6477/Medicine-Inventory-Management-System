package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.dto.MedicineDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import jakarta.validation.Valid;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OpenFDAApiService {
    private final RestTemplate restTemplate;
    @Value("${api.key}")
    private String apiKey;

    public OpenFDAApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Service method to fetch medicine data from openFDA
    public Optional<MedicineDTO> fetchMedicineData(String name, @Valid MedicineDTO defaultMedicineDTO) {
        try {
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
            String url = "https://api.fda.gov/drug/label.json?api_key=" + apiKey + "&search=" + encodedName;
            String result = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(result);

            if (json.has("results")) {
                JSONArray results = json.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject details = results.getJSONObject(i);
                    JSONObject openfda = details.optJSONObject("openfda");
                    if (openfda != null && openfda.has("brand_name")) {
                        JSONArray brandNames = openfda.getJSONArray("brand_name");
                        for (int j = 0; j < brandNames.length(); j++) {
                            if (brandNames.getString(j).equalsIgnoreCase(name)) {
                                return Optional.of(parseMedicine(details, defaultMedicineDTO));
                            }
                        }
                    }
                }
                System.out.println("No matching brand name found for: " + name);
            } else {
                System.out.println("No results found for: " + name);
            }
        } catch (Exception e) {
            System.err.println("API call failed: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private MedicineDTO parseMedicine(JSONObject details, MedicineDTO medicineDTO) {
        try {
            String description = findBestMatch(details, "description");
            String purpose = findBestMatch(details, "purpose");
            String contraindications = findBestMatch(details, "contraindications");

            String finalDescription = Stream.of(contraindications, description, purpose)
                    .filter(s -> s != null && !s.isEmpty())
                    .collect(Collectors.joining(" "));

            if (finalDescription.length() > 255) {
                finalDescription = finalDescription.substring(0, 255);
            }

            medicineDTO.setDescription(finalDescription.isEmpty() ? "Default description" : finalDescription);
        } catch (Exception e) {
            System.err.println("Error parsing medicine details: " + e.getMessage());
            medicineDTO.setDescription("Default description due to error in parsing details.");
        }
        return medicineDTO;
    }

    private String findBestMatch(JSONObject details, String key) {
        try {
            if (!details.has(key) || details.get(key) == JSONObject.NULL) return "";
            JSONArray arr = details.getJSONArray(key);
            for (int i = 0; i < arr.length(); i++) {
                String item = arr.optString(i, "");
                if (!item.isEmpty()) return item;
            }
        } catch (JSONException e) {
            System.err.println("Error accessing JSON data for key: " + key + ", error: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }
}

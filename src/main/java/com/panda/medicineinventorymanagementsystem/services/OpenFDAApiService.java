package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import jakarta.validation.Valid;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
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
    public Optional<Medicine> fetchMedicineData(String name, @Valid Medicine defaultMedicine) {
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
                                return Optional.of(parseMedicine(details, defaultMedicine));
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

    private Medicine parseMedicine(JSONObject details, Medicine medicine) {
        try {
            String description = findBestMatch(details, "description");
            String purpose = findBestMatch(details, "purpose");
            String contraindications = findBestMatch(details, "contraindications");

            String finalDescription = Stream.of(contraindications, description, purpose)
                    .filter(s -> s != null && !s.isEmpty())
                    .collect(Collectors.joining(" "));

            if (finalDescription.length() > 1000) {
                finalDescription = finalDescription.substring(0, 1000);
            }

            medicine.setDescription(finalDescription.isEmpty() ? "Default description" : finalDescription);
        } catch (Exception e) {
            System.err.println("Error parsing medicine details: " + e.getMessage());
            medicine.setDescription("Default description due to error in parsing details.");
        }
        return medicine;
    }



//    private String findBestMatchRecursive(JSONObject jsonObject, String key) {
//        if(jsonObject.has(key)) {
//            return jsonObject.get(key).toString();
//        }
//        for (String k : jsonObject.keySet()) {
//            Object child = jsonObject.get(k);
//            if (child instanceof JSONObject) {
//                String result = findBestMatchRecursive((JSONObject) child, key);
//                if (!result.isEmpty()) return result;
//            }
//        }
//        return "";
//    }

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




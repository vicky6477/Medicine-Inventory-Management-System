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

    /**
     * Constructor for initializing the OpenFDAApiService with a RestTemplate.
     * @param restTemplate the RestTemplate used for making HTTP requests.
     */
    public OpenFDAApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches medicine data from the openFDA API based on the provided medicine name and create medicine.
     * @param name the name of the medicine to search for.
     * @param defaultMedicineDTO the default MedicineDTO to use if parsing succeeds.
     * @return an Optional containing a filled MedicineDTO if the API call is successful and data is found; otherwise, an empty Optional.
     */
    public Optional<MedicineDTO> fetchMedicineData(String name, @Valid MedicineDTO defaultMedicineDTO) {
        try {//fetch from openFDA
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
            String url = "https://api.fda.gov/drug/label.json?api_key=" + apiKey + "&search=" + encodedName;
            String result = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(result);

            //Parses the JSON result from the FDA to find a matching medicine
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

    /**
     * Parses medicine details from JSON and updates the provided MedicineDTO.
     * @param details the JSON object containing the medicine details.
     * @param medicineDTO the MedicineDTO to populate.
     * @return the populated MedicineDTO.
     */
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


    /**
     * Finds the best matching string in a JSON object for a given key.
     * @param details the JSON object containing the data.
     * @param key the key for which to find the data.
     * @return the best matching string or an empty string if not found.
     */
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

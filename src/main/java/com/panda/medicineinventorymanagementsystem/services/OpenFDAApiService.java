package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.entity.Type;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.beans.factory.annotation.Value;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class OpenFDAApiService {
    private final RestTemplate restTemplate;
    @Value("${api.key}")
    private String apiKey;

    public OpenFDAApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<Medicine> fetchMedicineData(String name, Medicine medicine) {
        try {
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
            String url = "https://api.fda.gov/drug/label.json?api_key=" + apiKey + "&search=" + encodedName;
            String result = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(result);
            if (!json.isNull("results")) {
                JSONObject details = json.getJSONArray("results").getJSONObject(0);
                return Optional.of(parseMedicine(details, medicine));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }



    private Medicine parseMedicine(JSONObject details, Medicine medicine) {
        JSONArray purposeArray = details.optJSONArray("purpose");
        String purpose = (purposeArray != null && purposeArray.length() > 0) ? purposeArray.getString(0) : "";
        JSONArray whenUsingArray = details.optJSONArray("when_using");
        String whenUsing = (whenUsingArray != null && whenUsingArray.length() > 0) ? whenUsingArray.getString(0) : "";
        medicine.setDescription(purpose + ". " + whenUsing);

        return medicine;
    }


}



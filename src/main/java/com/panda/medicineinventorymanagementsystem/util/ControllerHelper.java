package com.panda.medicineinventorymanagementsystem.util;

import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;

public class ControllerHelper {
    public static Map<String, String> formatBindingErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }
}

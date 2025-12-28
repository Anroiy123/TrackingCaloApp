package com.example.trackingcaloapp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Model chứa kết quả validation cho form inputs.
 */
public class ValidationResult {
    private boolean isValid;
    private final Map<String, String> fieldErrors;

    public ValidationResult() {
        this.isValid = true;
        this.fieldErrors = new HashMap<>();
    }

    public void addError(String fieldName, String errorMessage) {
        fieldErrors.put(fieldName, errorMessage);
        isValid = false;
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean hasError(String fieldName) {
        return fieldErrors.containsKey(fieldName);
    }

    public String getError(String fieldName) {
        return fieldErrors.get(fieldName);
    }

    public Map<String, String> getAllErrors() {
        return new HashMap<>(fieldErrors);
    }

    // Static factory methods for common validations
    public static ValidationResult valid() {
        return new ValidationResult();
    }

    public static ValidationResult invalid(String fieldName, String errorMessage) {
        ValidationResult result = new ValidationResult();
        result.addError(fieldName, errorMessage);
        return result;
    }

    // Field name constants
    public static final String FIELD_NAME = "name";
    public static final String FIELD_AGE = "age";
    public static final String FIELD_HEIGHT = "height";
    public static final String FIELD_WEIGHT = "weight";
    public static final String FIELD_CALORIE_GOAL = "calorieGoal";
}

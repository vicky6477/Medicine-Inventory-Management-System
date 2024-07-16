package com.panda.medicineinventorymanagementsystem.exception;

public class MedicineAlreadyExistsException extends RuntimeException {

    public MedicineAlreadyExistsException(String message) {
        super(message);
    }

    public MedicineAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.panda.medicineinventorymanagementsystem.entity;

public enum Type {
    PRES, OTC, OTHER;
    public static boolean isValidType(String typeStr) {
        for (Type type : Type.values()) {
            if (type.name().equalsIgnoreCase(typeStr)) {
                return true;
            }
        }
        return false;
    }
}

package com.panda.medicineinventorymanagementsystem.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MedicineDTO {

    private Integer id;
    @NotBlank(message = "Name must not be blank")
    private String name;
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    private Integer quantity;
    @NotBlank(message = "Type must not be blank")
    private String type;

    /**
     * 获取
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * 设置
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取
     * @return quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * 设置
     * @param quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * 获取
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * 设置
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

}

package com.example.ksl.wsd2025.api.response;

public class ItemResponse<T> {

    private String status;
    private T data;

    public ItemResponse() {
    }

    public ItemResponse(String status, T data) {
        this.status = status;
        this.data = data;
    }

    public static <T> ItemResponse<T> success(T data) {
        return new ItemResponse<>("success", data);
    }

    public static <T> ItemResponse<T> error(T data) {
        return new ItemResponse<>("error", data);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
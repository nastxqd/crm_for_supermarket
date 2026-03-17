package com.shop.shop.data.repositories;

import lombok.Data;

@Data
public class DataBaseAnswer {
    private String message;
    public DataBaseAnswer(String message){
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
}

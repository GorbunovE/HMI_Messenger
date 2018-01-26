package com.firebase_info.firebaseandroidchat;

public class Message {
    // поле
    private String message;
    private String user;
    private String to;

    // пустой конструктор (необходим для работы базы данных Firebase)
    public Message() {
    }

    // конструктор, используемый нами
    public Message(String message, String user, String to) {
        this.message = message;
        this.user= user;
        this.to = to;
    }

    // геттер
    public String getText() {
        return message;
    }
    public String getUser(){
        return user;
    }
    public String getTo(){
        return to;
    }

    // сеттер
    public void setText(String message) {
        this.message = message;
    }
}



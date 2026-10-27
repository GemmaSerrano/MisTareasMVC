package com.example.miappnivel4;


//Clase que utilizamos para la lista DummyJson
public class Todo {

    private int id;
    private String todo;
    private boolean completed;
    private int userId;

    public Todo() {
    }

    public Todo(String todo, boolean completed, int userId) {
        this.todo = todo;
        this.completed = completed;
        this.userId = userId;
    }

    public String getTodo() {
        return todo;
    }
}
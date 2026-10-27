package com.example.miappnivel4;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @GET("todos")
    Call<TodosResponse> getTasks();

    @POST("todos/add")
    Call<Todo> addTask(@Body Todo task);
}

package com.example.a1stapp.Fragments;

import com.example.a1stapp.Notifications.MyResponse;
import com.example.a1stapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAANSCBBdM:APA91bEWDKkOA1PY3ICC43WKS1Q67j_HUxIq_AbzLI2ozlMLF39FoOcTmavOLXqYn5hze9qFVfkPnGOffZQ3VsonUtqGPp5F-WKpO0HRJxq44xchLoJ1srDjD_K4KK6vYTctkChM0JSW"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
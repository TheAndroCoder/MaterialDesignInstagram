package com.example.instamaterial.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization: key=AAAAniThTNk:APA91bF3HG_zwLiajcam8nay3UVjOKJdJeh56svqknhMZ87FErVaio2TEbAtbuUSE2j2hCGEQY_z0sVRG7a1pUEkaIx43YOnJg1yXKFs6S3K_SDzNI3ZYw45-XVcKT1zz2qrZK2OsSsq"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}

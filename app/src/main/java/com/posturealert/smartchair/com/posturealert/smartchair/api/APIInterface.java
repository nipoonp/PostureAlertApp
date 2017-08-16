package com.posturealert.smartchair.com.posturealert.smartchair.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Nipoon on 21/07/2017.
 */

public interface APIInterface {
    @GET("/sensorReadings")
    Call<APIReturn> getSensorReadingsAPI();

    @GET("/dashBoardPieChart/{userID}")
    Call<APIReturn> getDashBoardPitChart(@Path("userID") String userID);

    @GET("/userInfo/{userID}")
    Call<APIReturn> getUserInfo(@Path("userID") String userID);

    @POST("/trainData/{userID}/{posture}/{time}")
    Call<APIReturn> trainData(@Path("userID") String userID, @Path("posture") String posture, @Path("time") String time);

    @POST("/registerUser/{fname}/{lname}/{email}/{weight}/{height}/{password}")
    Call<APIReturn> registerUser(@Path("fname") String fname, @Path("lname") String lname, @Path("email") String email, @Path("weight") String weight, @Path("height") String height, @Path("password") String password);

}


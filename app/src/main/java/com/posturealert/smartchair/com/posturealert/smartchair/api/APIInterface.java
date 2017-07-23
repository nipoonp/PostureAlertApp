package com.posturealert.smartchair.com.posturealert.smartchair.api;

import retrofit2.Call;
import retrofit2.http.GET;
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
}


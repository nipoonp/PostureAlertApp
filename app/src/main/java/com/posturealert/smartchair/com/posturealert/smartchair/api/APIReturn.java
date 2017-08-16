package com.posturealert.smartchair.com.posturealert.smartchair.api;

/**
 * Created by Nipoon on 21/07/2017.
 */

public class APIReturn {


    //API /sensorReadings
    private String s0,s1,s2,s3,s4;

    public String getS0() {
        return s0;
    }

    public String getS1() {
        return s1;
    }

    public String getS2() {
        return s2;
    }

    public String getS3() {
        return s3;
    }

    public String getS4() {
        return s4;
    }


    //API /dashBoardPieChart
    private String E,LU,SF,LF,SS,SB,LL,LR,LC,RC,NA,PP;

    public String getE() {
        return E;
    }

    public String getLU() {
        return LU;
    }

    public String getSF() {
        return SF;
    }

    public String getSS() {
        return SS;
    }

    public String getSB() {
        return SB;
    }

    public String getLL() {
        return LL;
    }

    public String getLR() {
        return LR;
    }

    public String getLC() {
        return LC;
    }

    public String getRC() {
        return RC;
    }

    public String getNA() {
        return NA;
    }

    public String getPP() {
        return PP;
    }

    public String getLF() {
        return LF;
    }

    //API UserInfo
    private String firstName, lastName, userID, chairID;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserID() {
        return userID;
    }

    public String getChairID() {
        return chairID;
    }

    //API trainData
    private String status;

    public String getStatus() { return status; }

    //API RegisterUser



}

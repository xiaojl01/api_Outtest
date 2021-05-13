package com.test.day01;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class FutureLoanTest {
    @Test
    public void register(){
        String jsonStr = "{\"mobile_phone\": \"13169931206\",\"pwd\": \"lemon123456\",\"type\":\"0\",\"reg_name\":\"管理员用户xiao\"}";
        given().
                body(jsonStr).
                header("X-Lemonban-Media-Type","lemonban.v2").
                contentType("application/json;charset=utf-8").
        when().
                post("http://api.lemonban.com/futureloan/member/register").
        then().
                log().all();
    }

    @Test
    public void login() {
        String jsonStr = "{\"mobile_phone\": \"13169931206\",\"pwd\": \"lemon123456\"}";
        Response response =
        given().
               body(jsonStr).
               header("X-Lemonban-Media-Type", "lemonban.v2").
               contentType("application/json;charset=utf-8").
        when().
                post("http://api.lemonban.com/futureloan/member/login").
        then().
                log().all().
                extract().response();

    }
}

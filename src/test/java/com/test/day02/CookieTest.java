package com.test.day02;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class CookieTest {
    /**
     * cookie+session鉴权
     * （cookie+token的方法也一样）
     */
    @Test
    public void testAuthenticationWithCookie(){
        Response res=
        given().
                header("Content-Type","application/x-www-form-urlencoded;charset=utf-8").
                formParam("loginame","admin").
                formParam("password","e10adc3949ba59abbe56e057f20f883e").
        when().
                post("http://erp.lemfix.com/user/login").
        then().
                log().all().
                extract().response();
        //获取cookie方法一：
        System.out.println(res.header("Set-Cookie"));
        //获取cookie方法二：推荐
        System.out.println(res.getCookies());

        //getUserSession 接口请求
        given().
                cookies(res.getCookies()).
        when().
                get("http://erp.lemfix.com/user/getUserSession").
        then().
                log().all();


    }
}

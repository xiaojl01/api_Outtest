package com.test.day02;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class AssertTest {
    @Test
    public void testLogin(){
        String jsonStr = "{\"mobile_phone\": \"13169931206\",\"pwd\": \"lemon123456\"}";
        Response response =
        given().
               body(jsonStr).
               header("X-Lemonban-Media-Type","lemonban.v2").
               contentType("application/json;charset=utf-8").
        when().
               post("http://api.lemonban.com/futureloan/member/login").
        then().
               log().all().
               extract().response();
        //获取业务的code
        int code = response.path("code");
        //获取msg
        String msg = response.path("msg");
        //获取mobile_phone
        String mobilePhone = response.path("data.mobile_phone");
        //断言--使用TestNG框架所提供的断言API
        //第一个参数：实际值，第二个参数：期望值，第三个参数：断言失败的提示信息（可选）
        Assert.assertEquals(code,0);
        Assert.assertEquals(msg,"OK");
        Assert.assertEquals(mobilePhone,"13169931206","手机号码断言失败");
        Assert.assertTrue(msg.equals("OK"));
    }



}

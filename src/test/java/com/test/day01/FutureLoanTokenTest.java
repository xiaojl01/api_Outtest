package com.test.day01;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

public class FutureLoanTokenTest {
    @Test
    public void post(){
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
        //获取响应信息所有内容：响应头，响应体
//        System.out.println(response.asString());
        //提取响应状态码
//        System.out.println(response.statusCode());
        //获取接口的响应时间,单位为毫秒
//        System.out.println(response.time());
        //提取响应头
//        System.out.println(response.header("Content-Type"));
        //提取响应体token
        String tokenStr = response.path("data.token_info.token");
        //提取会员id
        int memberId = response.path("data.id");

        //充值的请求
        Map<String,String> map = new HashMap<String, String>();
        map.put("member_id",memberId+"");
        map.put("amount","10000.00");
        given().
                header("X-Lemonban-Media-Type","lemonban.v2").
                header("Authorization","Bearer "+tokenStr).
                header("Content-Type","application/json;charset=utf-8").
                body(map).
        when().
                post("http://api.lemonban.com/futureloan/member/recharge").
        then().
                log().all();

    }
}

package com.test.day01;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

public class RestApiTest {
    @Test
    public void get1(){
        //1.简单的get请求，没有参数
        given().
        when().
                get("http://httpbin.org/get").
        then().
             log().all();
    }

    @Test
    public void get2(){
        //2.get请求，带参数方法一：
        given().
        when().
                get("http://httpbin.org/get?name=张三&age=20").
        then().
                log().all();
    }

    @Test
    public void get3(){
        //3.get请求，带参数方法二：
        given().
                queryParam("name","张三").
                queryParam("age","18").
        when().
                get("http://httpbin.org/get").
        then().
                log().all();
    }

    @Test
    public void get4(){
        //4.get请求，带参数方法三：
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("name","张三");
        map.put("age",28);
        map.put("sex","男");
        given().
                queryParams(map).
        when().
                get("http://httpbin.org/get").
        then().
                log().all();
    }

    @Test
    public void post1(){
        //1.简单的post请求
        given().
        when().
                post("http://httpbin.org/post").
        then().
                log().all();
    }

    @Test
    public void post2(){
        //1.简单的post请求,form表单类型
        given().
                formParam("name","张三").
                formParam("age",18).
                contentType("application/x-www-form-urlencoded;charset=utf-8").
        when().
                post("http://httpbin.org/post").
        then().
                log().all();
    }

    @Test
    public void post3(){
        //1.简单的post请求,json类型
        String jsonStr = "{\"mobile_phone\":\"13169939770\",\"pwd\":\"123456\"}";
        given().
                contentType("application/json;charset=utf-8").
                body(jsonStr).
        when().
                post("http://httpbin.org/post").
        then().
                log().all();
    }

    @Test
    public void post4(){
        //1.简单的post请求,xml类型
        String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<suite>\n" +
                "    <class>测试xml</class>\n" +
                "</suite>";
        given().
                contentType("text/xml;charset=utf-8").
                body(xmlStr).
        when().
                post("http://httpbin.org/post").
        then().
                log().all();
    }

    @Test
    public void post5(){
        //1.简单的post请求,多参数表单类型,上传文件
        given().
                contentType("multipart/form-data;charset=utf-8").
                multiPart(new File("C:\\Users\\lmm\\Desktop\\新建文件夹\\1.jpg")).
        when().
                post("http://httpbin.org/post").
        then().
                log().all();
    }


}

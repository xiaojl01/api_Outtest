package com.lemon.testcases;

import com.lemon.base.Basecase;
import com.lemon.pojo.CaseInfo;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

public class RechargeTest extends Basecase {
    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setUp(){
        caseInfoList = getCaseDateFromExcel(3);
        //参数化替换
        caseInfoList = paramsReplace(caseInfoList);
    }


    @Test(dataProvider = "getRechargeData")
    public void testRecharge(CaseInfo caseInfo){

        //jackson json字符串转Map
        Map headersMap = fromJsonToMap(caseInfo.getRequestHeader());

        //将日志输出到对应的文件中
        String logfilePath = addLogToFile(caseInfo);

        Response res =
        given().log().all().
                //让Rest-Assured返回json小数的时候，使用BigDecimal类型来存储小数（默认是Float类型）
//                config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).
                body(caseInfo.getInputParams()).
                headers(headersMap).
        when().
                post(caseInfo.getUrl()).
        then().
                log().all().
                extract().response();

        //将日志信息添加到Allure报表中
        addLogToAllure(logfilePath);
        //接口响应信息断言
        assertExpected(caseInfo,res);
        //断言数据库
        assertSQL(caseInfo);
    }



    @DataProvider
    public Object[] getRechargeData() {
        //dataprovider数据提供者返回值类型可以是object[]也可以是object[][]
        //怎么把List集合转换为Object[][]或者Object[]
        Object[] datas = caseInfoList.toArray();
        System.out.println(datas);
        return datas;
    }


}





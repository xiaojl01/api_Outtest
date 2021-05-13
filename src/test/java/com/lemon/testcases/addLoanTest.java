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

public class addLoanTest extends Basecase{
    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setUp(){
        caseInfoList = getCaseDateFromExcel(4);
        //参数化替换
        caseInfoList = paramsReplace(caseInfoList);
    }


    @Test(dataProvider = "getAddLoanData")
    public void testAddLoan(CaseInfo caseInfo){
        //jackson json字符串转Map
        Map headersMap = fromJsonToMap(caseInfo.getRequestHeader());
        //将日志输出到对应的文件中
        String logfilePath = addLogToFile(caseInfo);

        Response res =
                given().log().all().
//                        config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).
                        headers(headersMap).
                        body(caseInfo.getInputParams()).
                when().
                        post(caseInfo.getUrl()).
                then().
                        log().all().
                        extract().response();

        //将日志信息添加到Allure报表中
        addLogToAllure(logfilePath);
        //断言
        //1.把数据转换为map
        assertExpected(caseInfo,res);
    }

    @DataProvider
    public Object[] getAddLoanData() {
        //dataprovider数据提供者返回值类型可以是object[]也可以是object[][]
        //怎么把List集合转换为Object[][]或者Object[]
        Object[] datas = caseInfoList.toArray();
        return datas;
    }

}

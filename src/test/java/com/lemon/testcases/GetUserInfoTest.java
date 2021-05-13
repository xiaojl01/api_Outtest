package com.lemon.testcases;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.base.Basecase;
import com.lemon.pojo.CaseInfo;
import com.lemon.data.GlobalEnviroment;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

public class GetUserInfoTest extends Basecase {
    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setUp(){
        caseInfoList = getCaseDateFromExcel(2);
        //参数化替换
        caseInfoList = paramsReplace(caseInfoList);
    }


    @Test(dataProvider = "data")
    public void getUserInfo(CaseInfo caseInfo){

        //jackson json字符串转Map
        Map headersMap = fromJsonToMap(caseInfo.getRequestHeader());
        //将日志输出到对应的文件中
        String logfilePath = addLogToFile(caseInfo);

        Response res =
        given().log().all().
                headers(headersMap).
        when().
                get(caseInfo.getUrl()).
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
    public Object[] data() {
        //dataprovider数据提供者返回值类型可以是object[]也可以是object[][]
        //怎么把List集合转换为Object[][]或者Object[]
        Object[] datas = caseInfoList.toArray();
        System.out.println(datas);
        return datas;
    }


}





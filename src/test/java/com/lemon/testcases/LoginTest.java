package com.lemon.testcases;

import com.lemon.base.Basecase;
import com.lemon.data.Constans;
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

import static io.restassured.RestAssured.given;

public class LoginTest extends Basecase {
    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setUp(){
        caseInfoList = getCaseDateFromExcel(1);
        //参数化替换
        caseInfoList = paramsReplace(caseInfoList);
    }
    @Test(dataProvider = "data02")
    public void login(CaseInfo caseInfo){
        //jackson json字符串转Map
        Map headersMap = fromJsonToMap(caseInfo.getRequestHeader());
        //将日志输出到对应的文件中
        String logfilePath = addLogToFile(caseInfo);

        Response res =
        given().log().all().
                body(caseInfo.getInputParams()).
                headers(headersMap).
        when().
                post(caseInfo.getUrl()).
        then().
                log().all().
                extract().response();

        //将日志信息添加到Allure报表中
        addLogToAllure(logfilePath);
        //断言
        assertExpected(caseInfo,res);
        //保存token到环境变量中
        if (caseInfo.getCaseID() == 1){
            //2.拿到正常用例返回响应信息里面的token
            GlobalEnviroment.envData.put("token1",res.path("data.token_info.token"));
        }else if (caseInfo.getCaseID() == 2){
            GlobalEnviroment.envData.put("token2",res.path("data.token_info.token"));
        }else if (caseInfo.getCaseID() == 3){
            GlobalEnviroment.envData.put("token3",res.path("data.token_info.token"));
        }

    }

    @DataProvider
    public Object[] data02() {
        //dataprovider数据提供者返回值类型可以是object[]也可以是object[][]
        //怎么把List集合转换为Object[][]或者Object[]
        Object[] datas = caseInfoList.toArray();
        return datas;
    }

}





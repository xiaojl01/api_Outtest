package com.lemon.testcases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lemon.base.Basecase;
import com.lemon.pojo.CaseInfo;
import com.lemon.util.JDBCUtils;
import com.lemon.util.PhoneRandom;
import com.lemon.data.GlobalEnviroment;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class RegisterTest extends Basecase {
    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setup(){
        caseInfoList = getCaseDateFromExcel(0);

    }

    @Test(dataProvider = "getRegisterData")
    public void testRegister(CaseInfo caseInfo){
        //随机生成三个没有注册过的手机号
        if (caseInfo.getCaseID() == 1) {
            String mobilePhone1 = PhoneRandom.getRandomPhone();
            //存到环境变量中
            GlobalEnviroment.envData.put("mobile_phone1",mobilePhone1);
        }else if (caseInfo.getCaseID() == 2){
            String mobilePhone2 = PhoneRandom.getRandomPhone();
            GlobalEnviroment.envData.put("mobile_phone2",mobilePhone2);
        }else if (caseInfo.getCaseID() == 3){
            String mobilePhone3 = PhoneRandom.getRandomPhone();
            GlobalEnviroment.envData.put("mobile_phone3",mobilePhone3);
        }
        //参数化替换
        caseInfo = paramsReplaceCaseInfo(caseInfo);

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
        //断言数据库
        assertSQL(caseInfo);
        //保存密码pwd
        Map inputParamsMap = fromJsonToMap(caseInfo.getInputParams());
        Object pwd = inputParamsMap.get("pwd");
        if (caseInfo.getCaseID() == 1){
            GlobalEnviroment.envData.put("pwd1",pwd);
            GlobalEnviroment.envData.put("member_id1",res.path("data.id"));
        }else if (caseInfo.getCaseID() == 2){
            GlobalEnviroment.envData.put("pwd2",pwd);
            GlobalEnviroment.envData.put("member_id2",res.path("data.id"));
        }else if (caseInfo.getCaseID() == 3){
            GlobalEnviroment.envData.put("pwd3",pwd);
            GlobalEnviroment.envData.put("member_id3",res.path("data.id"));
        }
    }

    @DataProvider
    public Object[] getRegisterData() {
        //dataprovider数据提供者返回值类型可以是object[]也可以是object[][]
        //怎么把List集合转换为Object[][]或者Object[] --- toArray()
        Object[] datas = caseInfoList.toArray();
        return datas;
    }

}

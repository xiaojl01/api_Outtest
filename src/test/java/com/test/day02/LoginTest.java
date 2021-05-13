package com.test.day02;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.*;

public class LoginTest {
    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setUp(){
        caseInfoList = getCaseDateFromExcel(1);
    }
    @Test(dataProvider = "data02")
    public void login(CaseInfo caseInfo) throws JsonProcessingException {
        //jackson json字符串转Map
        //1、实例化objectMapper对象
        ObjectMapper objectMapper = new ObjectMapper();
        //readValue方法参数解释
        //第一个参数：json字符串  第二个参数：转成的类型（Map）
        Map headersMap = objectMapper.readValue(caseInfo.getRequestHeader(), Map.class);
        Response res =
        given().
                body(caseInfo.getInputParams()).
                headers(headersMap).
        when().
                post("http://api.lemonban.com/futureloan"+ caseInfo.getUrl()).
        then().
                log().all().
                extract().response();

        //断言
        //1.把数据转换为map
        ObjectMapper objectMapper1 = new ObjectMapper();
        Map expectedMap = objectMapper1.readValue(caseInfo.getExpected(),Map.class);
        //2.循环遍历取到map里面每一组键值对
        Set<Map.Entry<String, Object>> set = expectedMap.entrySet();
        for (Map.Entry<String,Object> map : set){
            System.out.println("key:: " + map.getKey());
            System.out.println("value:: " + map.getValue());
            Assert.assertEquals(res.path(map.getKey()),map.getValue());

        }

        //保存memberId到环境变量中
        Integer memberID = res.path("data.id");
        if (memberID != null){
//            GlobalEnviroment.memberId = memberID;
            //1.拿到正常用例返回响应信息里面的memberId
            GlobalEnviroment.envData.put("member_id",memberID);
            //2.拿到正常用例返回响应信息里面的token
            GlobalEnviroment.envData.put("token",res.path("data.token_info.token"));
        }



    }

//    @DataProvider
//    public Object[][] data() {
//        //1.请求的接口地址，2.请求方式，3.请求头，4.请求数据
//        Object[][] datas = {{"http://api.lemonban.com/futureloan/member/login", "post", "application/json;charset=utf-8", "lemonban.v1", "{\"mobile_phone\": \"13169931202\",\"pwd\": \"lemon123456\"}"},
//                {"http://api.lemonban.com/futureloan/member/login", "post", "application/json;charset=utf-8", "lemonban.v1", "{\"mobile_phone\": \"131699312034\",\"pwd\": \"lemon123456\"}"},
//                {"http://api.lemonban.com/futureloan/member/login", "post", "application/json;charset=utf-8", "lemonban.v1", "{\"mobile_phone\": \"1316993120a\",\"pwd\": \"lemon123456\"}"},
//                {"http://api.lemonban.com/futureloan/member/login", "post", "application/json;charset=utf-8", "lemonban.v1", "{\"mobile_phone\": \"1001248576\",\"pwd\": \"lemon123456\"}"},
//        };
//        return datas;
//    }


    @DataProvider
    public Object[] data02() {
        //dataprovider数据提供者返回值类型可以是object[]也可以是object[][]
        //怎么把List集合转换为Object[][]或者Object[]
        Object[] datas = caseInfoList.toArray();
        return datas;
    }

    /**
     * 从excel读取所需的用例数据
     * @param index sheet的索引，从0开始
     * @return 每个caseinfo
     */
    public List<CaseInfo> getCaseDateFromExcel(int index){
        //dataprovider数据提供者返回值类型可以是object[]也可以是object[][]
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(index);
        File excelFile = new File("src/test/resources/api_testcases_futureloan_v1.xls");
        List<CaseInfo> list = ExcelImportUtil.importExcel(excelFile, CaseInfo.class, importParams);
        return list;

    }


//    public static void main(String[] args) {
//        //推荐读取excel技术：EasyPOI
//        //第一个参数：File对象  第二个参数：隐射的实体类  第三个参数：读取配置对象
//        ImportParams importParams = new ImportParams();
//        //读取表中第几个sheet，默认为0
//        importParams.setStartSheetIndex(1);
//        //读取的sheet数量，默认为1
//        importParams.setSheetNum(1);
//        File excelFile = new File("src/test/resources/api_testcases_futureloan_v1.xls");
//        List<CaseInfo> list = ExcelImportUtil.importExcel(excelFile, CaseInfo.class, importParams);
//        for (CaseInfo line : list) {
//            System.out.println(line);
//        }
//
//    }
}





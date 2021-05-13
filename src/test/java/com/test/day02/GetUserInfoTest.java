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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

public class GetUserInfoTest {
    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setUp(){
        caseInfoList = getCaseDateFromExcel(3);
        //参数化替换
        caseInfoList = paramsReplace(caseInfoList);
    }


    @Test(dataProvider = "data")
    public void getUserInfo(CaseInfo caseInfo) throws JsonProcessingException {
//        //参数化替换
//        //1.url中的参数{{member_id}}替换
//        String urlStr = regexReplace(caseInfo.getUrl());
//        //2.期望结果中的参数需要替换
//        String expectedStr = regexReplace(caseInfo.getExpected());
//        //3.请求头的参数替换
//        String requestHeaderStr = regexReplace(caseInfo.getRequestHeader());

        //jackson json字符串转Map
        //1、实例化objectMapper对象
        ObjectMapper objectMapper = new ObjectMapper();
        //readValue方法参数解释
        //第一个参数：json字符串  第二个参数：转成的类型（Map）
        Map headersMap = objectMapper.readValue(caseInfo.getRequestHeader(), Map.class);
        Response res =
        given().
                headers(headersMap).
        when().
                get("http://api.lemonban.com/futureloan"+ caseInfo.getUrl()).
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
            Assert.assertEquals(res.path(map.getKey()),map.getValue());

        }



    }



    @DataProvider
    public Object[] data() {
        //dataprovider数据提供者返回值类型可以是object[]也可以是object[][]
        //怎么把List集合转换为Object[][]或者Object[]
        Object[] datas = caseInfoList.toArray();
        System.out.println(datas);
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

    /**
     * 正则替换
     * @param sourceStr 要替换的原始字符串
     * @return 替换之后的新字符串
     */
    public String regexReplace(String sourceStr){
        //1、定义正则表达式
        String regex = "\\{\\{(.*?)\\}\\}";
        //2.通过正则表达式编译出来一个匹配器
        Pattern pattern = Pattern.compile(regex);
        //3.开始进行匹配 参数：为你要去在哪一个字符串里面去进行匹配
        Matcher matcher = pattern.matcher(sourceStr);
        //保存的是匹配的{{}}整个表达式
        String findstr = "";
        //保存的是匹配的（）里面的内容
        String singleStr = "";
        //4.连续查找，连续匹配
        while(matcher.find()){
            //匹配到的要替换的参数
            findstr =  matcher.group(0);
            singleStr = matcher.group(1);
        }
        //5.从环境变量中取到要替换的值
        Object replaceStr = GlobalEnviroment.envData.get(singleStr);
        //6.替换原始字符串的内容
        String replace = sourceStr.replace(findstr,replaceStr+"");
        return replace;
    }

    public List<CaseInfo> paramsReplace(List<CaseInfo> caseInfoList){
        //替换请求头，接口地址，输入参数，期望结果
        for (CaseInfo caseInfo : caseInfoList){
            if (caseInfo.getRequestHeader() != null) {
                String requestHeader = regexReplace(caseInfo.getRequestHeader());
                caseInfo.setRequestHeader(requestHeader);
            }
            if (caseInfo.getUrl() != null) {
                String url = regexReplace(caseInfo.getUrl());
                caseInfo.setUrl(url);
            }
            if (caseInfo.getInputParams() != null) {
                String inputParams = regexReplace(caseInfo.getInputParams());
                caseInfo.setInputParams(inputParams);
            }
            if (caseInfo.getExpected() != null) {
                String expected = regexReplace(caseInfo.getExpected());
                caseInfo.setExpected(expected);
            }
        }
        return caseInfoList;
    }

//    public static void main(String[] args) {
//        String number = "111";
//        String str = "/member/{{member_id1}}/info";
//        //参数化替换
//        //正则表达式
//        //"."匹配任意的字符
//        //"*"匹配前面的字符0次或者任意次
//        //"?"贪婪匹配
//        //1、定义正则表达式
//        String regex = "\\{\\{(.*?)\\}\\}";
//        //2.通过正则表达式编译出来一个匹配器
//        Pattern pattern = Pattern.compile(regex);
//        //3.开始进行匹配 参数：为你要去在哪一个字符串里面去进行匹配
//        Matcher matcher = pattern.matcher(str);
//        String findStr = "";
//        //4.连续查找，连续匹配
//        while(matcher.find()){
////            System.out.println(matcher.group(0));
////            System.out.println(matcher.group(1));
//            findStr = matcher.group(0);
//        }
//        String replace = str.replace(findStr, number);
//        System.out.println(replace);
//    }

}





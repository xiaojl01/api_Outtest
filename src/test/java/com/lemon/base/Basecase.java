package com.lemon.base;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.data.Constans;
import com.lemon.pojo.CaseInfo;
import com.lemon.data.GlobalEnviroment;
import com.lemon.util.JDBCUtils;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

/**
 * 所有测试类的父类，公共的方法
 */
public class Basecase {
    @BeforeTest
    public void globalSetup() throws FileNotFoundException {
        //整体全局性前置配置/初始化
        //1.设置项目的baseurl
        RestAssured.baseURI = "http://api.lemonban.com/futureloan";
        //2.设置接口响应结果，如果是返回的json小数类型，就返回BigDecimal类型
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        //3.设置项目的日志存储到本地文件中
//        PrintStream fileOutPutStream = new PrintStream(new File("log/test_all.log"));
//        RestAssured.filters(new RequestLoggingFilter(fileOutPutStream),new ResponseLoggingFilter(fileOutPutStream));
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
        File excelFile = new File(Constans.EXCEL_PATH);
        List<CaseInfo> list = ExcelImportUtil.importExcel(excelFile, CaseInfo.class, importParams);
        return list;

    }

    /**
     * 正则替换
     * @param sourceStr 要替换的原始字符串
     * @return 替换之后的新字符串
     */
    public String regexReplace(String sourceStr){
        //如果参数化的原字符串为null的话不需要去参数化替换
        if (sourceStr == null){
            return sourceStr;
        }
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
            //5.从环境变量中取到要替换的值
            Object replaceStr = GlobalEnviroment.envData.get(singleStr);
            //6.替换原始字符串的内容
            sourceStr = sourceStr.replace(findstr, replaceStr + "");
        }
        return sourceStr;
    }

    /**
     * 替换用例中所有的case中的参数
     * @param caseInfoList 用例数据集合
     * @return 替换好的用例数据集合
     */
    public List<CaseInfo> paramsReplace(List<CaseInfo> caseInfoList){
        //替换请求头，接口地址，输入参数，期望结果
        for (CaseInfo caseInfo : caseInfoList){
            String requestHeader = regexReplace(caseInfo.getRequestHeader());
            caseInfo.setRequestHeader(requestHeader);
            String url = regexReplace(caseInfo.getUrl());
            caseInfo.setUrl(url);
            String inputParams = regexReplace(caseInfo.getInputParams());
            caseInfo.setInputParams(inputParams);
            String expected = regexReplace(caseInfo.getExpected());
            caseInfo.setExpected(expected);
            //参数化替换数据库效验
            String checkSQL = regexReplace(caseInfo.getCheckSQL());
            caseInfo.setCheckSQL(checkSQL);
        }
        return caseInfoList;
    }

    /**
     * 对某一条case进行参数化替换
     * @param caseInfo 需要替换参数的case
     * @return 替换之后的case
     */
    public CaseInfo paramsReplaceCaseInfo(CaseInfo caseInfo){
        //替换请求头，接口地址，输入参数，期望结果
        String requestHeader = regexReplace(caseInfo.getRequestHeader());
        caseInfo.setRequestHeader(requestHeader);
        String url = regexReplace(caseInfo.getUrl());
        caseInfo.setUrl(url);
        String inputParams = regexReplace(caseInfo.getInputParams());
        caseInfo.setInputParams(inputParams);
        String expected = regexReplace(caseInfo.getExpected());
        caseInfo.setExpected(expected);
        String checkSQL = regexReplace(caseInfo.getCheckSQL());
        caseInfo.setCheckSQL(checkSQL);
        return caseInfo;
    }

    /**
     * json串转换成Map类型
     * @param jsonStr 要转换的json串
     * @return 返回转换的Map类型数据
     */
    public Map fromJsonToMap(String jsonStr){
        //jackson json字符串转Map
        //1、实例化objectMapper对象
        ObjectMapper objectMapper = new ObjectMapper();
        //readValue方法参数解释
        //第一个参数：json字符串  第二个参数：转成的类型（Map）
        Map map = null;
        try {
            map = objectMapper.readValue(jsonStr, Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 用例公共的断言方法，断言期望值和实际值
     * @param caseInfo 用例信息
     * @param res 接口的响应结果
     */
    public void assertExpected(CaseInfo caseInfo, Response res){
        //1.把数据转换为map
        Map expectedMap = fromJsonToMap(caseInfo.getExpected());
        //2.循环遍历取到map里面每一组键值对
        Set<Map.Entry<String, Object>> set = expectedMap.entrySet();
        for (Map.Entry<String,Object> map : set){
            //把期望值转换（期望值的json结果是小数类型-Float/Double类型）
            Object expected = map.getValue();
            //判断一下，是不是小数类型，如果是小数类型，才转换
            if (expected instanceof Float || expected instanceof Double) {
                BigDecimal bigDecimal = new BigDecimal(expected.toString());
                Assert.assertEquals(res.path(map.getKey()), bigDecimal,"接口响应断言失败");
            }else{
                Assert.assertEquals(res.path(map.getKey()),expected,"接口响应断言失败");
            }
        }
    }

    /**
     * 断言数据库
     * @param caseInfo
     */
    public void assertSQL(CaseInfo caseInfo){
        //断言数据库
        if (caseInfo.getCheckSQL() != null) {
            Map checkSqlMap = fromJsonToMap(caseInfo.getCheckSQL());
            Set<Map.Entry<String, Object>> set = checkSqlMap.entrySet();
            for (Map.Entry<String, Object> mapEntry : set) {
                Object actual = JDBCUtils.querySingle(mapEntry.getKey());
                Object expected = mapEntry.getValue();
                //1.数据库查询返回结果是Long类型，Excel读取的期望结果类型是Integer类型
                if (expected instanceof Integer) {
                    //把expected转成Long类型
                    Long expectedValue = new Long(expected.toString());
                    Assert.assertEquals(actual, expectedValue,"数据库断言失败");
                }else if (expected instanceof Double){
                    //2.数据库查询返回结果是BigDecimal，Excel读取的期望结果类型是Double类型
                    BigDecimal expectedBigDecimal = new BigDecimal(expected.toString());
                    Assert.assertEquals(actual,expectedBigDecimal,"数据库断言失败");
                }else {
                    //3.数据库查询返回结果和Excel中的期望结果是String类型
                    Assert.assertEquals(actual,expected,"数据库断言失败");
                }

            }
        }
    }

    /**
     * 将日志重定向到单独的文件中
     * @param caseInfo 用例信息
     */
    public String addLogToFile(CaseInfo caseInfo) {
        String logFilePath = "";
        if (!Constans.IS_DEBUG) {
            //日志输出
            //提前创建好目录层级
            String dirPath = "target/log/" + caseInfo.getInterfaceName();
            File dirFile = new File(dirPath);
            //判断该目录是否存在，不存在再创建
            if (!dirFile.isDirectory()) {
                dirFile.mkdirs();
            }
            logFilePath = dirPath + "/" + caseInfo.getInterfaceName() + "_" + caseInfo.getCaseID() + ".log";
            //请求之前对日志做配置，输出到对应的文件中
            PrintStream fileOutPutStream = null;
            try {
                fileOutPutStream = new PrintStream(new File(logFilePath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            RestAssured.config = RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(fileOutPutStream));

        }
        return logFilePath;
    }

    /**
     * 接口请求之后把请求和响应的信息添加到Allure中
     * @param logFilePath 日志文件的路径
     */
    public void addLogToAllure(String logFilePath){
        if (!Constans.IS_DEBUG){
            //将日志作为附件添加到Allure中（附件形式）
            //第一个参数：附件的名字 第二个参数：FileInputStream
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(logFilePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Allure.addAttachment("接口请求响应信息",inputStream);
        }
    }
}

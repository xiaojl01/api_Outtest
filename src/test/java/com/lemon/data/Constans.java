package com.lemon.data;
/*
 * 常量类
 */
public class Constans {
    //excel用例文件路径
    public static final String EXCEL_PATH = "src/test/resources/api_testcases_futureloan_v4.xls";
    //数据库地址
    public static final String SQLSEVER_URL = "jdbc:mysql://8.129.91.152:3306/futureloan?useUnicode=true&characterEncoding=utf-8";
    //数据库用户名
    public static final String SQLSRVER_USER = "future";
    //数据库密码
    public static final String SQLSRVER_PWD = "123456";
    //测试环境basic地址
    public static final String BASEURL_TEST = "http://api.lemonban.com/futureloan";
    //日志调试开关 true输出到控制台  false输出到文件
    public static final boolean IS_DEBUG = false;
}

package com.nowy.baselib.web;

import android.text.TextUtils;


import com.nowy.baselib.utils.ConvertUtil;
import com.nowy.baselib.utils.JsonUtil;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nowy on 2017/11/3.
 * URL操作类，主要用于与HTML5的URL交互
 * 支持两种格式解析
 * app://share?userId=10010&data='http://shareUrl.com?a=b'
 * app://share?{...}
 */

public class UrlOperation {
    public static final String TAG = UrlOperation.class.getSimpleName();
    private static final String CONSTANTS_SPLICING = "#c_param_";
    private static final String FORMAT_CONSTANTS_SPLICING = CONSTANTS_SPLICING +"%d";//用来替换字符串的格式
    private Map<String, String> mParamMap = new LinkedHashMap<>();
    private String mBaseUrl;
    /**
     * 去掉url中的路径，留下请求参数部分
     * @param strURL url地址
     * @return url请求参数部分
     */
    private String truncateUrlPage(String strURL){
        String strAllParam=null;
        String[] arrSplit=null;
        mBaseUrl = strURL;
        strURL=strURL.trim().toLowerCase();
        arrSplit=strURL.split("[?]");
        if(strURL.length()>1){
            if(arrSplit.length>1){
                strAllParam = strURL.substring(strURL.indexOf("?")+1,strURL.length());
            }
            mBaseUrl = arrSplit[0];
        }
        return strAllParam;
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.php?Action=del&id=123"，解析出Action:del,id:123存入map中
     * @param URL  url地址
     * @return  url请求参数部分
     */
    public Map<String, Object> URLRequest(String URL){
        Map<String, Object> mapRequest = new HashMap<>();
        String strUrlParam=truncateUrlPage(URL);
        if(strUrlParam==null){
            return mapRequest;
        }
        if(isJson(strUrlParam)){
            mapRequest = json2Map(strUrlParam);
        }else{
            mapRequest = urlParam2Map(strUrlParam);
        }
        return mapRequest;
    }


    /**
     * URL的参数转化为map
     * @param urlParam
     * @return
     */
    private Map<String, Object> urlParam2Map(String urlParam){
        Map<String, Object> mapRequest = new HashMap<>();
        String paramStr = replaceParams(urlParam);
        String[] arrSplit = paramStr.split("[&]");
        for(String strSplit:arrSplit){
            String[] arrSplitEqual;
            arrSplitEqual= strSplit.split("[=]");
            //解析出键值
            if(arrSplitEqual.length>1){
                String key = getRealParam(urlParam,arrSplitEqual[0]);
                String value = getRealParam(urlParam,arrSplitEqual[1]);
                mapRequest.put(key, value);
            }else{
                if(!TextUtils.isEmpty(arrSplitEqual[0])){
                    String key = getRealParam(urlParam,arrSplitEqual[0]);
                    mapRequest.put(key, "");
                }
            }
        }
        return mapRequest;
    }


    /**
     * JSON数据转map
     */
    private Map<String, Object> json2Map(String json){
        return JsonUtil.parseJson2Map(json);
    }

    /**
     * 修改参数字符串
     * @param strUrlParam
     * @return
     */
    private String replaceParams(String strUrlParam){
        Pattern pattern = Pattern.compile("'(.*?)(?<![^\\\\]\\\\)'");
        Matcher matcher = pattern.matcher(strUrlParam);
        mParamMap.clear();
        String tmp = strUrlParam;
        while(matcher.find()){

            String value = matcher.group();
            int index = strUrlParam.indexOf(value);
            String key = replaceParam(index);
            mParamMap.put(key,value);
            tmp = tmp.replace(value,key);
        }
        Logger.t(TAG).e("UrlOperation replaceParams tmp:"+tmp);
//        LogUtils.e("matcher strUrlParam:"+strUrlParam);
        return tmp;
    }


    /**
     * 根据“{}”判断是否JSON，可能比较笼统
     * 暂时不校验“[]”类型的JSON
     * @param urlParam
     * @return
     */
    private boolean isJson(String urlParam){
        if(TextUtils.isEmpty(urlParam)
                || !urlParam.startsWith("{")
                || !urlParam.endsWith("}")) return false;
        return true;
    }


    /**
     * 根据index生成规格字符串
     * 用于生成替换URL里面单引号内容的字符串
     * 格式含义：[c_param_10]：参数10是URL的参数部分里面的起始位置
     * @param index
     * @return
     */
    private String replaceParam(int index){
        String newParam  = String.format(Locale.CHINA,FORMAT_CONSTANTS_SPLICING,index);
        return newParam;
    }


    /**
     * 获取真实的参数内容
     * @param strUrlParam
     * @param param
     * @return
     */
    private String getRealParam(String strUrlParam,String param){
        if(mParamMap == null || !mParamMap.containsKey(param)) return param;
        String value = mParamMap.get(param);
        int index = strUrlParam.indexOf(value);
        int paramIndex = ConvertUtil.stringToInt(param.replace(CONSTANTS_SPLICING,""));
        if(index == paramIndex) return value;
        return param;

    }
}

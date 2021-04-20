package com.aqi.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aqi.entity.AllResult;
import com.aqi.utils.http.HttpRequestConfig;
import com.aqi.utils.http.HttpRequestResult;
import com.aqi.utils.http.HttpUtils;
import jodd.io.NetUtil;
import lombok.Data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Test {
    static Map<String,Integer> d = new HashMap<>();
    static Map<String,Integer> x = new HashMap<>();


    private static void gen(Map<String, Integer> d, Map<String, Integer> x) {
        String dstr = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z";
        String xstr = "a b c d e f g h i j k l m n o p q r s t u v w x y z";
        char[] dchars = dstr.toCharArray();
        char[] xchars = xstr.toCharArray();
        for(char c : dchars){
            if(c >= 'A' && c <= 'Z'){
                d.put(String.valueOf(c),(int)c - 64);
            }
        }
        for(char c : xchars){
            if(c >= 'a' && c <= 'z'){
                x.put(String.valueOf(c),(int)c - 97);
            }
        }
    }

    private static String url = "http://mapidroid.aqicn.org/aqicn/json/android/%s/v11.json?cityID=Hebei%2F%25E5%25BC%25A0%25E5%25AE%25B6%25E5%258F%25A3%25E5%25B8%2582%2F%25E4%25BA%2594%25E9%2587%2591%25E5%25BA%2593&lang=zh&package=Asia&appv=132&appn=3.5&tz=28800000&metrics=1080,2211,3.0&wifi=&devid=6fb268749236975d";

    public static void main(String[] args) throws IOException {
        List<Integer> list = new ArrayList<>();
        List<Integer> pm10s = new ArrayList<>();
        List<Integer> so2s = new ArrayList<>();
        List<Integer> no2s = new ArrayList<>();
        List<Integer> o3s = new ArrayList<>();
        gen(d,x);
        HttpRequestConfig httpRequestConfig = new HttpRequestConfig();
        httpRequestConfig.url(url);
        HttpRequestResult result = HttpUtils.get(httpRequestConfig);
        AllResult allResult = JSON.parseObject(result.getResponseText(), AllResult.class);
        List<Map<String, String>> historic = allResult.getHistoric();
        historic.forEach(m->{
            if(m.get("n").equals("pm25")){
                String v = m.get("d");
                handle(v,list);
            }
            if(m.get("n").equals("pm10")){
                String v = m.get("d");
                handle(v,pm10s);
            }
            if(m.get("n").equals("so2")){
                String v = m.get("d");
                handle(v,so2s);
            }
            if(m.get("n").equals("no2")){
                String v = m.get("d");
                handle(v,no2s);
            }
            if(m.get("n").equals("o3")){
                String v = m.get("d");
                handle(v,o3s);
            }
        });
        System.out.println(list.get(0));
        System.out.println(pm10s.get(0));
        System.out.println(so2s.get(0));
        System.out.println(no2s.get(0));
        System.out.println(o3s.get(0));
    }

    private static void handle(String v, List<Integer> list) {
        int c = 0;
        char[] chars = v.toCharArray();
        for(int i=0; i < chars.length; i++){
            char ch = chars[i];
            char f;
            if(ch >= '0' && ch <= '9'){
                continue;
            }
            if(ch >= 'A' && ch <= 'Z'){
                f = '-';
                Integer data = d.get(String.valueOf(ch));
                c = compute(c,data,f);
                list.add(c);
                StringBuilder sb1 = new StringBuilder();
                if(i < chars.length - 1){
                    sb1.append(chars[i + 1]);
                }
                if(i < chars.length - 2){
                    sb1.append(chars[i + 2]);
                }
                if(i < chars.length - 3){
                    sb1.append(chars[i + 3]);
                }
                if(sb1.length() > 0){
                    char[] chars1 = sb1.toString().toCharArray();
                    StringBuilder sb = new StringBuilder();
                    for(char cha : chars1){
                        if(cha >= '0' && cha <= '9'){
                            sb.append(cha);
                        }else{
                            break;
                        }
                    }
                    if(sb.length() > 0 && Integer.parseInt(sb.toString()) > 0){
                        for(int j=0; j < Integer.parseInt(sb.toString()) -1; j++){
                            list.add(0);
                        }
                    }
                }
            }
            if(ch >= 'a' && ch <= 'z'){
                f = '+';
                Integer data = x.get(String.valueOf(ch));
                c = compute(c,data,f);
                list.add(c);
                StringBuilder sb1 = new StringBuilder();
                if(i < chars.length - 1){
                    sb1.append(chars[i + 1]);
                }
                if(i < chars.length - 2){
                    sb1.append(chars[i + 2]);
                }
                if(i < chars.length - 3){
                    sb1.append(chars[i + 3]);
                }
                if(sb1.length() > 0){
                    char[] chars1 = sb1.toString().toCharArray();
                    StringBuilder sb = new StringBuilder();
                    for(char cha : chars1){
                        if(cha >= '0' && cha <= '9'){
                            sb.append(cha);
                        }else{
                            break;
                        }
                    }
                    if(sb.length() > 0 && Integer.parseInt(sb.toString()) > 0){
                        for(int j=0; j < Integer.parseInt(sb.toString()) -1; j++){
                            list.add(0);
                        }
                    }
                }
            }
            if(ch == '!'){
                f = '+';
                StringBuilder sb = new StringBuilder();
                merge(chars[i + 1], sb);
                merge(chars[i + 2], sb);
                merge(chars[i + 3], sb);
                c = compute(c, Integer.parseInt(sb.toString()), f);
                list.add(c);
            }
            if(ch == '$'){
                f = '-';
                StringBuilder sb = new StringBuilder();
                merge(chars[i + 1], sb);
                merge(chars[i + 2], sb);
                merge(chars[i + 3], sb);
                c = compute(c, Integer.parseInt(sb.toString()), f);
                list.add(c);
            }
        }
    }

    private static void merge(char ar, StringBuilder sb){
        if(ar >= '0' && ar <= '9'){
            sb.append(ar);
        }
    }

    private static int compute(int a, int b, char f){
        if(f == '-'){
            return a - b;
        }else if(f == '+'){
            return a + b;
        }
        return 0;
    }


}

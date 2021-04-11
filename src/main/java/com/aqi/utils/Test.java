package com.aqi.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Test {

    static String json = "[\n" +
            "  {\n" +
            "    \"name\": \"阿拉善盟\",\n" +
            "    \"cityCode\": \"0483\",\n" +
            "    \"firstCharacter\": \"A\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"鞍山市\",\n" +
            "    \"cityCode\": \"0412\",\n" +
            "    \"firstCharacter\": \"A\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"安庆市\",\n" +
            "    \"cityCode\": \"0556\",\n" +
            "    \"firstCharacter\": \"A\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"安阳市\",\n" +
            "    \"cityCode\": \"1526\",\n" +
            "    \"firstCharacter\": \"A\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"阿坝藏族羌族自治州\",\n" +
            "    \"cityCode\": \"0837\",\n" +
            "    \"firstCharacter\": \"A\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"安顺市\",\n" +
            "    \"cityCode\": \"0853\",\n" +
            "    \"firstCharacter\": \"A\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"安康市\",\n" +
            "    \"cityCode\": \"0915\",\n" +
            "    \"firstCharacter\": \"A\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"阿克苏地区\",\n" +
            "    \"cityCode\": \"0997\",\n" +
            "    \"firstCharacter\": \"A\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"阿勒泰地区\",\n" +
            "    \"cityCode\": \"0906\",\n" +
            "    \"firstCharacter\": \"A\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"北京市\",\n" +
            "    \"cityCode\": \"010\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"保定市\",\n" +
            "    \"cityCode\": \"0312\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"包头市\",\n" +
            "    \"cityCode\": \"0472\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"巴彦淖尔市\",\n" +
            "    \"cityCode\": \"0478\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"本溪市\",\n" +
            "    \"cityCode\": \"0414\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"白山市\",\n" +
            "    \"cityCode\": \"0439\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"白城市\",\n" +
            "    \"cityCode\": \"0436\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"蚌埠市\",\n" +
            "    \"cityCode\": \"0552\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"亳州市\",\n" +
            "    \"cityCode\": \"0558\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"滨州市\",\n" +
            "    \"cityCode\": \"0543\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"北海市\",\n" +
            "    \"cityCode\": \"0779\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"百色市\",\n" +
            "    \"cityCode\": \"0776\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"巴中市\",\n" +
            "    \"cityCode\": \"0827\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"毕节市\",\n" +
            "    \"cityCode\": \"0857\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"保山市\",\n" +
            "    \"cityCode\": \"0875\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"宝鸡市\",\n" +
            "    \"cityCode\": \"0917\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"白银市\",\n" +
            "    \"cityCode\": \"0943\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"博尔塔拉蒙古自治州\",\n" +
            "    \"cityCode\": \"0909\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"巴音郭楞蒙古自治州\",\n" +
            "    \"cityCode\": \"0996\",\n" +
            "    \"firstCharacter\": \"B\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"承德市\",\n" +
            "    \"cityCode\": \"0314\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"沧州市\",\n" +
            "    \"cityCode\": \"0317\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"长治市\",\n" +
            "    \"cityCode\": \"0355\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"赤峰市\",\n" +
            "    \"cityCode\": \"0476\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"朝阳市\",\n" +
            "    \"cityCode\": \"0421\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"长春市\",\n" +
            "    \"cityCode\": \"0431\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"常州市\",\n" +
            "    \"cityCode\": \"0519\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"滁州市\",\n" +
            "    \"cityCode\": \"0550\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"池州市\",\n" +
            "    \"cityCode\": \"0566\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"长沙市\",\n" +
            "    \"cityCode\": \"0731\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"常德市\",\n" +
            "    \"cityCode\": \"0736\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"潮州市\",\n" +
            "    \"cityCode\": \"0768\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"崇左市\",\n" +
            "    \"cityCode\": \"0771\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"重庆市\",\n" +
            "    \"cityCode\": \"023\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"成都市\",\n" +
            "    \"cityCode\": \"028\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"楚雄彝族自治州\",\n" +
            "    \"cityCode\": \"0878\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"昌吉回族自治州\",\n" +
            "    \"cityCode\": \"0994\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"嘉义市\",\n" +
            "    \"cityCode\": \"05\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"彰化县\",\n" +
            "    \"cityCode\": \"04\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"嘉义县\",\n" +
            "    \"cityCode\": \"05\",\n" +
            "    \"firstCharacter\": \"C\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"大同市\",\n" +
            "    \"cityCode\": \"0352\",\n" +
            "    \"firstCharacter\": \"D\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"大连市\",\n" +
            "    \"cityCode\": \"0411\",\n" +
            "    \"firstCharacter\": \"D\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"丹东市\",\n" +
            "    \"cityCode\": \"0415\",\n" +
            "    \"firstCharacter\": \"D\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"大庆市\",\n" +
            "    \"cityCode\": \"0459\",\n" +
            "    \"firstCharacter\": \"D\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"德州市\",\n" +
            "    \"cityCode\": \"0534\",\n" +
            "    \"firstCharacter\": \"D\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"东莞市\",\n" +
            "    \"cityCode\": \"0769\",\n" +
            "    \"firstCharacter\": \"D\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"德阳市\",\n" +
            "    \"cityCode\": \"0838\",\n" +
            "    \"firstCharacter\": \"D\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"达州市\",\n" +
            "    \"cityCode\": \"0818\",\n" +
            "    \"firstCharacter\": \"D\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"大理白族自治州\",\n" +
            "    \"cityCode\": \"0872\",\n" +
            "    \"firstCharacter\": \"D\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"德宏傣族景颇族自治州\",\n" +
            "    \"cityCode\": \"0692\",\n" +
            "    \"firstCharacter\": \"D\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"迪庆藏族自治州\",\n" +
            "    \"cityCode\": \"0887\",\n" +
            "    \"firstCharacter\": \"D\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"定西市\",\n" +
            "    \"cityCode\": \"0932\",\n" +
            "    \"firstCharacter\": \"D\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"鄂州市\",\n" +
            "    \"cityCode\": \"0711\",\n" +
            "    \"firstCharacter\": \"E\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"恩施土家族苗族自治州\",\n" +
            "    \"cityCode\": \"0718\",\n" +
            "    \"firstCharacter\": \"E\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"抚顺市\",\n" +
            "    \"cityCode\": \"024\",\n" +
            "    \"firstCharacter\": \"F\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"阜阳市\",\n" +
            "    \"cityCode\": \"0558\",\n" +
            "    \"firstCharacter\": \"F\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"福州市\",\n" +
            "    \"cityCode\": \"0591\",\n" +
            "    \"firstCharacter\": \"F\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"抚州市\",\n" +
            "    \"cityCode\": \"0794\",\n" +
            "    \"firstCharacter\": \"F\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"佛山市\",\n" +
            "    \"cityCode\": \"0757\",\n" +
            "    \"firstCharacter\": \"F\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"防城港市\",\n" +
            "    \"cityCode\": \"0770\",\n" +
            "    \"firstCharacter\": \"F\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"赣州市\",\n" +
            "    \"cityCode\": \"0797\",\n" +
            "    \"firstCharacter\": \"G\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"广州市\",\n" +
            "    \"cityCode\": \"020\",\n" +
            "    \"firstCharacter\": \"G\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"桂林市\",\n" +
            "    \"cityCode\": \"0773\",\n" +
            "    \"firstCharacter\": \"G\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"贵港市\",\n" +
            "    \"cityCode\": \"0775\",\n" +
            "    \"firstCharacter\": \"G\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"广元市\",\n" +
            "    \"cityCode\": \"0839\",\n" +
            "    \"firstCharacter\": \"G\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"广安市\",\n" +
            "    \"cityCode\": \"0826\",\n" +
            "    \"firstCharacter\": \"G\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"贵阳市\",\n" +
            "    \"cityCode\": \"0851\",\n" +
            "    \"firstCharacter\": \"G\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"甘南藏族自治州\",\n" +
            "    \"cityCode\": \"0941\",\n" +
            "    \"firstCharacter\": \"G\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"邯郸市\",\n" +
            "    \"cityCode\": \"0310\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"衡水市\",\n" +
            "    \"cityCode\": \"0318\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"呼和浩特市\",\n" +
            "    \"cityCode\": \"0471\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"呼伦贝尔市\",\n" +
            "    \"cityCode\": \"0470\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"兴安盟\",\n" +
            "    \"cityCode\": \"0482\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"葫芦岛市\",\n" +
            "    \"cityCode\": \"0429\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"哈尔滨市\",\n" +
            "    \"cityCode\": \"0451\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"鹤岗市\",\n" +
            "    \"cityCode\": \"0468\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"黑河市\",\n" +
            "    \"cityCode\": \"0456\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"淮安市\",\n" +
            "    \"cityCode\": \"0517\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"杭州市\",\n" +
            "    \"cityCode\": \"0571\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"湖州市\",\n" +
            "    \"cityCode\": \"0572\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"合肥市\",\n" +
            "    \"cityCode\": \"0551\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"淮南市\",\n" +
            "    \"cityCode\": \"0554\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"淮北市\",\n" +
            "    \"cityCode\": \"0561\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"黄山市\",\n" +
            "    \"cityCode\": \"0559\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"菏泽市\",\n" +
            "    \"cityCode\": \"0530\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"鹤壁市\",\n" +
            "    \"cityCode\": \"0392\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"黄石市\",\n" +
            "    \"cityCode\": \"0714\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"黄冈市\",\n" +
            "    \"cityCode\": \"0713\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"衡阳市\",\n" +
            "    \"cityCode\": \"0734\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"怀化市\",\n" +
            "    \"cityCode\": \"0745\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"海口市\",\n" +
            "    \"cityCode\": \"0898\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"汉中市\",\n" +
            "    \"cityCode\": \"0916\",\n" +
            "    \"firstCharacter\": \"H\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"伊犁哈萨克自治州\",\n" +
            "    \"cityCode\": \"0999\",\n" +
            "    \"firstCharacter\": \"I\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"晋城市\",\n" +
            "    \"cityCode\": \"0356\",\n" +
            "    \"firstCharacter\": \"J\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"晋中市\",\n" +
            "    \"cityCode\": \"0354\",\n" +
            "    \"firstCharacter\": \"J\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"吉林市\",\n" +
            "    \"cityCode\": \"0432\",\n" +
            "    \"firstCharacter\": \"J\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"金华市\",\n" +
            "    \"cityCode\": \"0579\",\n" +
            "    \"firstCharacter\": \"J\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"济南市\",\n" +
            "    \"cityCode\": \"0531\",\n" +
            "    \"firstCharacter\": \"J\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"焦作市\",\n" +
            "    \"cityCode\": \"0391\",\n" +
            "    \"firstCharacter\": \"J\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"开封市\",\n" +
            "    \"cityCode\": \"0378\",\n" +
            "    \"firstCharacter\": \"K\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"昆明市\",\n" +
            "    \"cityCode\": \"0871\",\n" +
            "    \"firstCharacter\": \"K\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"克孜勒苏柯尔克孜自治州\",\n" +
            "    \"cityCode\": \"0908\",\n" +
            "    \"firstCharacter\": \"K\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"九龙\",\n" +
            "    \"cityCode\": \"00852\",\n" +
            "    \"firstCharacter\": \"K\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"临汾市\",\n" +
            "    \"cityCode\": \"0357\",\n" +
            "    \"firstCharacter\": \"L\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"丽水市\",\n" +
            "    \"cityCode\": \"0578\",\n" +
            "    \"firstCharacter\": \"L\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"临沂市\",\n" +
            "    \"cityCode\": \"0539\",\n" +
            "    \"firstCharacter\": \"L\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"洛阳市\",\n" +
            "    \"cityCode\": \"0379\",\n" +
            "    \"firstCharacter\": \"L\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"拉萨市\",\n" +
            "    \"cityCode\": \"0891\",\n" +
            "    \"firstCharacter\": \"L\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"牡丹江市\",\n" +
            "    \"cityCode\": \"0453\",\n" +
            "    \"firstCharacter\": \"M\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"马鞍山市\",\n" +
            "    \"cityCode\": \"0555\",\n" +
            "    \"firstCharacter\": \"M\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"茂名市\",\n" +
            "    \"cityCode\": \"0668\",\n" +
            "    \"firstCharacter\": \"M\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"南京市\",\n" +
            "    \"cityCode\": \"025\",\n" +
            "    \"firstCharacter\": \"N\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"南通市\",\n" +
            "    \"cityCode\": \"0513\",\n" +
            "    \"firstCharacter\": \"N\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"宁波市\",\n" +
            "    \"cityCode\": \"0574\",\n" +
            "    \"firstCharacter\": \"N\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"莆田市\",\n" +
            "    \"cityCode\": \"0594\",\n" +
            "    \"firstCharacter\": \"P\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"平顶山市\",\n" +
            "    \"cityCode\": \"0375\",\n" +
            "    \"firstCharacter\": \"P\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"衢州市\",\n" +
            "    \"cityCode\": \"0570\",\n" +
            "    \"firstCharacter\": \"Q\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"泉州市\",\n" +
            "    \"cityCode\": \"0595\",\n" +
            "    \"firstCharacter\": \"Q\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"青岛市\",\n" +
            "    \"cityCode\": \"0532\",\n" +
            "    \"firstCharacter\": \"Q\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"庆阳市\",\n" +
            "    \"cityCode\": \"0934\",\n" +
            "    \"firstCharacter\": \"Q\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"日照市\",\n" +
            "    \"cityCode\": \"0633\",\n" +
            "    \"firstCharacter\": \"R\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"石家庄市\",\n" +
            "    \"cityCode\": \"0311\",\n" +
            "    \"firstCharacter\": \"S\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"朔州市\",\n" +
            "    \"cityCode\": \"0349\",\n" +
            "    \"firstCharacter\": \"S\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"沈阳市\",\n" +
            "    \"cityCode\": \"024\",\n" +
            "    \"firstCharacter\": \"S\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"苏州市\",\n" +
            "    \"cityCode\": \"0512\",\n" +
            "    \"firstCharacter\": \"S\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"十堰市\",\n" +
            "    \"cityCode\": \"0719\",\n" +
            "    \"firstCharacter\": \"S\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"三沙市\",\n" +
            "    \"cityCode\": \"0898\",\n" +
            "    \"firstCharacter\": \"S\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"石嘴山市\",\n" +
            "    \"cityCode\": \"0952\",\n" +
            "    \"firstCharacter\": \"S\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"天津市\",\n" +
            "    \"cityCode\": \"022\",\n" +
            "    \"firstCharacter\": \"T\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"唐山市\",\n" +
            "    \"cityCode\": \"0315\",\n" +
            "    \"firstCharacter\": \"T\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"太原市\",\n" +
            "    \"cityCode\": \"0351\",\n" +
            "    \"firstCharacter\": \"T\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"台州市\",\n" +
            "    \"cityCode\": \"0576\",\n" +
            "    \"firstCharacter\": \"T\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"吐鲁番地区\",\n" +
            "    \"cityCode\": \"0995\",\n" +
            "    \"firstCharacter\": \"T\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"乌兰察布市\",\n" +
            "    \"cityCode\": \"0474\",\n" +
            "    \"firstCharacter\": \"W\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"乌鲁木齐市\",\n" +
            "    \"cityCode\": \"0991\",\n" +
            "    \"firstCharacter\": \"W\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"潍坊市\",\n" +
            "    \"cityCode\": \"0536\",\n" +
            "    \"firstCharacter\": \"W\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"威海市\",\n" +
            "    \"cityCode\": \"0631\",\n" +
            "    \"firstCharacter\": \"W\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"武汉市\",\n" +
            "    \"cityCode\": \"0022222\",\n" +
            "    \"firstCharacter\": \"W\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"邢台市\",\n" +
            "    \"cityCode\": \"0319\",\n" +
            "    \"firstCharacter\": \"X\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"忻州市\",\n" +
            "    \"cityCode\": \"0350\",\n" +
            "    \"firstCharacter\": \"X\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"信阳市\",\n" +
            "    \"cityCode\": \"0376\",\n" +
            "    \"firstCharacter\": \"X\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"阳泉市\",\n" +
            "    \"cityCode\": \"0353\",\n" +
            "    \"firstCharacter\": \"Y\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"运城市\",\n" +
            "    \"cityCode\": \"0359\",\n" +
            "    \"firstCharacter\": \"Y\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"营口市\",\n" +
            "    \"cityCode\": \"0417\",\n" +
            "    \"firstCharacter\": \"Y\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"宜昌市\",\n" +
            "    \"cityCode\": \"0717\",\n" +
            "    \"firstCharacter\": \"Y\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"岳阳市\",\n" +
            "    \"cityCode\": \"0730\",\n" +
            "    \"firstCharacter\": \"Y\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"玉溪市\",\n" +
            "    \"cityCode\": \"0877\",\n" +
            "    \"firstCharacter\": \"Y\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"舟山群岛新区\",\n" +
            "    \"cityCode\": \"0580\",\n" +
            "    \"firstCharacter\": \"Z\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"郑州市\",\n" +
            "    \"cityCode\": \"0371\",\n" +
            "    \"firstCharacter\": \"Z\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"肇庆市\",\n" +
            "    \"cityCode\": \"0758\",\n" +
            "    \"firstCharacter\": \"Z\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"张掖市\",\n" +
            "    \"cityCode\": \"0936\",\n" +
            "    \"firstCharacter\": \"Z\"\n" +
            "  }\n" +
            "]";

    public static void main(String[] args) {
        try {
            boolean flag = false;
            Map<String,String> map = new HashMap<>();
            BufferedReader in = new BufferedReader(new FileReader("D:\\aqi\\aqi\\src\\main\\resources\\city.txt"));
            String str;
            while ((str = in.readLine()) != null) {
                String[] split = str.split("\t");
                String name1 = split[1];
                int start = name1.indexOf("(");
                int end = name1.indexOf(")");
                String name = name1;
                if(start != -1 && end != -1){
                    name = name1.substring(start+1,end);
                }
                map.put(name,split[0]);
            }
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            ArrayList<JSONObject> list = (ArrayList<JSONObject>) JSONObject.parseArray(json).toJavaList(JSONObject.class);
            ArrayList<JSONObject> list1 = (ArrayList<JSONObject>) list.clone();
            Iterator<JSONObject> iterator1 = list.iterator();
            while (iterator1.hasNext()){
                JSONObject obj = iterator1.next();
                for (Map.Entry<String, String> e : map.entrySet()){
                    //System.out.println(obj.getString("name")+" "+e.getKey());
                    if(obj.getString("name").contains(e.getKey())){
                        obj.put("cityCode", e.getValue());
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    System.out.println(obj.getString("name"));
                    list1.remove(obj);
                }
                flag = false;
            }
            System.out.println(JSONObject.toJSONString(list1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

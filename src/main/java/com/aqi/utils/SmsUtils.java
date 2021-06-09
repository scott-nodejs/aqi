package com.aqi.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.utils.StringUtils;

import com.aqi.configer.exception.ResultException;
import com.aqi.global.SmsConstant;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
public class SmsUtils {

    public static String common(String phoneNumber, String templateParam){
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                SmsConstant.SMS_KEY,
                SmsConstant.SMS_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);
        try {
            if(!isMobile(phoneNumber)){
                throw new ResultException(400, "手机号格式不正确");
            }
            CommonRequest request = new CommonRequest();
            request.setSysMethod(MethodType.POST);
            request.setSysDomain("dysmsapi.aliyuncs.com");
            request.setSysVersion("2017-05-25");
            request.setSysAction("SendSms");
            request.putQueryParameter("RegionId", "cn-hangzhou");
            request.putQueryParameter("PhoneNumbers", phoneNumber);
            request.putQueryParameter("SignName", SmsConstant.signName);
            request.putQueryParameter("TemplateCode", SmsConstant.templateCode);
            request.putQueryParameter("TemplateParam", templateParam);

            CommonResponse response = client.getCommonResponse(request);
            log.info(phoneNumber + " 发送短信: " +response.getData());
            return response.getData();
        } catch (ServerException e) {
            log.error("短信发送服务端异常: ", e);
        } catch (ClientException e) {
            log.error("短信发送客户端异常: ", e);
        }
        return null;
    }

    private static final String REGEX_MOBILE = "((\\+86|0086)?\\s*)((134[0-8]\\d{7})|(((13([0-3]|[5-9]))|(14[5-9])|15([0-3]|[5-9])|(16(2|[5-7]))|17([0-3]|[5-8])|18[0-9]|19(1|[8-9]))\\d{8})|(14(0|1|4)0\\d{7})|(1740([0-5]|[6-9]|[10-12])\\d{7}))";

    /**
     * 校验手机号
     *
     * @param phone 手机号
     * @return boolean true:是  false:否
     */
    public static boolean isMobile(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        return Pattern.matches(REGEX_MOBILE, phone);
    }
}

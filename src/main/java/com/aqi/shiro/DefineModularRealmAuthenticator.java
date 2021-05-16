package com.aqi.shiro;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Map;

public class DefineModularRealmAuthenticator extends ModularRealmAuthenticator {

    private static final Logger log = LoggerFactory.getLogger(DefineModularRealmAuthenticator.class);

    /**
     * 将Realm的实现类作为Map传入,这样便可以分清除前后台登录
     */
    private Map<String, Object> defineRealms;

    /**
     * 调用单个Realm来进行验证
     */
    @Override
    protected AuthenticationInfo doSingleRealmAuthentication(Realm realm,
                                                             AuthenticationToken token) {
        if (!realm.supports(token)) {
            String msg = "Realm [" + realm + "] does not support authentication token [" + token + "].  Please ensure that the appropriate Realm implementation is " + "configured correctly or that the realm accepts AuthenticationTokens of this type.";
            throw new UnsupportedTokenException(msg);
        }
        AuthenticationInfo info = null;
        try {
            info = realm.getAuthenticationInfo(token);
            if (info == null) {
                String msg = "Realm [" + realm + "] was unable to find account data for the " + "submitted AuthenticationToken [" + token + "].";
                throw new UnknownAccountException(msg);
            }
        }  catch (IncorrectCredentialsException e) {
            throw e;
        }  catch (UnknownAccountException e) {
            throw e;
        }catch (Throwable throwable) {
            if (log.isDebugEnabled()) {
                String msg = "Realm [" + realm + "] threw an exception during a multi-realm authentication attempt:";
                log.debug(msg,throwable );
            }

        }

        return info;
    }

    /**
     * 判断Realm是不是null
     */
    @Override
    protected void assertRealmsConfigured() throws IllegalStateException {
        defineRealms = getDefineRealms();
        if (CollectionUtils.isEmpty(defineRealms)) {
            String msg = "Configuration error:  No realms have been configured!  One or more realms must be "
                    + "present to execute an authentication attempt.";
            throw new IllegalStateException(msg);
        }
    }
    /**
     * 这个方法比较重要,用来判断此次调用是前台还是后台
     */
    @Override
    protected AuthenticationInfo doAuthenticate(
            AuthenticationToken authenticationToken)
            throws AuthenticationException {
        assertRealmsConfigured();
        CaptchaAuthenticationToken token = (CaptchaAuthenticationToken) authenticationToken;
        Realm realm = null;
        // 前端登录
        if (StringUtils.equals(token.getLoginType(), LoginEnum.CUSTOMER.toString())) {
            realm = (Realm) defineRealms.get("customerRealm");
        }
        // 后台登录
        if (StringUtils.equals(token.getLoginType(), LoginEnum.ADMIN.toString())) {
            realm = (Realm) defineRealms.get("adminRealm");
        }
        if(realm==null){
            return null;
        }
        return doSingleRealmAuthentication(realm, authenticationToken);

    }

    public void setDefineRealms(Map<String, Object> defineRealms) {
        this.defineRealms = defineRealms;
    }

    public Map<String, Object> getDefineRealms() {
        return defineRealms;
    }
}

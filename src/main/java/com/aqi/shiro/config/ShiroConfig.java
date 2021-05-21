//package com.aqi.shiro.config;
//
//import com.aqi.shiro.*;
//import com.aqi.shiro.config.properties.ShiroProjectProperties;
//import net.sf.ehcache.CacheManager;
//import org.apache.shiro.cache.ehcache.EhCacheManager;
//import org.apache.shiro.codec.Base64;
//import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
//import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
//import org.apache.shiro.web.mgt.CookieRememberMeManager;
//import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
//import org.apache.shiro.web.servlet.SimpleCookie;
//import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import javax.servlet.Filter;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
///**
// * @author lucong
// * @date 2020/01/23
// */
//@Configuration
//public class ShiroConfig {
//    @Bean
//    public UserAuthFilter userAuthFilter(RedisTemplate<String, Object> redisTemplate){
//        return new UserAuthFilter(redisTemplate);
//    }
//
//    @Bean
//    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager securityManager,UserAuthFilter userAuthFilter) {
//        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
//        shiroFilterFactoryBean.setSecurityManager(securityManager);
//
//        /**
//         * 添加自定义拦截器，重写user认证方式，处理session超时问题
//         */
//        HashMap<String, Filter> myFilters = new HashMap<>();
//        myFilters.put("userAuth", userAuthFilter);
//        shiroFilterFactoryBean.setFilters(myFilters);
//
//        /**
//         *  过滤规则（注意优先级）
//         *  —anon 无需认证(登录)可访问
//         * 	—authc 必须认证才可访问
//         * 	—perms[标识] 拥有资源权限才可访问
//         * 	—role 拥有角色权限才可访问
//         * 	—user 认证和自动登录可访问
//         */
//        LinkedHashMap<String, String> filterMap = new LinkedHashMap<>();
//        filterMap.put("/**", "anon");
//
//        // 设置过滤规则
//        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
//        // 设置登录页面
//        shiroFilterFactoryBean.setLoginUrl("/login");
//        // 未授权错误页面
//        shiroFilterFactoryBean.setUnauthorizedUrl("/noAuth");
//
//        return shiroFilterFactoryBean;
//    }
//
//    @Bean
//    public DefaultWebSecurityManager getDefaultWebSecurityManager(AuthRealm authRealm,
//                                                                  DefineModularRealmAuthenticator defineModularRealmAuthenticator,
//                                                                  DefaultWebSessionManager sessionManager,
//                                                                  CookieRememberMeManager rememberMeManager) {
//        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
//        securityManager.setAuthenticator(defineModularRealmAuthenticator);
//        securityManager.setRealm(authRealm);
//        securityManager.setSessionManager(sessionManager);
//        securityManager.setRememberMeManager(rememberMeManager);
//        return securityManager;
//    }
//
//    /**
//     * 自定义前后台登录认证机制
//     * @return
//     */
//    @Bean
//    public DefineModularRealmAuthenticator defineModularRealmAuthenticator(AuthRealm authRealm){
//        DefineModularRealmAuthenticator defineModularRealmAuthenticator = new DefineModularRealmAuthenticator();
//        Map<String, Object> map = new HashMap<>();
//        map.put("customerRealm",authRealm);
//        defineModularRealmAuthenticator.setDefineRealms(map);
//        return defineModularRealmAuthenticator;
//    }
//
//    /**
//     * 自定义的Realm
//     */
//    @Bean
//    public AuthRealm getRealm(EhCacheManager ehCacheManager) {
//        AuthRealm authRealm = new AuthRealm();
//        authRealm.setCacheManager(ehCacheManager);
//        return authRealm;
//    }
//
//    @Bean
//    public CacheManager cacheManager(){
//        return new CacheManager();
//    }
//
//    /**
//     * 缓存管理器-使用Ehcache实现缓存
//     */
//    @Bean
//    public EhCacheManager ehCacheManager(CacheManager cacheManager) {
//        EhCacheManager ehCacheManager = new EhCacheManager();
//        ehCacheManager.setCacheManager(cacheManager);
//        return ehCacheManager;
//    }
//
//    /**
//     * session管理器
//     */
//    @Bean
//    public DefaultWebSessionManager getDefaultWebSessionManager(EhCacheManager ehCacheManager, ShiroProjectProperties properties, RedisSessionDAO redisSessionDAO) {
//        DefaultWebSessionManager sessionManager = new MyShiroSessionManager();
//        sessionManager.setCacheManager(ehCacheManager);
//        sessionManager.setGlobalSessionTimeout(properties.getGlobalSessionTimeout() * 1000);
//        sessionManager.setSessionValidationInterval(properties.getSessionValidationInterval() * 1000);
//        sessionManager.setDeleteInvalidSessions(true);
//        sessionManager.setSessionDAO(redisSessionDAO);
//        sessionManager.validateSessions();
//        // 去掉登录页面地址栏jsessionid
//        sessionManager.setSessionIdUrlRewritingEnabled(false);
//        return sessionManager;
//    }
//
//    /**
//     * rememberMe管理器
//     */
//    @Bean
//    public CookieRememberMeManager rememberMeManager(SimpleCookie rememberMeCookie) {
//        CookieRememberMeManager manager = new CookieRememberMeManager();
//        manager.setCipherKey(Base64.decode("WcfHGU25gNnTxTlmJMeSpw=="));
//        manager.setCookie(rememberMeCookie);
//        return manager;
//    }
//
//    /**
//     * 创建一个简单的Cookie对象
//     */
//    @Bean
//    public SimpleCookie rememberMeCookie(ShiroProjectProperties properties) {
//        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
//        simpleCookie.setHttpOnly(true);
//        // cookie记住登录信息时间，默认7天
//        simpleCookie.setMaxAge(properties.getRememberMeTimeout() * 24 * 60 * 60);
//        return simpleCookie;
//    }
//
//    /**
//     * 启用shrio授权注解拦截方式，AOP式方法级权限检查
//     */
//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
//        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
//                new AuthorizationAttributeSourceAdvisor();
//        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
//        return authorizationAttributeSourceAdvisor;
//    }
//
//}

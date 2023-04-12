package com.secrity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;


@Configuration
@EnableResourceServer
public class ResouceServerConfig extends ResourceServerConfigurerAdapter {
    //注入授权的tokenStore
    @Autowired
    private TokenStore tokenStore;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("resource1") //资源服务id，资源服务应用的唯一标识
//                .tokenServices(resourceServerTokenServices())  //用来实现令牌校验
                .tokenStore(tokenStore)  //使用授权服务的jwttokenStore 来解析token
                .stateless(true);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        //设置哪些资源需要token验证后才能访问)
        //会产生oauth2一个filter Chain，该过滤器链优先级较大，如果匹配路径相同，会覆盖security的http配置

        /*
        * 默认拦截路径
        *       1：与认证服务在一个应用：除oauth2相关路径不拦截，其他路径都拦截
        *       2：资源服务单独一个应用：拦截所有路径
        * */
        http.antMatcher("/resource/**")  //设置资源服务过滤器链拦截的路径
                .authorizeRequests()
                    .antMatchers("/resource/index").permitAll()  // 哪些资源不需要token也能访问
                    .antMatchers("/resource/manage/aa").access("#oauth2.hasScope('all')") //请求携带的token必须拥有all授权才可以访问此资源
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
    }
//
//    @Bean
//    public ResourceServerTokenServices resourceServerTokenServices(){
//        RemoteTokenServices remoteTokenServices=new RemoteTokenServices();
//        remoteTokenServices.setCheckTokenEndpointUrl("http://127.0.0.1:30000/oauth/oauth/check_token");
//        remoteTokenServices.setClientId("cl1");
//        remoteTokenServices.setClientSecret("123");
//        return remoteTokenServices;
//    }
}

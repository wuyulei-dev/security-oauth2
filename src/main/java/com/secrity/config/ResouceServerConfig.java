package com.secrity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;


@Configuration
@EnableResourceServer
public class ResouceServerConfig extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("resource1") //资源服务id，资源服务应用的唯一标识
                .tokenServices(resourceServerTokenServices())  //用来实现令牌访问服务
                .stateless(true);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        //会产生oauth2自己的一个filter Chain，

        //用于保护oauth要开放的资源(哪些需要token验证后才能访问)
        http.authorizeRequests()
                .antMatchers("/oauth/**").permitAll()  // 资源服务器和认证服务器在一个项目中需设置/oauth/**的接口不需要授权就可以访问
                .antMatchers("/index").permitAll()  // 哪些资源不需要token也能访问
                .antMatchers("/**").access("#oauth2.hasScope('all')") //请求携带的token必须拥有all授权才可以访问此资源
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
    }

    @Bean
    public ResourceServerTokenServices resourceServerTokenServices(){
        RemoteTokenServices remoteTokenServices=new RemoteTokenServices();
        remoteTokenServices.setCheckTokenEndpointUrl("http://127.0.0.1:30000/oauth/oauth/check_token");
        remoteTokenServices.setClientId("cl1");
        remoteTokenServices.setClientSecret("123");
        return remoteTokenServices;
    }
}

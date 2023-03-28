/*
 * @(#)AuthorizationServerConfigurer.java
 * Copyright (C) 2020 Neusoft Corporation All rights reserved.
 *
 * VERSION        DATE       BY              CHANGE/COMMENT
 * ----------------------------------------------------------------------------
 * @version 1.00  2023年3月28日 wwp-pc          初版
 *
 */
package com.secrity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer   //Spring Security打开OAuth认证服务
public class AuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter{
    
    //配置客户端信息后会自动创建
    @Autowired
    private ClientDetailsService clientDetailsService;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    /**
     * 配置客戶端信息 客户端详情信息写死在这里或者是通过数据库来存储调取详情信息。
     */
    @Override
    public void configure(
        ClientDetailsServiceConfigurer clients) throws Exception {
        /*
         * ClientDetailsServiceConfigurer能够使用内存或者JDBC来实现客户端详情服务(ClientDetailsService)
         * ClientDetailsService负责查找ClientDetails，一个ClientDetails代表一个需要接入的第三方应用，
         * 例如 我们上面提到的OAuth流程中的百度。ClientDetails中有几个重要的属性如下：
         *      clientId:用来标识客户的ID。必须。 
         *      secret: 客户端安全码，如果有的话。在微信登录中就是必须的。 
         *      scope：用来限制客户端的访问范围，如果是空(默认)的话，那么客户端拥有全部的访问范围。
         *      authrizedGrantTypes：此客户端可以使用的授权类型，默认为空。在微信登录中，只支持
         *              authorization_code这一种。 
         *      authorities：此客户端可以使用的权限(基于Spring Securityauthorities) 
         *      redirectUris：回调地址。授权服务会往该回调地址推送此客户端相关的信息。
         * ClientDetails客户端详情，能够在应用程序运行的时候进行更新，可以通过访问底层的存储服务(例如访问mysql，就提供了JdbcClientDetailsService)
         * 或者通过自己实现ClientRegisterationService接口(同时也可以实现ClientDetailsService接口)来进行定制。
         */
        //内存方式存儲客戶端信息
        clients.inMemory()
            .withClient("cl1")  //客戶端id
            .secret(new BCryptPasswordEncoder().encode("123")) //密匙
            .scopes("all") //授權範圍
            .redirectUris("http://www.baidu.com")  //重定向url
            .accessTokenValiditySeconds(3600)  //token有效時間
            .refreshTokenValiditySeconds(3600)  //refresh有效時間
            .authorizedGrantTypes("authorization_code","password", "refresh_token"); //支持的授權類型
        
     // 加载自定义的客户端管理服务
     // clients.withClientDetails(clientDetailsService)
    }
    
    
    /**
     * 用来配置令牌服务(tokenservices)和令牌访问端点
     */
    @Override
    public void configure(
        AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        /*
         * TokenService配置：
         * 
         * AuthorizationServerTokenService接口定义了一些对令牌进行管理的操作
         *      默认实现类：DefaultTokenServices。 你可以使用它来修改令牌的格式和令牌的存储。默认情况下，他在创建一个
         *              令牌时，是使用随机值来进行填充的。这个类需要依赖spring容器中的一个TokenStore接口实现类来定制令牌持久化。
         *      TokenStore的实现类：
         *              InMemoryTokenStore：TokenStore默认实现类。token存储在内存
         *              JdbcTokenStore：这是一个基于JDBC的实现类，令牌会被保存到关系型数据库中。需要使用spring boot jdbc相关的依赖 
         *              JwtTokenStore： jwt作为令牌
         */
        endpoints.tokenServices(tokenService());
        /*
         * 令牌访问端点配置：
         * 
         * AuthorizationServerEndpointsConfigurer对于不同类型的授权类型，也需要配置不同的属性。
         * authenticationManager：密码模式。需要指定authenticationManager 认证管理器对象来进行鉴权。
         * userDetailsService：用户主体管理服务。 
         * authorizationCodeServices： 授权码类型模式。
         * implicitGrantService：这个属性用于设置隐式授权模式的状态。
         * tokenGranter：如果设置了这个东东(即TokenGranter接口的实现类)，那么授权将会全部交由你
         * 来自己掌控，并且会忽略掉以上几个属性。这个属性一般是用作深度拓展用途的，即标准的四种授 权模式已经满足不了你的需求时，才会考虑使用这个。
         * 配置授权端点的URL：
         *         框架默认的URL链接有如下几个：
         *          /oauth/authorize ： 授权端点
         *          /auth/token ：令牌端点 
         *          /oauth/confirm_access ： 用户确认授权提交的端点
         *          /oauth/error : 授权服务错误信息端点。
         *          /oauth/check_token ： 用于资源服务访问的令牌进行解析的端点
         *          /oauth/token_key ：使用Jwt令牌需要用到的提供公有密钥的端点。 
         *          需要注意的是，这几个授权端点应该被Spring Security保护起来只供授权用户访问。
         */
        
        endpoints.authenticationManager(authenticationManager)  //密码模式配置:认证用户身份
                 .userDetailsService(userDetailsService)        //密码模式配置
                 .authorizationCodeServices(authorizationCodeServices()); //授权码模式配置：用来管理授权码
    }
    
    /**
     * 用来配置令牌端点的安全约束.
     */
    @Override
    public void configure(
        AuthorizationServerSecurityConfigurer security) throws Exception {
        //用来配置令牌端点(Token Endpoint)的安全约束
        super.configure(security);
    }

   
    //toen存储策略
    @Bean
    public JwtTokenStore tokenStore() {
//        return new InMemoryTokenStore();  基于内存
        return new JwtTokenStore(jwtAccessTokenConverter()); //基于token
    }

  @Bean
  public JwtAccessTokenConverter jwtAccessTokenConverter() {
      JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
      jwtAccessTokenConverter.setSigningKey("test_key");
      return jwtAccessTokenConverter;
  }
    
    //tokensevice服务
    @Bean
    public AuthorizationServerTokenServices tokenService() {
        DefaultTokenServices tokenService = new DefaultTokenServices();
        tokenService.setClientDetailsService(clientDetailsService);
        tokenService.setTokenStore(tokenStore());
        //使用jwt 必须配置不然不会办法jtwToken
        tokenService.setTokenEnhancer(jwtAccessTokenConverter());
        tokenService.setSupportRefreshToken(true);
        tokenService.setAccessTokenValiditySeconds(60);
        tokenService.setRefreshTokenValiditySeconds(7200);
        return tokenService;
    }
    
    //授权码存储策略
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }
}

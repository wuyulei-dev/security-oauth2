/*
 * @(#)CustomTokenEnhancer.java
 * Copyright (C) 2020 Neusoft Corporation All rights reserved.
 *
 * VERSION        DATE       BY              CHANGE/COMMENT
 * ----------------------------------------------------------------------------
 * @version 1.00  2023年4月10日 wwp-pc          初版
 *
 */
package com.secrity.security;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//自定义TokenEnhancer 对令牌内容进行增强
@Component
public class CustomTokenEnhancer implements TokenEnhancer{

    @Override
    public OAuth2AccessToken enhance(
        OAuth2AccessToken accessToken,OAuth2Authentication authentication) {
        ObjectMapper objectMapper = new ObjectMapper();
        //获取用户信息
        Object principal = authentication.getPrincipal();
        try {
            //将用户信息写入jwt 载体中
            String value = objectMapper.writeValueAsString(principal);
            Map map = objectMapper.readValue(value, Map.class);
            //移除一些不要的属性
            map.remove("password");
            map.remove("authorities");
            map.remove("enabled");
            Map<String, Object> additionalInformation=new HashMap<>();
            additionalInformation.put("user_info", map);
            ((DefaultOAuth2AccessToken)accessToken).setAdditionalInformation(additionalInformation);
        } catch (IOException e) {

        }
        return accessToken;
    }

}

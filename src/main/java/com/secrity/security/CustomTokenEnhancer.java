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

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

//自定义TokenEnhancer 对令牌内容进行增强
@Component
public class CustomTokenEnhancer implements TokenEnhancer{

    @Override
    public OAuth2AccessToken enhance(
        OAuth2AccessToken accessToken,OAuth2Authentication authentication) {
        
        Map<String, Object> additionalInformation=new HashMap<>();
        additionalInformation.put("userId", "123456");
        ((DefaultOAuth2AccessToken)accessToken).setAdditionalInformation(additionalInformation);
        return accessToken;
    }

}

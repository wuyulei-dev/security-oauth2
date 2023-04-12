/*
 * @(#)HomeContrroler.java
 * Copyright (C) 2020 Neusoft Corporation All rights reserved.
 *
 * VERSION        DATE       BY              CHANGE/COMMENT
 * ----------------------------------------------------------------------------
 * @version 1.00  2023年3月21日 wwp-pc          初版
 *
 */
package com.secrity.controller;

import org.apache.tomcat.util.descriptor.web.ContextHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/resource")
public class HomeContrroler {

    @RequestMapping("/index")
    public String index() {
        System.out.println("sadkfljjjjjjjjj");
       return "main.html";
    }


    @RequestMapping("/manage/aa")
    @ResponseBody
    public String aa() {
        System.out.println("aa");
        return "aa";
    }
}

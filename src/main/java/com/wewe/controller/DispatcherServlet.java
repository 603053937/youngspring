package com.wewe.controller;


import com.wewe.controller.fronted.MainPageController;
import com.wewe.controller.superadmin.HeadLineOperationController;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 *  仅有一个servlet
 *  1. 拦截所有请求
 *  2. 解析请求
 *  3. 派发给对应的controller里面的方法进行处理
 */
@WebServlet("/")
public class DispatcherServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("request path is : " + req.getServletPath());
        System.out.println("request method is : " + req.getMethod());
        if (req.getServletPath().equals("/frontend/getmainpageinfo") && req.getMethod() == "GET") {
            new MainPageController().getMainPageInfo(req, resp);
        } else if (req.getServletPath().equals("/superadmin/addheadline") && req.getMethod() == "POST") {
            new HeadLineOperationController().addHeadLine(req, resp);
        }
    }
}

package com.wewe;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebServlet("/hello") //指定访问路径
public class HelloServlet extends HttpServlet { //继承HttpServlet,才可调用Servlet框架
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = "spring框架青春版";
        log.debug("name is \t" + name);
        //设置指定元素上的某个属性值。如果属性已经存在，则更新该值；否则，使用指定的名称和值添加一个新的属性。
        req.setAttribute("name", name);
        //从客户端获取请求request，指定转发的jsp地址
        //forward是用来传递request和response的
        req.getRequestDispatcher("/WEB-INF/jsp/hello.jsp").forward(req,resp);
    }
}

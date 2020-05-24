package org.youngspringframework.mvc;


import com.wewe.controller.fronted.MainPageController;
import com.wewe.controller.superadmin.HeadLineOperationController;
import org.youngspringframework.aop.AspectWeaver;
import org.youngspringframework.core.BeanContainer;
import org.youngspringframework.inject.DependencyInjector;
import org.youngspringframework.mvc.processor.RequestProcessor;
import org.youngspringframework.mvc.processor.impl.ControllerRequestProcessor;
import org.youngspringframework.mvc.processor.impl.JspRequestProcessor;
import org.youngspringframework.mvc.processor.impl.PreRequestProcessor;
import org.youngspringframework.mvc.processor.impl.StaticResourceRequestProcessor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/*
 *  仅有一个servlet
 *  1. 拦截所有请求
 *  2. 解析请求
 *  3. 派发给对应的controller里面的方法进行处理
 */
// @WebServlet("/") 这样配置时，不过滤（放行）jsp文件。只有jsp不进控制器（servlet），其他都进
// @WebServlet("/*") 过滤所有文件。
@WebServlet("/*")
public class DispatcherServlet extends HttpServlet {
    List<RequestProcessor> PROCESSOR = new ArrayList<>();
    @Override
    public void init() {
        //1.初始化容器
        // 新建容器
        BeanContainer beanContainer = BeanContainer.getInstance();
        // 扫描包 加载beans
        beanContainer.loadBeans("com.imooc");
        // aop横切逻辑的织入
        new AspectWeaver().doAop();
        // 依赖注入
        new DependencyInjector().doIoc();
        //2.初始化请求处理器责任链
        // 请求预处理，包括编码以及路径处理，所以必须第一步执行
        PROCESSOR.add(new PreRequestProcessor());
        // 静态资源请求处理,包括但不限于图片、css、以及js文件等 - DefaultServlet
        // 需要将请求转发到tomcat的DefaultServlet进行处理
        // getServletContext() 获取ServletContext
        // 在tomcat容器中每个Context对应一个web应用
        // Context中存放着Wapper，每个Wapper封装着一个Servlet实例
        // 为了将请求注入到DefaultServlet，需要获得对应的Wrapper
        PROCESSOR.add(new StaticResourceRequestProcessor(getServletContext()));
        // jsp资源请求处理
        PROCESSOR.add(new JspRequestProcessor(getServletContext()));
        // Controller请求处理器，将请求与方法相匹配，耗时较长，所以放在最后一步执行
        PROCESSOR.add(new ControllerRequestProcessor());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        //1.创建责任链对象实例
        RequestProcessorChain requestProcessorChain = new RequestProcessorChain(PROCESSOR.iterator(), req, resp);
        //2.通过责任链模式来依次调用请求处理器对请求进行处理
        requestProcessorChain.doRequestProcessorChain();
        //3.对处理结果进行渲染
        requestProcessorChain.doRender();
    }

}

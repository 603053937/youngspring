package org.youngspringframework.mvc.processor.impl;

import lombok.extern.slf4j.Slf4j;
import org.youngspringframework.mvc.RequestProcessorChain;
import org.youngspringframework.mvc.processor.RequestProcessor;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

/**
 * 静态资源请求处理,包括但不限于图片、css、以及js文件等
 * 需要将请求转发到tomcat的DefaultServlet进行处理
 * 针对业务只有一个servlet,但项目背后需要一定数量的系统及工具集的servlet来支撑
 */
@Slf4j
public class StaticResourceRequestProcessor implements RequestProcessor {
    public static final String DEFAULT_TOMCAT_SERVLET = "default";
    public static final String STATIC_RESOURCE_PREFIX = "/static/";

    //tomcat默认请求派发器RequestDispatcher的名称
    RequestDispatcher defaultDispatcher;

    public StaticResourceRequestProcessor(ServletContext servletContext) {
        //根据名字获取defaultServlet的RequestDispatcher
        this.defaultDispatcher = servletContext.getNamedDispatcher(DEFAULT_TOMCAT_SERVLET);
        if (this.defaultDispatcher == null) {
            throw new RuntimeException("There is no default tomcat servlet");
        }
        log.info("The default servlet for static resource is {}", DEFAULT_TOMCAT_SERVLET);
    }

    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        //1.通过请求路径判断是否是请求的静态资源  静态资源存放在webapp/static目录中
        if(isStaticResource(requestProcessorChain.getRequestPath())){
            //2.如果是静态资源,则将请求转发给default servlet处理
            defaultDispatcher.forward(requestProcessorChain.getRequest(), requestProcessorChain.getResponse());
            // false表明该资源已被当前Processor处理了,不需要再传给下个RequestProcessor处理
            return false;
        }
        // true说明资源未被处理,需要往下传递,让别的RequestProcessor进行处理
        return true;
    }
    // 通过请求路径前缀（目录）是否为静态资源 /static/
    // webapp是根目录,不会出现在请求路径中
    private boolean isStaticResource(String path){
        return path.startsWith(STATIC_RESOURCE_PREFIX);
    }
}

package org.youngspringframework.mvc.processor.impl;

import org.youngspringframework.mvc.RequestProcessorChain;
import org.youngspringframework.mvc.processor.RequestProcessor;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

/**
 * jsp资源请求处理
 */
public class JspRequestProcessor implements RequestProcessor {

    //jsp请求的RequestDispatcher的名称
    private static final String JSP_SERVLET = "jsp";
    //Jsp请求资源路径前缀
    private static final String  JSP_RESOURCE_PREFIX = "/templates/";

    /**
     * jsp的RequestDispatcher,处理jsp资源
     */
    private RequestDispatcher jspServlet;

    public JspRequestProcessor(ServletContext servletContext) {
        // 获取对应的jspServlet的Wrapper实例
        jspServlet = servletContext.getNamedDispatcher(JSP_SERVLET);
        if (null == jspServlet) {
            throw new RuntimeException("there is no jsp servlet");
        }
    }

    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        //1.是否请求的是jsp资源
        if (isJspResource(requestProcessorChain.getRequestPath())) {
            //2.如果是jsp资源，则将请求转发给jsp servlet处理
            jspServlet.forward(requestProcessorChain.getRequest(), requestProcessorChain.getResponse());
            // false表明该资源已被当前Processor处理了，不需要再传给下个RequestProcessor处理
            return false;
        }
        // true说明资源未被处理，需要往下传递，让别的RequestProcessor进行处理
        return true;
    }

    /**
     * 是否请求的是jsp资源
     */
    private boolean isJspResource(String url) {
        return url.startsWith(JSP_RESOURCE_PREFIX);
    }
}


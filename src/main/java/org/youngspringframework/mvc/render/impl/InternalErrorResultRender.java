package org.youngspringframework.mvc.render.impl;

import org.youngspringframework.mvc.RequestProcessorChain;
import org.youngspringframework.mvc.render.ResultRender;

import javax.servlet.http.HttpServletResponse;


/**
 * 内部异常渲染器
 */
public class InternalErrorResultRender implements ResultRender {
    // 遗传信息
    private String errorMsg;
    public InternalErrorResultRender(String  errorMsg){
        this.errorMsg = errorMsg;
    }

    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        requestProcessorChain.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg);
    }

}

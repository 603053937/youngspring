package org.youngspringframework.mvc.render.impl;


import org.youngspringframework.mvc.RequestProcessorChain;
import org.youngspringframework.mvc.render.ResultRender;

/**
 * 默认渲染器
 */
public class DefaultResultRender implements ResultRender {
    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        // 将请求返回的状态码设置进http response响应流实例中
        requestProcessorChain.getResponse().setStatus(requestProcessorChain.getResponseCode());
    }

}

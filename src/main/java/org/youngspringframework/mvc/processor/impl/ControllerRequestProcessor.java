package org.youngspringframework.mvc.processor.impl;

import lombok.extern.slf4j.Slf4j;
import org.youngspringframework.mvc.RequestProcessorChain;
import org.youngspringframework.mvc.processor.RequestProcessor;


/**
 * Controller请求处理器
 */
@Slf4j
public class ControllerRequestProcessor implements RequestProcessor {
    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        return false;
    }
}

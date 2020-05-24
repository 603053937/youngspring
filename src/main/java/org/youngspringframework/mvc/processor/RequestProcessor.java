package org.youngspringframework.mvc.processor;

import org.youngspringframework.mvc.RequestProcessorChain;

/**
 * 请求执行器
 */
public interface RequestProcessor {
    boolean process(RequestProcessorChain requestProcessorChain) throws Exception;
}

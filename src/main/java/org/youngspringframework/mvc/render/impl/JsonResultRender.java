package org.youngspringframework.mvc.render.impl;


import com.google.gson.Gson;
import org.youngspringframework.mvc.RequestProcessorChain;
import org.youngspringframework.mvc.render.ResultRender;

import java.io.PrintWriter;


/**
 * Json渲染器
 */
public class JsonResultRender implements ResultRender {
    private Object jsonData;
    public JsonResultRender(Object jsonData) {
        this.jsonData = jsonData;
    }

    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        // 设置响应头
        requestProcessorChain.getResponse().setContentType("application/json");
        requestProcessorChain.getResponse().setCharacterEncoding("UTF-8");
        // 响应流写入经过gson格式化之后的处理结果
        // 获取响应流的writer实例
        try(PrintWriter writer = requestProcessorChain.getResponse().getWriter()){
            Gson gson = new Gson();
            // 将处理结果转换为json数据
            writer.write(gson.toJson(jsonData));
            // 将响应流的内容显示到页面上
            writer.flush();
        }
    }
}

package org.youngspringframework.mvc.type;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 存储处理完后的结果数据,以及显示该数据的视图
 */
public class ModelAndView {
    //页面所在的路径
    @Getter
    private String view;
    //页面的data数据
    @Getter
    private Map<String, Object> model = new HashMap<>();

    // 为什么连个set方法都要返回对象实例
    // 为了方便调用链的使用，可以通过一连串的 .addxxx 来设置对象的值
    // modelAndView.setView("addheadline.jsp").addViewData("aaa", "bbb");
    public ModelAndView setView(String view){
        this.view = view;
        return this;
    }

    public ModelAndView addViewData(String attributeName,  Object attributeValue){
        model.put(attributeName, attributeValue);
        return this;
    }
}

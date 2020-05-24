package com.wewe.controller.fronted;

import com.wewe.entity.dto.MainPageInfoDTO;
import com.wewe.entity.dto.Result;
import com.wewe.service.combine.HeadLineShopCategoryCombineService;
import lombok.Getter;
import org.youngspringframework.core.annotation.Controller;
import org.youngspringframework.inject.annotation.Autowired;
import org.youngspringframework.mvc.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Getter
@RequestMapping("/mainpage")
public class MainPageController {

    @Autowired
    private HeadLineShopCategoryCombineService headLineShopCategoryCombineService;

    public Result<MainPageInfoDTO> getMainPageInfo(HttpServletRequest req, HttpServletResponse response) {
        return headLineShopCategoryCombineService.getMainPageInfo();
    }

    @RequestMapping("/test")
    public void throwException() {
        throw new RuntimeException("抛出异常测试");
    }
}

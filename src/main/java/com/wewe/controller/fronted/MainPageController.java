package com.wewe.controller.fronted;

import com.wewe.entity.dto.MainPageInfoDTO;
import com.wewe.entity.dto.Result;
import com.wewe.service.combine.HeadLineShopCategoryCombineService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainPageController {
    private HeadLineShopCategoryCombineService headLineShopCategoryCombineService;

    public Result<MainPageInfoDTO> getMainPageInfo(HttpServletRequest req, HttpServletResponse response) {
        return headLineShopCategoryCombineService.getMainPageInfo();
    }
}

package com.wewe.service.combine;

import com.wewe.entity.dto.MainPageInfoDTO;
import com.wewe.entity.dto.Result;

public interface HeadLineShopCategoryCombineService {
    Result<MainPageInfoDTO> getMainPageInfo();
}

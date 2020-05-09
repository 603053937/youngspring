package com.wewe.entity.dto;

import com.wewe.entity.bo.HeadLine;
import com.wewe.entity.bo.ShopCategory;
import lombok.Data;

import java.util.List;

@Data
public class MainPageInfoDTO {
    private List<HeadLine> headLineList;
    private List<ShopCategory> shopCategoryList;
}

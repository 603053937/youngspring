package com.wewe.controller.superadmin;

import com.wewe.entity.bo.HeadLine;
import com.wewe.entity.dto.Result;
import com.wewe.service.solo.HeadLineService;
import org.youngspringframework.core.annotation.Controller;
import org.youngspringframework.inject.annotation.Autowired;
import org.youngspringframework.mvc.annotation.RequestMapping;
import org.youngspringframework.mvc.annotation.RequestParam;
import org.youngspringframework.mvc.annotation.ResponseBody;
import org.youngspringframework.mvc.type.ModelAndView;
import org.youngspringframework.mvc.type.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/headline")
public class HeadLineOperationController {

    @Autowired("HeadLineServiceImpl")
    private HeadLineService headLineService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ModelAndView addHeadLine(@RequestParam("lineName") String lineName,
                                    @RequestParam("lineLink") String lineLink,
                                    @RequestParam("lineImg") String lineImg,
                                    @RequestParam("priority") Integer priority) {
        HeadLine headLine = new HeadLine();
        headLine.setLineName(lineName);
        headLine.setLineLink(lineLink);
        headLine.setLineImg(lineImg);
        headLine.setPriority(priority);
        Result<Boolean> result = headLineService.addHeadLine(headLine);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView("addheadline.jsp").addViewData("result", result);
        return modelAndView;
    }

    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    public void removeHeadLine() {
        System.out.println("删除HeadLine");
    }

    public Result<Boolean> modifyHeadLine(HttpServletRequest req, HttpServletResponse resp) {
        //TODO:参数校验以及请求参数转化
        return headLineService.modifyHeadLine(new HeadLine());
    }

    public Result<HeadLine> queryHeadLineById(HttpServletRequest req, HttpServletResponse resp) {
        //TODO:参数校验以及请求参数转化
        return headLineService.queryHeadLineById(1);
    }

    @RequestMapping("/query")
    @ResponseBody
    public Result<List<HeadLine>> queryHeadLine() {
        //TODO:参数校验以及请求参数转化
        return headLineService.queryHeadLine(null, 1, 100);
    }

}

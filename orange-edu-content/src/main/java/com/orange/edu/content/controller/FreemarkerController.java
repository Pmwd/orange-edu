package com.orange.edu.content.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
@Controller
public class FreemarkerController {

    @GetMapping("/testfreemarker")
    public ModelAndView test() {
        ModelAndView modelAndView = new ModelAndView();
        // 准备模型数据
        modelAndView.addObject("name", "小明");
        // 设置视图名称（模板文件名去掉扩展名）
        modelAndView.setViewName("test");
        return modelAndView;
    }
}

package com.nowcoder.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 13:51 2019/7/4
 * @Modified By
 */
@Controller
public class SettingController {
    @RequestMapping(path = "/setting")
    @ResponseBody
    public String setting(){
        return "Setting:OK";
    }
}

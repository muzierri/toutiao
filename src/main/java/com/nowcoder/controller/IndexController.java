package com.nowcoder.controller;


import com.nowcoder.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.JobImpressions;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 16:13 2019/6/20
 * @Modified By
 */
//@Controller
public class IndexController {
    @RequestMapping(path = {"/", "/index"})
    @ResponseBody
    public String index() {
        return "hello world!";
    }

    @RequestMapping(value = {"/profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("groupId") String groupId,
                          @PathVariable("userId") int userId,
                          @RequestParam(value = "type", defaultValue = "world") String type,
                          @RequestParam(value = "key", defaultValue = "1") int key) {
        return String.format("GID{%s},UID{%d},TYPE{%s},KEY{%d}", groupId, userId, type, key);

    }

    @RequestMapping("/vm")
    public String news(Model model) {
        model.addAttribute("user", new User("Tom"));
        return "news";

    }

    @RequestMapping(value = {"/admin"})
    @ResponseBody
    public String admin(@RequestParam(value = "key",required = false) String key){
        if ("admin".equals(key)){
            return "hello admin";
        }else
        throw new IllegalArgumentException("key错误");
    }

    @ExceptionHandler
    @ResponseBody
    public String error(Exception e) {
        return "error:" + e.getMessage();

    }

}

package cn.ljaer.ssm.controller;


import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.ljaer.ssm.service.UserService;

/**
 * Created by jaer on 2016/9/12.
 */

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    
    @RequestMapping("selectByPrimaryKey")
    public void selectByPrimaryKeyCont(@RequestParam("id") int id, HttpServletResponse response) throws  Exception{
    	userService.saveMethod();
    	userService.selectMethod();
    	response.getWriter().write(userService.selectByPrimaryKey(id).toString());
    }
}

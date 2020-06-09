package com.deep.tcpservice.request;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 用于标注控制层组件(如struts中的action)，@ResponseBody和@Controller的合集
 */
@RestController
public class RequestController {

    private final Log log = LogFactory.getLog(RestController.class);

    /**
     * http - Get请求 - http://127.0.0.1:8080/tcpservice_war/h
     * @return 打印在网页的内容 类型String
     */
    @GetMapping("/h")
    public String say() {
        return "HelloNetty";
    }

    @PostMapping("/login")
    public String login(HttpSession session, @RequestParam("username") String username, @RequestParam("password") String password) {
        session.setAttribute("isLogin","is Login Now");
        log.info(session.getAttribute("isLogin"));
        log.info("user:"+username+" pass:"+password);
        return "user:"+username+" pass:"+password;
    }
}

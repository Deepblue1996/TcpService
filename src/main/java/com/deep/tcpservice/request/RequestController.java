package com.deep.tcpservice.request;

import com.deep.tcpservice.bean.*;
import com.deep.tcpservice.util.AesUtil;
import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 用于标注控制层组件(如struts中的action)，@ResponseBody和@Controller的合集
 */
@RestController
public class RequestController {

    private final Log log = LogFactory.getLog(RestController.class);

    @Resource
    private UserTableRepository userTableRepository;

    /**
     * 初始化一个新的Token
     *
     * @param userId 用户id
     * @return token
     */
    private String initToken(int userId) {
        LoginBean loginBean = new LoginBean();
        // 录入userId
        loginBean.setUserId(userId);
        // 录入有效时间
        loginBean.setDateTimeEnd(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
        // 转化字符串
        String tokenJson = new Gson().toJson(loginBean);
        String token = null;
        try {
            // AES加密
            token = AesUtil.aesEncryption(tokenJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    /**
     * 判断是否有效
     *
     * @param token 要解密的字符串
     * @return 是否有效
     */
    private boolean haveToken(String token) {
        String tokenJson = null;
        try {
            tokenJson = AesUtil.aesDecryption(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 判断是否有效Token
        if (tokenJson == null) {
            return false;
        }
        // 判断是否有效对象
        LoginBean loginBean = new Gson().fromJson(tokenJson, LoginBean.class);
        // 判断数据库是否存在
        if (userTableRepository.findByIdLike(loginBean.getUserId()).size() == 0) {
            return false;
        }
        // 判断是否有效时间
        return loginBean.getDateTimeEnd() < System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7;
    }

    /**
     * http - Get请求 - http://127.0.0.1:8080/tcpservice_war/h
     *
     * @return 打印在网页的内容 类型String
     */
    @GetMapping("/h")
    public String say() {
        return "HelloNetty";
    }

    /**
     * 登陆
     *
     * @param session 会话控制
     * @param username 用户名称
     * @param password 用户密码
     * @return 相关信息
     */
    @PostMapping("/login")
    public String login(HttpServletResponse response, @RequestParam("username") String username, @RequestParam("password") String password) {

        InfoBean<TokenBean> tokenBeanInfoBean = new InfoBean<>();
        TokenBean tokenBean = new TokenBean();

        List<UserTable> userTables = userTableRepository.findByUsernameLike(username);
        if (userTables.size() == 0) {
            tokenBeanInfoBean.setCode(400);
            tokenBeanInfoBean.setMsg("Database does not exist for the user");

            log.info("User:"+username+" Database does not exist for the user");

            return new Gson().toJson(tokenBeanInfoBean);
        }
        if (password.equals(userTables.get(0).getPassword())) {
            tokenBean.setToken(initToken(userTables.get(0).getId()));
            tokenBeanInfoBean.setCode(200);
            tokenBeanInfoBean.setMsg("Log in successfully");
            tokenBeanInfoBean.setData(tokenBean);

            response.setHeader("token", tokenBean.getToken());

            log.info("User:"+username+" Login success");
            log.info("User:"+username+" token: "+ tokenBean.getToken());

            return new Gson().toJson(tokenBeanInfoBean);
        } else {
            tokenBeanInfoBean.setCode(400);
            tokenBeanInfoBean.setMsg("Incorrect password");

            log.info("User:"+username+" Incorrect password");

            return new Gson().toJson(tokenBeanInfoBean);
        }
    }

    /**
     * 注册
     *
     * @param username 用户名称
     * @param password 用户密码
     * @return 相关信息
     */
    @PostMapping("/register")
    public String register(@RequestParam("username") String username, @RequestParam("password") String password) {

        InfoBean<TokenBean> tokenBeanInfoBean = new InfoBean<>();

        List<UserTable> userTables = userTableRepository.findByUsernameLike(username);
        if (userTables.size() == 0) {

            // 保存
            UserTable userTable = new UserTable();
            userTable.setUsername(username);
            userTable.setPassword(password);
            UserTable userTableTemp = userTableRepository.saveAndFlush(userTable);

            if(userTableTemp.getId() != 0) {
                tokenBeanInfoBean.setCode(200);
                tokenBeanInfoBean.setMsg("success");

                log.info("User:"+username+" register success");
            } else {
                tokenBeanInfoBean.setCode(400);
                tokenBeanInfoBean.setMsg("Registration failed");

                log.info("User:"+username+" Database registration failed");
            }

            return new Gson().toJson(tokenBeanInfoBean);
        }

        tokenBeanInfoBean.setCode(400);
        tokenBeanInfoBean.setMsg("The user is registered");

        log.info("User:"+username+" Registration failed, the database already exists for the user");

        return new Gson().toJson(tokenBeanInfoBean);
    }

    @PostMapping("/loginEffective")
    public String loginEffective(@RequestHeader(name = "token") String token) {

        InfoBean<String> tokenBeanInfoBean = new InfoBean<>();

        try {
            if (haveToken(token)) {
                tokenBeanInfoBean.setCode(200);
                tokenBeanInfoBean.setMsg("Login success");
            } else {
                tokenBeanInfoBean.setCode(400);
                tokenBeanInfoBean.setMsg("Login failure");
            }

            return new Gson().toJson(tokenBeanInfoBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Gson().toJson(tokenBeanInfoBean);
    }
}

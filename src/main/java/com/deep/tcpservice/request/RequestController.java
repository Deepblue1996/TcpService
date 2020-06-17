package com.deep.tcpservice.request;

import com.deep.tcpservice.bean.InfoBean;
import com.deep.tcpservice.bean.TokenBean;
import com.deep.tcpservice.bean.UserTable;
import com.deep.tcpservice.bean.UserTableRepository;
import com.deep.tcpservice.util.TokenUtil;
import com.deep.tcpservice.websocket.WssHandler;
import com.deep.tcpservice.websocket.bean.UserChatBean;
import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 用于标注控制层组件(如struts中的action)，@ResponseBody和@Controller的合集
 */
@RestController
public class RequestController {

    private final Log log = LogFactory.getLog(RequestController.class);

    @Resource
    private UserTableRepository userTableRepository;

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
     * @param response 会话控制
     * @param username 用户名称
     * @param password 用户密码
     * @return 相关信息
     */
    @PostMapping("/login")
    public String login(HttpServletResponse response, @RequestParam("username") String username, @RequestParam("password") String password) {

        InfoBean<TokenBean> tokenBeanInfoBean = new InfoBean<>();
        TokenBean tokenBean = new TokenBean();

        // 赋予全局
        TokenUtil.userTableRepository = userTableRepository;

        List<UserTable> userTables = userTableRepository.findByUsernameLike(username);
        if (userTables.size() == 0) {
            tokenBeanInfoBean.setCode(400);
            tokenBeanInfoBean.setMsg("Database does not exist for the user");

            log.info("User:" + username + " Database does not exist for the user");

            return new Gson().toJson(tokenBeanInfoBean);
        }
        if (password.equals(userTables.get(0).getPassword())) {
            tokenBean.setToken(TokenUtil.initToken(userTables.get(0).getId()));
            tokenBeanInfoBean.setCode(200);
            tokenBeanInfoBean.setMsg("Log in successfully");
            tokenBeanInfoBean.setData(tokenBean);

            response.setHeader("token", tokenBean.getToken());

            log.info("User:" + username + " Login success");
            log.info("User:" + username + " token: " + tokenBean.getToken());

        } else {
            tokenBeanInfoBean.setCode(400);
            tokenBeanInfoBean.setMsg("Incorrect password");

            log.info("User:" + username + " Incorrect password");

        }
        return new Gson().toJson(tokenBeanInfoBean);
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

            if (userTableTemp.getId() != 0) {
                tokenBeanInfoBean.setCode(200);
                tokenBeanInfoBean.setMsg("success");

                log.info("User:" + username + " register success");
            } else {
                tokenBeanInfoBean.setCode(400);
                tokenBeanInfoBean.setMsg("Registration failed");

                log.info("User:" + username + " Database registration failed");
            }

            return new Gson().toJson(tokenBeanInfoBean);
        }

        tokenBeanInfoBean.setCode(400);
        tokenBeanInfoBean.setMsg("The user is registered");

        log.info("User:" + username + " Registration failed, the database already exists for the user");

        return new Gson().toJson(tokenBeanInfoBean);
    }

    /**
     * 登陆状态检测
     *
     * @param token 令牌
     * @return 返回相关信息
     */
    @PostMapping("/loginEffective")
    public String loginEffective(@RequestHeader(name = "token") String token) {

        InfoBean<String> tokenBeanInfoBean = new InfoBean<>();

        TokenUtil.userTableRepository = userTableRepository;

        try {
            if (TokenUtil.haveToken(token)) {
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

    /**
     * 获取当前所有在线的用户
     *
     * @param token 令牌
     * @return 返回相关信息
     */
    @PostMapping("/userList")
    public String userList(@RequestHeader(name = "token") String token) {

        InfoBean<List<UserChatBean>> tokenBeanInfoBean = new InfoBean<>();
        List<UserChatBean> userChatBeanList = new ArrayList<>(WssHandler.userChatBeanList);
        tokenBeanInfoBean.setData(userChatBeanList);
        try {
            if (TokenUtil.haveToken(token)) {
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

    /**
     * 上传头像
     *
     * @param file    文件
     * @param token   token
     * @return 信息
     */
    @SuppressWarnings("all")
    @Transactional // 执行数据库更新事务
    @PostMapping(value = "/fileUploadHeadPortrait")
    public String fileUploadHeadPortrait(@RequestHeader(name = "token") String token, @RequestParam(value = "file") MultipartFile file) {

        TokenUtil.userTableRepository = userTableRepository;

        InfoBean<String> tokenBeanInfoBean = new InfoBean<>();
        if (file.isEmpty()) {
            tokenBeanInfoBean.setCode(400);
            tokenBeanInfoBean.setMsg("file failure");
            return new Gson().toJson(tokenBeanInfoBean);
        }

        try {
            if (TokenUtil.haveToken(token)) {
                tokenBeanInfoBean.setCode(200);

                String fileName = file.getOriginalFilename();  // 文件名
                String suffixName = fileName.substring(fileName.lastIndexOf("."));  // 后缀名

                String realPath = ResourceUtils.getURL("classpath:").getPath() + "static/upload/";

                fileName = UUID.randomUUID() + suffixName; // 新文件名
                File dest = new File(realPath + fileName);
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();
                }
                try {
                    file.transferTo(dest);
                } catch (IOException e) {
                    e.printStackTrace();

                    tokenBeanInfoBean.setCode(400);
                    tokenBeanInfoBean.setMsg("user create file faile");

                    return new Gson().toJson(tokenBeanInfoBean);
                }
                String filesName = realPath + fileName;

                tokenBeanInfoBean.setData(fileName);
                tokenBeanInfoBean.setMsg("Path:" + filesName);

                log.info("User: upload Img to service - " + filesName + " success");

                // 获取用户
                UserTable userTable = TokenUtil.getUser(token);

                userTableRepository.updateHeaderById(userTable.getId(), fileName);

            } else {
                tokenBeanInfoBean.setCode(400);
                tokenBeanInfoBean.setMsg("token failure");
            }
        } catch (Exception e) {
            e.printStackTrace();
            tokenBeanInfoBean.setCode(400);
            tokenBeanInfoBean.setMsg("file failure");
        }

        return new Gson().toJson(tokenBeanInfoBean);
    }
}

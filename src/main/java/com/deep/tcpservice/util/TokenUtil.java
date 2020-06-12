package com.deep.tcpservice.util;

import com.deep.tcpservice.bean.LoginBean;
import com.deep.tcpservice.bean.UserTableRepository;
import com.google.gson.Gson;

/**
 * Token 管理对象
 */
public class TokenUtil {

    /**
     * 初始化一个新的Token
     *
     * @param userId 用户id
     * @return token
     */
    public static String initToken(int userId) {
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
    public static boolean haveToken(UserTableRepository userTableRepository, String token) {
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

}

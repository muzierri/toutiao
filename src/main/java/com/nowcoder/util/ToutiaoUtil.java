package com.nowcoder.util;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.controller.LoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.Map;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 22:13 2019/6/29
 * @Modified By
 */
public class ToutiaoUtil {
    private static final Logger logger = LoggerFactory.getLogger(ToutiaoUtil.class);

    //七牛外链前缀
    //public static String QINIU_DOMAIN_PREFIX = "http://puf18t6mw.bkt.clouddn.com/";
    public static String QINIU_DOMAIN_PREFIX = "http://pvyg7vxrh.bkt.clouddn.com/";
    //本地
    public static String TOUTIAO_DOMAIN = "http://127.0.0.1:8080/";
    //图片上传地址
    public static String IMAGE_DIR = "E:/upload/";
    //判断扩展名是否为图片
    public static String[] IMAGE_FILE_EXT = new String[]{"png", "jpg", "bmp", "jpeg"};

    public static boolean isFileAllowed(String fileExt) {
        for (String ext : IMAGE_FILE_EXT) {
            if (ext.equals(fileExt)) {
                return true;
            }
        }
        return false;
    }

    //json
    public static String getJSONString(int code) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        return json.toJSONString();
    }

    public static String getJSONString(int code, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        return json.toJSONString();
    }


    //密码加密方法
    public static String MD5(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            logger.error("生成MD5失败", e);
            return null;
        }
    }
}

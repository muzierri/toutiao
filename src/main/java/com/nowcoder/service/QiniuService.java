package com.nowcoder.service;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.controller.NewsController;
import com.nowcoder.util.ToutiaoUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 17:04 2019/7/10
 * @Modified By
 */
@Service
public class QiniuService {
    private static final Logger logger = LoggerFactory.getLogger(QiniuService.class);
    //构造一个带指定Zone对象的配置类
    Configuration cfg = new Configuration(Zone.zone0());

    UploadManager uploadManager = new UploadManager(cfg);
    //...生成上传凭证，然后准备上传
    String accessKey = "eLXG5fy_14TS2OqgluGmWMuRMZAfArJHrK3Llf71";
    String secretKey = "WbkHIyk_-2Zkb_xHVSS2v5pYF3pFV_6SQj_3bNxs";
    //String bucket = "lybucket";
    String bucket = "ly_bucket";

    //默认不指定key的情况下，以文件内容的hash值作为文件名
    String key = null;
    Auth auth = Auth.create(accessKey, secretKey);

    public String getUpToken() {
        return auth.uploadToken(bucket);
    }

    public String saveImage(MultipartFile file) throws IOException {
        try {
            int doPos = file.getOriginalFilename().lastIndexOf(".");
            if (doPos < 0) {
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(doPos + 1).toLowerCase();
            if (!ToutiaoUtil.isFileAllowed(fileExt)) {
                return null;//格式不符合
            }
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
            //调用put方法上传
            Response response = uploadManager.put(file.getBytes(), fileName, getUpToken());
            //打印返回的信息
            System.out.println(response.bodyString());
            //System.out.println(response.toString());
            if (response.isOK() && response.isJson()) {
                String key = JSONObject.parseObject(response.bodyString()).get("key").toString();
                return ToutiaoUtil.QINIU_DOMAIN_PREFIX + key;
            } else {
                logger.error("七牛异常:" + response.bodyString());
                return null;
            }
        } catch (QiniuException e) {
            logger.error("七牛异常" + e.getMessage());
            return null;
        }
    }
}

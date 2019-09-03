package com.nowcoder.util;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;
import java.util.Properties;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 19:24 2019/7/24
 * @Modified By
 */
@Service
public class MailSender implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    //用模板渲染出邮件并发送
    public boolean sendWithHTMLTemplate(String to, String subject, String template,
                                        Map<String, Object> model) {
        try {
            String nick = MimeUtility.encodeText("ly");//昵称
            InternetAddress from = new InternetAddress(nick + "<229398570@qq.com>");//发件人
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            //内容
            String result = VelocityEngineUtils
                    .mergeTemplateIntoString(velocityEngine, template, "UTF-8", model);

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(result,true);
            mailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            logger.error("发送邮件失败" + e.getMessage());
            return false;
        }

    }

    //初始化
    @Override
    public void afterPropertiesSet() throws Exception {
        mailSender = new JavaMailSenderImpl();

        //请输入自己的邮箱和密码,用于发送邮件
        mailSender.setUsername("229398570@qq.com");
        mailSender.setPassword("qevqvqhviarmbhea");
        mailSender.setHost("smtp.qq.com");//发送的服务器
        mailSender.setPort(465);//端口
        mailSender.setProtocol("smtps");//协议
        mailSender.setDefaultEncoding("utf8");
        Properties JavaMailProperties = new Properties();
        JavaMailProperties.put("mail.smtp.ssl.enable", true);
//        JavaMailProperties.setProperty("mail.host", "smtp.qq.com");
//        JavaMailProperties.setProperty("mail.transport.protocol", "smtp");
//        JavaMailProperties.setProperty("mail.smtp.auth", "true");
//        JavaMailProperties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        JavaMailProperties.setProperty("mail.smtp.port", "465");
//        JavaMailProperties.setProperty("mail.smtp.socketFactory.port", "465");
        mailSender.setJavaMailProperties(JavaMailProperties);
    }
}

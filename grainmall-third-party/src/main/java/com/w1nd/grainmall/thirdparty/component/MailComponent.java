package com.w1nd.grainmall.thirdparty.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@ConfigurationProperties(prefix = "mail.qq")
@Data
@Component
public class MailComponent {

    private String host;
    private String password;

    public void sendMailCode(String mail, String code) {
        Properties props = System.getProperties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.host","smtp.qq.com");// 设置邮件服务器
        props.put("mail.user",host);
        props.put("mail.password",password);//开启pop3/smtp时的验证码
        props.put("mail.smtp.port","587");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.timeout", "25000");

        // 获取默认session对象
        Session session = Session.getDefaultInstance(props,new Authenticator(){
            public PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(
                        host,
                        password); //发件人邮件用户名、授权码
            }
        });
        // session.setDebug(true);//代表启用debug模式，可以在控制台输出smtp协议应答的过程

        try{
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(host));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mail));
            message.setSubject("This is the Subject Line! code is : " + code);

            // 设置消息体
            message.setText("This is actual message, code is : " + code);

            // 发送消息
            Transport.send(message);
            System.out.println("Sent message successfully....");
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}

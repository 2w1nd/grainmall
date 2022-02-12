package com.w1nd.grainmall.thirdparty;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.w1nd.grainmall.thirdparty.component.MailComponent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GrainmallThirdPartyApplicationTests {

    @Autowired
    MailComponent mailComponent;

    @Autowired
    OSSClient ossClient;

    @Test
    public void testUpload() throws FileNotFoundException {
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = "oss-cn-shanghai.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = "";
        String accessKeySecret = "";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        InputStream inputStream = new FileInputStream("D:\\image\\QQ图片20200418221025.jpg");
        // 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt）。Object完整路径中不能包含Bucket名称。
        ossClient.putObject("grainmall-hello1", "w1nd111.jpg", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
    }

    @Test
    public void testMailSend() {
        // receiver email
        String to = "2964680209@qq.com";
        //sender email
        String from = "584202045@qq.com";
        Properties props = System.getProperties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.host","smtp.qq.com");// 设置邮件服务器
        props.put("mail.user","584202045@qq.com");
        props.put("mail.password","bdbjgrzynxpmbfff");//开启pop3/smtp时的验证码
        props.put("mail.smtp.port","25");
        props.put("mail.smtp.starttls.enable", "true");

        // 获取默认session对象
        Session session = Session.getDefaultInstance(props,new Authenticator(){
            public PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(
                        "584202045@qq.com",
                        "bdbjgrzynxpmbfff"); //发件人邮件用户名、授权码
            }
        });
        session.setDebug(true);//代表启用debug模式，可以在控制台输出smtp协议应答的过程

        try{
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("This is the Subject Line!");

            // 设置消息体
            message.setText("This is actual message");

            // 发送消息
            Transport.send(message);
            System.out.println("Sent message successfully....");
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }


    @Test
    public void contextLoads() {
    }

    @Test
    public void testSmsComponent() {
        mailComponent.sendMailCode("2964680209@qq.com", "1234");
    }

}

package com.w1nd.grainmall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.w1nd.grainmall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000119614629";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCzypGZRGfphcRQ6Q08y3WQwxaUYlhOoanmo3++cL5fTisuORRUyeZhdl27RKeQ0RpLtC5smUiy8bPZOdBE33k9DEyf47atPYssU6mChoNfn0D5iq/oWTxbUvmFhdSudv5vC+Bs5grjT3jlQD17NJRj4txb8xX140G8DweukdgtWvh2lLdtwID7M4eeqGaHHGkE9WMwhEaG4CMbpgLn0A3yBFAqTsCuboYDoE6wQvNdPqjmcITTSfUgb3iDi7MIoSPMElW/Aa16xnDMiwCTTGA8dMd/Y2z7ue4kwmsr1a1gIH5B41jxy6MdxGeCCDLDUuoMrUY/MNhvbWZUSuNAzDLTAgMBAAECggEBALLgtAO9n3UjdbLabCU0eH/S2cBAKIPzLEsgGJStL3s7FpMijyvcxK3rSk5rfBwFLxhq6FV8HGyNNdbGQoSDouguOH5L3RB3zuQHSPtoQYT9uXONnH6bagWihEdoY7ZBBdwOAPfX2LVX+Hx+ca0HHyxSJdQGAq9lr50fhxEUYfScgtH4Mjh4bCfgrkwZ69UpQhF7mOqPQNoXEVcrzWatuk6ZDErmkA8pD1lBKdoIiuNvpvPDu/MLeWGGuFnymX8XTS6DGHk+uT9ueUOq2GcuMdbsLJf9MlJ68Dx9TulM1FWy5gPou9TUPnLXzB8UVFMk4Eifsllco6GsTwz3o5YMECECgYEA9p4wgxoa6DnFP9lUGetKVlcIhY4wR3hxqPa+7VYSGmBvRSthBjdwGN5DTtzx0f3PRLEdEiS7EZ0JgBFCGsM5N92IRqkgzZaYWJ+mjolNV2EPjVbqm3jWwzThoQcXpLxz6Gzt+DRTdknfgAAj1/ALjdfFgqDXKPlJDbYNfKcMo+0CgYEAuqGOEm8Gdb+7PWcZ4OkRKUR6ctmvnnfTPWPfm0yxRiRYBPcozBwnf+dgGLxtDKiXuiFW115bdGJG6ktECZ628iTIIJg+vyOMjfuIUn8QQiycimBXe/hGJJQ0Wx4lvX+ysgq8XxG9Y0926eYd4gclbMTigdP4PBYz/C0T9EvS2b8CgYBK/V8S/mUgKDDqcVM5bmp0RpK4ybu0f9NP5Xe5A2sNyjULe11NXp4fcbZVzKlbiSn1YUXNto2LlYzF/Hl1WdeUPTCrC9KFsT0x9ZnXzbhO0ZzBBaThEaV8RiA1rspptUQpcLYLux2F4oCrk19S0hMxVmMgjXffZa3pFWqUdNdLIQKBgA5WmR6rbvie6Vi3UhKodUwRWcL3HHn1RMJjqOs6gigcZLLE9lpwE0zNTuG3oD0nurhvFUfolOglIMOhUrHWgsGpt8JhT/jJV2QVHg0QfARJfyFWc4IvudvXkwopD8c2R1lMX9qB92Xo1f70UflqiSKJIcnNqjWbkxTdtbnAFYvrAoGBAKSXqJj/r/YjguXfhrUF+oGrbjeuzw2NPKMelyHSnjmjCs5DGjyjI8ixuYLGm8MJXVGY8U5BqEMPBAvdyq7OtTks1bVHgMr2EiPF6wjzJcSeFSjLfTy4XXDZL2IrTBGPmLEEgpqBlD0d6OnrfTd5lb4CctQ/0sa5iGSiDevb9c7K";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA8UCnpu4DcHqnB/BeF9lRVnNbC9wtefTaUniPNPXXT0YmLqqKSSU0LJRFH9wYtkyGAwPWuhhBVo0hjHRicdiNeK0F1ueJmS46n56KNvm94c2d7oOg6jLmgKykibZpQOl2xwjb+rZ7/HYMhPXwQ1Xli5kvXgkJAPfUBo3+r4u/zbXIqKKvU1VirhVMDxJF8mwuKYmGxBIAwaHFRVNOsXBiL32TnS4GcVisd1v4wZmOrdPkZUFMW8qcjiezLtf3SEuKtRZEVDqJPYMgTBn9kZibfsU1qg4j98Wu8tQl+9bdnO603gjOBSQtI3Xs0TaE36cTc7CNFwKHJ3riovgctsCDmwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://h48851078g.qicp.vip/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url = "http://member.grainmall.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    private String timeout = "30m";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\""+ timeout +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}

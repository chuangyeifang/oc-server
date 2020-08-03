package com.oc.auth;

import com.oc.common.utils.MD5Encrypt;
import com.oc.exception.BsAuthorizeException;
import com.oc.util.base64.B64;
import com.oc.util.http.HttpRequests;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户信息认证
 * @author chuangyeifang
 */
@Slf4j
public class CustomerAuthCoder {

    @SuppressWarnings("FieldCanBeLocal")
    public static String CUSTOMER_COOKIE_NAME = "OMC-Passport";

    public static String encode(CustomerInfo customerInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(customerInfo.getTtc()).append("|");
        sb.append(customerInfo.getTmc()).append("|");
        sb.append(customerInfo.getSkc()).append("|");
        sb.append(customerInfo.getBuc()).append("|");
        sb.append(customerInfo.getCc()).append("|");
        sb.append(customerInfo.getCn()).append("|");
        sb.append(customerInfo.getGc()).append("|");
        sb.append(customerInfo.getReal()).append("|");
        sb.append(customerInfo.getDevice()).append("|");
        sb.append(System.currentTimeMillis());
        String sign = MD5Encrypt.sign(sb.toString());
        sb.append("=").append(sign);
        return B64.encoder(sb.toString());
    }

    public static CustomerInfo decode(HttpHeaders headers) throws BsAuthorizeException {
        String customerCookieValueB64 = HttpRequests.getCookie(headers, CUSTOMER_COOKIE_NAME);
        if (null == customerCookieValueB64 || customerCookieValueB64.isEmpty()) {
            throw new BsAuthorizeException("1004 无法客户获取认证信息");
        }
        try {
            String customerCookieValue = B64.decoder(customerCookieValueB64);
            String[] body = customerCookieValue.split("=");
            String[] params = body[0].split("\\|");
            String sign = MD5Encrypt.sign(body[0]);
            if (sign.equals(body[1])) {
                CustomerInfo customerInfo = new CustomerInfo();
                customerInfo.setTtc(params[0]);
                customerInfo.setTmc(Integer.valueOf(params[1]));
                customerInfo.setSkc(Integer.valueOf(params[2]));
                customerInfo.setBuc(params[3]);
                customerInfo.setCc(params[4]);
                customerInfo.setCn(params[5]);
                customerInfo.setGc(params[6]);
                customerInfo.setReal(params[7]);
                customerInfo.setDevice(params[8]);
                return customerInfo;
            }
        } catch (Exception e) {
            log.error("客户认证信息：{} 解析发生异常", customerCookieValueB64, e);
        }
        throw new BsAuthorizeException("1002 客户信息认证失败: [" + customerCookieValueB64 + "]");
    }
}

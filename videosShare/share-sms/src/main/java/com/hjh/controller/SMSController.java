package com.hjh.controller;

import com.hjh.utils.RedisUtils;
import com.hjh.utils.SMSUtils;
import com.hjh.vo.MsgVO;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class SMSController {
    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private SMSUtils smsUtils;

    private static final Logger log = LoggerFactory.getLogger(SMSController.class);

    @PostMapping("captchas")
    public void captchas(@RequestBody MsgVO msgVO) {
        //1. 获取手机号
        String phone = msgVO.getPhone();
        log.info("发送短信的手机号为: {}", phone);

        //1.5 如果重复发送验证码则抛出异常
        String timeoutKey = "timeout_" + phone;
        if (redisUtils.hasKey(timeoutKey)) {
            throw new RuntimeException("提示: 重复提交频率过快，稍后在试");
        }

        try {
            //2.生成4位的验证码
            String code = RandomStringUtils.randomNumeric(4);
            log.info("发送的验证码: {}", code);

            //3. 发送验证码
            smsUtils.sendMsg(phone,code);

            String phoneKey = "phone_" + phone;
            //4. 由于没有开通短信服务，验证码默认为1234
            redisUtils.set(phoneKey,code,10, TimeUnit.MINUTES); // 10分钟过期


            //5. 如果验证码在有效期内，不允许重新发送
            redisUtils.set(timeoutKey,"true",60,TimeUnit.SECONDS);//两次发送间隔时间
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("提示: 短信发送失败");
        }


    }
}

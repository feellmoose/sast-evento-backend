package sast.evento;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import sast.evento.config.ActionRegister;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.model.wxServiceDTO.AccessTokenRequest;
import sast.evento.model.wxServiceDTO.WxSubscribeRequest;
import sast.evento.service.CodeService;
import sast.evento.service.QrCodeCheckInService;
import sast.evento.utils.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class SastEventoBackendApplicationTests {

    @Test
    void genereateToken(){
        JwtUtil jwtUtil = SpringContextUtil.getBean(JwtUtil.class);
        HashMap<String,String> map = new HashMap<>();
        map.put("user_id","1");
        String token = jwtUtil.generateToken(map);
        System.out.println(token);
    }

    @Test
    void getAllMethodNameByJson() {
        String json = JsonUtil.toJson(new ArrayList<>(ActionRegister.actionNameSet));
        System.out.println("json: " + json);
        System.out.println("max lengh: " + json.length());
    }

    @Test
    void generateQrCode() {
        try {
            BufferedImage image = QRCodeUtil.generateQrCode("");
        } catch (Exception e) {
            throw new LocalRunTimeException(ErrorEnum.QRCODE_ERROR);
        }
    }

    @Test
    void wxSubscribe() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("key1", "");
        dataMap.put("key2", String.valueOf(111));
        dataMap.put("key3", String.valueOf(111));
        System.out.println(JsonUtil.toJson(WxSubscribeRequest.getData(dataMap)));
        System.out.println(JsonUtil.toJson(new AccessTokenRequest()));
    }

    @Test
    void refreshJobTest(){
        Integer eventId = 1000000;
        QrCodeCheckInService service = SpringContextUtil.getBean(QrCodeCheckInService.class);
        service.getCheckInQrCode(eventId);
        CodeService codeService = SpringContextUtil.getBean(CodeService.class);
        String code = codeService.getCode(eventId);
        System.out.println("code: "+code);
        System.out.println("check: "+service.checkCode(eventId, code));
        codeService.refreshCode(eventId);
        System.out.println("check: "+service.checkCode(eventId, code));
        service.close(eventId);
    }

    @Test
    void CosUtilTest(){
    }


}

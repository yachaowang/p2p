package com.bjpowernode.p2p.qr;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName QrCode
 * @Description
 * @Version 1.0
 * @Date 2019/4/3 10:01
 * @Author wyc
 **/
public class QrCode {
    @Test
    public void generateQRCode() throws WriterException, IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("country","CHINA");
        jsonObject.put("province","HeBei");
        jsonObject.put("city","石家庄");
        JSONObject areaJson = new JSONObject();
        areaJson.put("address","幸福街道");
        areaJson.put("小区","快乐小区");
        jsonObject.put("area",areaJson);

        String content = jsonObject.toJSONString();

        Map<EncodeHintType,Object> map = new HashMap<>();
        map.put(EncodeHintType.CHARACTER_SET,"UTF-8");
        //创建矩阵对象
        BitMatrix bitMatrix = new MultiFormatWriter().encode("http://www.pptbz.com/pptpic/UploadFiles_6909/201203/2012031220134655.jpg", BarcodeFormat. QR_CODE,200,200,map);
        String filePath = "f://";
        String fileName = "QRCode.jpg";

        Path path = FileSystems.getDefault().getPath(filePath,fileName);
        MatrixToImageWriter.writeToPath(bitMatrix,"jpg",path);
        System.out.println("生成二维码成功");
    }

}
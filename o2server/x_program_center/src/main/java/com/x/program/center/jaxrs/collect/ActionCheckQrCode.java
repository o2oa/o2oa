package com.x.program.center.jaxrs.collect;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.ApplicationServer;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Node;
import com.x.base.core.project.config.WebServer;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * Created by fancyLou on 7/27/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionCheckQrCode extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionCheckQrCode.class);

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    ActionResult<Wo> execute() throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        wo.setApplicationList(new ArrayList<AbstractWoProxy.Application>());
        Map.Entry<String, CenterServer> firstCenter = Config.nodes().centerServers().first();
        if (firstCenter == null) {
            throw new ExceptionProxyEmpty("中心服务");
        }
        String protocol = firstCenter.getValue().getHttpProtocol();
        if (StringUtils.isEmpty(protocol)) {
            throw new ExceptionProxyEmpty("http访问协议");
        }
        wo.setHttpProtocol(protocol);

        AbstractWoProxy.Center center = new AbstractWoProxy.Center();
        String centerHost = firstCenter.getValue().getProxyHost();
        if (StringUtils.isEmpty(centerHost)) {
            throw new ExceptionProxyEmpty("中心服务器代理Host");
        }
        center.setProxyHost(centerHost);
        center.setProxyPort(firstCenter.getValue().getProxyPort());
        wo.setCenter(center);

        for (Map.Entry<String, Node> en : Config.nodes().entrySet()) {
            if (null != en.getValue()) {
                WebServer webServer = en.getValue().getWeb();
                if (null != webServer && BooleanUtils.isTrue(webServer.getEnable())) {
                    AbstractWoProxy.Web web = new AbstractWoProxy.Web();
                    web.setProxyHost(StringUtils.isNotEmpty(webServer.getProxyHost()) ? webServer.getProxyHost() : centerHost); // 没有配置使用中心服务器host
                    web.setProxyPort(webServer.getProxyPort());
                    wo.setWeb(web);
                }
                ApplicationServer applicationServer = en.getValue().getApplication();
                if (null != applicationServer && BooleanUtils.isTrue(applicationServer.getEnable())) {
                    AbstractWoProxy.Application application = new AbstractWoProxy.Application();
                    application.setNode(en.getKey());
                    application.setProxyHost(StringUtils.isNotEmpty(applicationServer.getProxyHost()) ? applicationServer.getProxyHost() : centerHost);// 没有配置使用中心服务器host
                    application.setProxyPort(applicationServer.getProxyPort());
                    wo.getApplicationList().add(application);
                }
            }
        }
        if (wo.getWeb() == null) {
            throw new ExceptionProxyEmpty("web服务器");
        }

        String url = protocol + "://" + wo.getWeb().getProxyHost() + ":" + wo.getWeb().getProxyPort() + "/x_desktop/appMobileConnectCheck.html";
        logger.info("扫码测试连接的地址： " + url);

        /**
         * 生成二维码
         */
        int width = 200; // 二维码图片宽度
        int height = 200; // 二维码图片高度
        String format = "png";// 二维码的图片格式

        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        // hints.put(EncodeHintType.CHARACTER_SET, DefaultCharset.name); //
        // 内容所使用字符集编码
        hints.put(EncodeHintType.MARGIN, "1");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q.toString());

        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? BLACK : WHITE);
            }
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, out);
            wo.setQrcode(Base64.encodeBase64String(out.toByteArray()));
        }
        result.setData(wo);
        return result;
    }


    public static class Wo extends  AbstractWoProxy {

        @FieldDescribe("二维码图片base64")
        private String qrcode;

        public String getQrcode() {
            return qrcode;
        }

        public void setQrcode(String qrcode) {
            this.qrcode = qrcode;
        }
    }
}

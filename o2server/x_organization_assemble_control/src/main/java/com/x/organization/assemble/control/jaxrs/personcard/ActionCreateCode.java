package com.x.organization.assemble.control.jaxrs.personcard;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.PersonCard;

class ActionCreateCode extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateCode.class);

    // 个人通讯录生成二维码。
    ActionResult<Wo> qrcode(EffectivePerson effectivePerson, String flag) throws Exception {

        LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            PersonCard personCard = emc.find(flag, PersonCard.class);
            if (null == personCard) {
                throw new ExceptionPersonCardNotExist(flag);
            }

            String fname = personCard.getId() + ".png";
            String content = "BEGIN:VCARD\n" + "VERSION:3.0\n" + "N:" + personCard.getName() + "\n";

            if (!personCard.getOfficePhone().equals("")) {
                content = content + "TEL:" + personCard.getOfficePhone() + "\n";
            }
            if (!personCard.getMobile().equals("")) {
                content = content + "TEL;CELL:" + personCard.getMobile() + "\n";
            }
            if (!personCard.getAddress().equals("")) {
                content = content + "ADR;HOME;POSTAL:;;" + personCard.getAddress() + ";;;;\n";
            }
            if (!personCard.getGroupType().equals("")) {
                content = content + "ORG:" + personCard.getGroupType() + "\n";
                // content = content+"TITLE:"+personCard.getGroupType()+"\n";
            }
            if (!personCard.getDescription().equals("")) {
                content = content + "NOTE:" + personCard.getDescription() + "\n";
            }
            content = content + "END:VCARD";

            /*
             * content = "BEGIN:VCARD\n" + "VERSION:1.0\n" + "N:李德伟\n" +
             * "EMAIL:1606841559@qq.com\n" + "TEL:12345678912\n" + "TEL;CELL:12345678913\n"
             * + "ADR;HOME;POSTAL:;;文二路391号;杭州市;浙江省;433330;中国\n" + "ORG:济南\n" +
             * "TITLE:软件工程师\n" + "URL:http://blog.csdn.net/lidew521\n" + "NOTE:呼呼测试下吧。。。\n"
             * + "END:VCARD";
             */
            // getBarCode(content,path);

            Wo wo = new Wo(getBarCodeWo(content), this.contentType(false, fname),
                    this.contentDisposition(false, fname));
            result.setData(wo);
            return result;
        }
    }

    /**
     * 二维码实现
     * 
     * @param msg  /二维码包含的信息
     * @param path /二维码存放路径
     */
    public static void getBarCode(String msg, String path) {
        File file = new File(path);
        try (OutputStream ous = new FileOutputStream(file)) {
            if (StringUtils.isEmpty(msg)) {
                return;
            }
            String format = "png";
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            Map<EncodeHintType, String> map = new HashMap<EncodeHintType, String>();
            // 设置编码 EncodeHintType类中可以设置MAX_SIZE，
            // ERROR_CORRECTION，CHARACTER_SET，DATA_MATRIX_SHAPE，AZTEC_LAYERS等参数
            map.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            map.put(EncodeHintType.MARGIN, "1");
            // 生成二维码
            BitMatrix bitMatrix = new MultiFormatWriter().encode(msg, BarcodeFormat.QR_CODE, 300, 300, map);
            MatrixToImageWriter.writeToStream(bitMatrix, format, ous);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 二维码实现
     * 
     * @param msg /二维码包含的信息
     */
    public static byte[] getBarCodeWo(String msg) {
        byte[] bs = null;
        try {
            String format = "png";
            Map<EncodeHintType, String> map = new HashMap<EncodeHintType, String>();
            // 设置编码 EncodeHintType类中可以设置MAX_SIZE，
            // ERROR_CORRECTION，CHARACTER_SET，DATA_MATRIX_SHAPE，AZTEC_LAYERS等参数
            map.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            map.put(EncodeHintType.MARGIN, "1");
            // 生成二维码
            BitMatrix bitMatrix = new MultiFormatWriter().encode(msg, BarcodeFormat.QR_CODE, 300, 300, map);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "gif", out);
            bs = out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bs;
    }

    public static class Wo extends WoFile {

        public Wo(byte[] bytes, String contentType, String contentDisposition) {
            super(bytes, contentType, contentDisposition);
        }

    }

}

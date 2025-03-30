package org.dopamine.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class BarCodeUtils {
    /**
     * 数据流转换成Base64字符串
     * @param is 数据流
     * @return
     */
    public static String getImageBase64ToString(InputStream is) {
        String imgBase64String = null;
        try {
//            File imgFile = new File(imgPath);
//            InputStream inputStream = new FileInputStream(imgFile);
            InputStream inputStream = is;
            int available = inputStream.available();
            byte[] bytes = new byte[available];
            inputStream.read(bytes);
            imgBase64String = new String(Base64.encodeBase64(bytes));

        } catch (IOException e) {
        }
        return StringUtils.isNotEmpty(imgBase64String) ? imgBase64String : "";
    }

    /**
     * 返回标签号对应的条形码或者二维码
     *
     * @param proId      项目号
     * @return 输入流
     * @throws Exception
     */
    public static InputStream getBarcodeOrQrCode(boolean isQrCode, String proId) throws Exception {
        ByteArrayOutputStream os = null;
        os = new ByteArrayOutputStream();
        /*judge1D2D True是条形码 False是二维码*/
        if (isQrCode) {
            os = get2dCode(proId, proId);
        } else {
            os = get1dCode(proId);
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        os.close();
        return is;
    }

    /**
     * 条形码
     * @param text 条形码字符串
     * @return
     */
    public static ByteArrayOutputStream get1dCode(String text){
        ByteArrayOutputStream os = null;
        try {
            // log.info(Log.op(op).msg("开始转化条形码").kv("xxx", xxx).toString());
            BufferedImage image = QrCodeUtils.getBarCodeWithWords(text, text, "", "", "");
            os = new ByteArrayOutputStream();
            // 转入输出流
            ImageIO.write(image, "png", os);
            return os;
            // log.info(Log.op(op).msg("条形码转化结束").toString());
        } catch (Exception e) {
            // throw MallExceptionCode.IO_ERROR.exp(e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
            return os;
        }
    }

    /**
     *  二维码
     * @param text 二维码对应的字符串
     * @param path 输出本地对应的路径
     */
    public static ByteArrayOutputStream get2dCode(String text, String path) {
        int QR_CODE_SIZE = 200;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
//            输出本地
//            Path file = FileSystems.getDefault().getPath(path);
//            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", file);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", os);
            return os;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

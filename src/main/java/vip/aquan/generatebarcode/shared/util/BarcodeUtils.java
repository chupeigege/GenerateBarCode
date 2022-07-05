package vip.aquan.generatebarcode.shared.util;

import org.apache.commons.lang3.StringUtils;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * BarcodeUtils
 */
public class BarcodeUtils {


    public static byte[] generateBarCode(String msg, Double height) {
        ByteArrayOutputStream ous = new ByteArrayOutputStream();
        generateBarCode(msg, ous, height);
        return ous.toByteArray();
    }

    public static void generateBarCode(String msg, OutputStream ous,Double height) {
        if (StringUtils.isEmpty(msg) || ous == null) {
            return;
        }
        if (height == null) {
            height = 34.25;
        }
        Code128Bean bean = new Code128Bean();
        final int dpi = 120;
        final double moduleWidth = UnitConv.in2mm(2.0f / dpi);
        bean.setModuleWidth(moduleWidth);
        bean.setHeight(height);
        bean.doQuietZone(false);
        bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);

        String format = "image/png";
        try {

            BitmapCanvasProvider canvas = new BitmapCanvasProvider(ous, format, dpi,
                    BufferedImage.TYPE_BYTE_BINARY, false, 0);
            bean.generateBarcode(canvas, msg);

            canvas.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}

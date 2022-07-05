package vip.aquan.generatebarcode.controller;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import vip.aquan.generatebarcode.service.BarcodeReaderService;
import vip.aquan.generatebarcode.shared.common.BusinessCode;
import vip.aquan.generatebarcode.shared.response.RestResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

@CrossOrigin
@RestController
@RequestMapping("/barcode")
public class BarcodeReaderController {

    private static final Logger log = LoggerFactory.getLogger(BarcodeReaderController.class);

    private final BarcodeReaderService barcodeReaderService;

    public BarcodeReaderController(BarcodeReaderService barcodeReaderService) {
        this.barcodeReaderService = barcodeReaderService;
    }

//    请求示例： http://localhost:8080/barcode/generate?code=123456&height=100
    @GetMapping("/generate")
    public void generateBarCode(@RequestParam("code") String code,
                                @RequestParam(value = "height", required = false) Double height,
                                HttpServletResponse httpServletResponse) throws IOException {
        OutputStream os = null;
        try {
            httpServletResponse.setContentType("image/png");
            byte[] bytes = barcodeReaderService.generateBarCode(code, height);
            os = httpServletResponse.getOutputStream();
            os.write(bytes);
            os.flush();
        } catch (Exception e) {
            httpServletResponse.setContentType("application/json; charset=UTF-8");
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter writer = httpServletResponse.getWriter();
            writer.println(JSON.toJSONString(RestResponse.fail(BusinessCode.RSC_500,e.getMessage())));
            writer.close();
            log.error("generateBarCode error", e);
        } finally {
            if (os != null) {
                os.close();
            }

        }

    }
}

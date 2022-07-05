package vip.aquan.generatebarcode.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vip.aquan.generatebarcode.service.BarcodeReaderService;
import vip.aquan.generatebarcode.shared.util.BarcodeUtils;


@Service
public class BarcodeReaderServiceImpl implements BarcodeReaderService {

    private static final Logger log = LoggerFactory.getLogger(BarcodeReaderServiceImpl.class);


    @Override
    public byte[] generateBarCode(String code, Double height) {
        return BarcodeUtils.generateBarCode(code, height);

    }

}

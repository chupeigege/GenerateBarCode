package vip.aquan.generatebarcode.service;



/**
 * @Author: Chupei.Wang
 * @Date: 2021/9/29
 * @Description:
 */
public interface BarcodeReaderService {

    byte [] generateBarCode(String code, Double height);

}

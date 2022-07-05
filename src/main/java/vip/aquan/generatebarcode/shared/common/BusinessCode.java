package vip.aquan.generatebarcode.shared.common;

public enum BusinessCode {
    RSC_200("200", "Succeeded"),
    RSC_500("500", "Failed");

    private final String code;
    private final String message;

    BusinessCode(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage(){
        return message;
    }
}

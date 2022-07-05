package vip.aquan.generatebarcode.shared.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vip.aquan.generatebarcode.shared.common.BusinessCode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestResponse<T> {
    private static final String SUCCESS_MESSAGE = "Succeeded";
    private static final String FAIL_MESSAGE = "Failed";

    private static final String CODE_OK = BusinessCode.RSC_200.getCode();
    private static final String CODE_ERROR = "Error";

    private String code;
    private String message;
    private T data;
    public RestResponse(String message, T data){
        this.code = CODE_OK;
        this.message = message;
        this.data = data;
    }
    public RestResponse(T data){
        this.code = CODE_OK;
        this.message = SUCCESS_MESSAGE;
        this.data = data;
    }

    public static RestResponse<Object> ok() {
        RestResponse<Object> okResponse = new RestResponse<Object>(CODE_OK, SUCCESS_MESSAGE, null);
        return okResponse;
    }

    public static RestResponse<Object> ok(String message) {
        RestResponse<Object> okResponse = new RestResponse<Object>(CODE_OK, message, null);
        return okResponse;
    }

    public static RestResponse<Object> fail(String message) {
        RestResponse<Object> okResponse = new RestResponse<Object>(CODE_ERROR, message, null);
        return okResponse;
    }

    public static RestResponse<Object> fail(BusinessCode bc, String message) {
        RestResponse<Object> okResponse = new RestResponse<Object>(bc.getCode(), message, null);
        return okResponse;
    }

    public String getCode() {
        return code;
    }

    public RestResponse<T> setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public RestResponse<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public RestResponse<T> setData(T data) {
        this.data = data;
        return this;
    }
}

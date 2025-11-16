package yuseong.com.guchung.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GlobalResponseDto<T> {
    private int statusCode;
    private String message;
    private T data;

    public static <T> GlobalResponseDto<T> success(String message, T data) {
        return new GlobalResponseDto<>(200, message, data);
    }

    public static <T> GlobalResponseDto<T> error(String message) {
        return new GlobalResponseDto<>(400, message, null);
    }
}
package sherry.common.exphandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YyghException extends RuntimeException {
    private Integer code;//自定义的异常状态码
    private String msg;//自定义的异常信息
}

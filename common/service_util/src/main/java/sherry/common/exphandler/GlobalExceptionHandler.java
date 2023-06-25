package sherry.common.exphandler;

import sherry.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {


    //当接口中出现Exception类型的异常时，最后就会执行到该方法
    //方法的参数：目标接口所出现的异常
    //目标接口出现异常，最后return 返回R.error().message(exp.getMessage())
    @ExceptionHandler(value = Exception.class)
    public R handlerException(Exception exp){
        return R.error().message(exp.getMessage());
    }


    @ExceptionHandler(value = ArithmeticException.class)
    public R handlerException(ArithmeticException exp){
        return R.error().message(exp.getMessage());
    }


    @ExceptionHandler(value = NullPointerException.class)
    public R handlerException(NullPointerException exp){
        return R.error().message(exp.getMessage());
    }

    @ExceptionHandler(value = YyghException.class)
    public R handlerException(YyghException exp){

        log.error("出现了异常，" + exp.getMsg());

        //自定义异常
        return R.error().message( "自定义异常，" + exp.getMsg());
    }


}

package sherry.common.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Data：使用该注解可以自动生成getter、setter、equals、canEqual、hashCode、toString方法。
 * @NoArgsConstructor：使用该注解可以生成无参构造函数。
 * @AllArgsConstructor：使用该注解可以生成全参构造函数。
 * @ApiModelProperty：用于描述一个请求或响应参数的模型数据，包括参数名称、类型、说明等信息。
 * @Author: SHERRY
 * @email: <a href="mailto:SherryTh743779@gmail.com">TianHai</a>
 * @Date: 2023/6/25 19:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public  class R {

    private Integer code;
    private String message;
    private boolean success;
    private Map<String, Object> data = new HashMap<>();

    //将创建r对象的过程封装到ok方法中
    public static R ok() {

        R r = new R();
        r.setCode(ResultCode.SUCCESS);
        r.setSuccess(true);
        r.setMessage("操作成功");

        return r;
    }

    //操作失败，默认的r对象
    public static R error() {

        R r = new R();
        r.setCode(ResultCode.ERROR);
        r.setSuccess(false);
        r.setMessage("操作失败");

        return r;
    }

    //向r对象的data中添加一组key-value
    public R data(String key, Object value) {
        //哪个对象的data属性？ this表示调用data方法的r对象
        this.getData().put(key, value);
        return this;
    }

    //将参数map，直接赋值给r对象的data属性
    public R data(Map<String, Object> map) {
        this.setData(map);
        return this;
    }


    //修改r对象的message
    public R message(String message) {
        this.setMessage(message);
        return this;
    }


    //修改r对象中的code值
    public R code(Integer code) {
        this.setCode(code);
        return this;
    }

    public R success(Boolean success) {
        this.setSuccess(success);
        return this;
    }
}
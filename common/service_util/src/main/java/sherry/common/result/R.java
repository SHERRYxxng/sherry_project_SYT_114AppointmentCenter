package sherry.common.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;


//使用swagger注解描述类的作用，属性的作用
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description="公共的返回结果类")
public class R {

    @ApiModelProperty(name = "code",value = "自定义的响应状态码，20000表示成功，20001表示失败")
    private Integer code;

    @ApiModelProperty(name = "message",value = "自定义的描述信息")
    private String message;

    @ApiModelProperty(name = "success",value = "布尔类型返回值，true表示成功，false表示失败")
    private boolean success;

    @ApiModelProperty(name = "data",value = "接口返回的业务数据存放在data中")
    private Map<String,Object> data = new HashMap<>();

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
    public R data(String key,Object value) {
        //哪个对象的data属性？ this表示调用data方法的r对象
        this.getData().put(key,value);
        return this;
    }

    //将参数map，直接赋值给r对象的data属性
    public R data(Map<String,Object> map) {
        this.setData(map);
        return this;
    }


    //修改r对象的message
    public R message(String message){
        this.setMessage(message);
        return this;
    }


    //修改r对象中的code值
    public R code(Integer code){
        this.setCode(code);
        return this;
    }

    public R success(Boolean success){
        this.setSuccess(success);
        return this;
    }

}

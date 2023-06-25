package sherry.hosp.controller;





import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sherry.common.result.R;
import sherry.common.result.ResultCode;
import sherry.hosp.service.HospitalSetService;
import sherry.model.hosp.HospitalSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: SHERRY
 * @email: <a href="mailto:SherryTh743779@gmail.com">TianHai</a>
 * @Date: 2023/6/25 19:00
 */
//医院设置接口
@CrossOrigin //跨域,服務器的安全設置問題
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

    @GetMapping("{id}")
    public R findById(@PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);

//        R r = new R();
//        r.setCode(ResultCode.SUCCESS);
//        r.setSuccess(true);
//        r.setMessage("操作成功");
//        r.getData().put("item",hospitalSet);//前提是R中的data必须先初始化

//        return R.ok().data("item",hospitalSet).data("aa","1").data("bb","2");
        Map<String,Object> map = new HashMap<>();
        map.put("aa",1);
        map.put("bb",2);
        map.put("item",hospitalSet);

        return R.ok().data(map).message("根据id查询成功");
    }

    @DeleteMapping("{id}")
    public R deleteById(@PathVariable Long id){
        boolean b = hospitalSetService.removeById(id);
        return b?R.ok().message("根据id删除成功"):R.error().message("根据id删除失败");
    }

}
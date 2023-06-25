package sherry.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sherry.common.result.R;
import sherry.hosp.service.HospitalSetService;
import sherry.model.hosp.HospitalSet;
import sherry.vo.hosp.HospitalSetQueryVo;

import java.util.List;

//原则：后台管理系统使用的接口，通常/admin开头
@Api(description = "医院设置接口文档")
@RestController
@RequestMapping("/admin/hosp/hospset")

@Slf4j //lombok下的一个日志注解，在当前类中就可以直接使用log对象调用其方法，打印不同级别的日志
public class HospitalSetController {

    @Autowired
    HospitalSetService hospitalSetService;


    //医院设置的批量删除  示例数据： [ 4,6,7 ]
    @ApiOperation(value = "根据id删除医院设置（逻辑删除）")
    @DeleteMapping("deleteByIds")
    public R deleteByIds(@ApiParam(name = "ids", value = "医院设置的id集合") @RequestBody List<Long> ids) {
        boolean b = hospitalSetService.removeByIds(ids);
        return b ? R.ok().message("批量删除成功") : R.error().message("批量删除失败");
    }


    //修改医院设置的信息（根据id修改医院设置）
    //参数示例：
    /*{
        "id":7, //必选参数
        "contactsName":"李四"
    }*/
    @PostMapping("updateHospset")
    public R updateHospset(@RequestBody HospitalSet hospitalSet) {

        //可以校验是否有id
        Long id = hospitalSet.getId();
        if (id == null) {
            return R.error().message("id为空");
        }

//        hospitalSet.setUpdateTime(new Date());

        boolean b = hospitalSetService.updateById(hospitalSet);

        return b ? R.ok().message("修改成功") : R.error().message("修改失败");
    }


    //医院设置列表的条件+分页查询（pageNum，pageSize  分页从1开始）  查询条件：医院名称模糊查询 , 医院编号等值查询  { hosname:xxx,hoscode:xx }
    //返回值 ： { data:{total:10,rows:[当前页的结果集]} }
    @PostMapping("{pageNum}/{pageSize}")
    public R pageQuery(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @RequestBody HospitalSetQueryVo hospitalSetQueryVo) {

        //1、创建page分页对象(如果传入的小于1的值，默认也是从1开始)
        Page<HospitalSet> hospitalSetPage = new Page<>(pageNum, pageSize);

        //2、封装查询条件
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        String hoscode = hospitalSetQueryVo.getHoscode();
        String hosname = hospitalSetQueryVo.getHosname();

        //where hosname like ?  and hoscode = ?
        //where hosname like null  and hoscode = null  必须动态sql拼接（先判空，如果不为空，再拼接条件）
        if (!StringUtils.isEmpty(hoscode)) { //注意： ！ 不为空
            queryWrapper.eq("hoscode", hoscode);//表中的列名
        }
        if (!StringUtils.isEmpty(hosname)) {
            queryWrapper.like("hosname", hosname);
        }

        //3、调用service中page方法（注意：配置分页插件PaginationInterceptor）
        hospitalSetService.page(hospitalSetPage, queryWrapper);


        //4、从hospitalSetPage对象中解析返回值
        List<HospitalSet> records = hospitalSetPage.getRecords();//当前页的结果集
        long total = hospitalSetPage.getTotal();//总记录数（前端的分页控件需要使用总记录数）


        return R.ok().data("total", total).data("rows", records);
    }


    //需求：医院设置的锁定和解锁。修改status字段值，status的取值范围0和1； 该接口中需要校验取值范围
    //参数：医院设置的id +  希望修改成的状态值
    @GetMapping("lockHospset/{id}/{status}")
    public R updateStatus(@PathVariable Long id, @PathVariable Integer status) {

        //1、根据id查询到该医院设置，并且判断是否存在（未开通医院设置） 返回 message=未开通医院设置 code=20001
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        if (hospitalSet == null) {
            return R.error().message("未开通医院设置");
        }

        //2、判断status的取值范围，如果status不是0或1，返回message=status取值范围不正确  code=20001
        if (status != 1 && status != 0) {
            return R.error().message("status取值范围不正确");
        }

        //3、判断是否重复操作，如果status原来是1，现在要改成1，这就是重复操作，返回message=重复操作  code=20001
        if (status == hospitalSet.getStatus()) {
            return R.error().message("重复操作");
        }

        //4、更新
        hospitalSet.setStatus(status);
        boolean b = hospitalSetService.updateById(hospitalSet);

        return b ? R.ok() : R.error();
    }


    //需求：开通医院设置（其实就添加）  ， 如果某个医院想入驻到尚医通平台，必须先由管理员在后台系统为该医院开通权限；信息由医院端提供，管理员负责录入
    // 需要提供的数据：
    //以下是北京协和医院的示例数据
    /*{
            "apiUrl": "http://127.0.0.1:9998", 医院端的接口地址（提交挂号订单时，会去调用医院端的接口）
            "contactsName": "张三",
            "contactsPhone": "13101102345",
            "hoscode": "10000", //医院编号，具备唯一性
            "hosname": "北京协和医院",//医院名称
            "signKey": "1" //医院端的签名
    }*/
    //要求：请求方式：post  路径：saveHospset  参数：json { apiUrl contactsName hoscode ... }  医院设置的status状态默认为1（正常状态）

    @ApiOperation(value = "开通医院设置")
    @PostMapping("saveHospset")
    public R saveHospset(@ApiParam(name = "hospitalSet", value = "医院设置信息") @RequestBody HospitalSet hospitalSet) {

        hospitalSet.setStatus(1);//0-医院权限锁定  1-正常
        boolean save = hospitalSetService.save(hospitalSet);

        return save ? R.ok().message("开通成功") : R.error().message("开通失败");
    }


    /**
     * 查询所有医院设置
     *
     * @return {
     * "code": 20000,
     * "message": "操作成功",
     * "success": true,
     * "data": {
     * "list": []
     * }
     * }
     */
    @GetMapping("/findAll")
    public R findAll() {

        //不同的级别，表示不同的严重程度
//        log.info("我是一条info级别的日志");
//        log.error("我是一条error级别的日志");
//        log.warn("我是一条warn级别的日志");


        List<HospitalSet> list = null;
//        try {
        list = hospitalSetService.list();

        if(list==null || list.isEmpty()){
//            log.info("医院设置列表为空");
        }

//        int i = 1 / 0;

//        String str = null;
//        if(StringUtils.isEmpty(str)){
//            throw new YyghException(20001,"字符串为空");
//        }
//        str.length();
//        } catch (Exception e) {
////            e.printStackTrace();
//            return R.error().message(e.getMessage());
//        }

        return R.ok().data("list", list);
    }


    /**
     * 根据id查询某个医院设置
     *
     * @param id
     * @return { code:xx,message:'',success:'',data:{item: {医院设置对象} } }
     */
    @GetMapping("{id}")
    public R findById(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);

//        R r = new R();
//        r.setCode(ResultCode.SUCCESS);
//        r.setSuccess(true);
//        r.setMessage("操作成功");
//        r.getData().put("item",hospitalSet);//前提是R中的data必须先初始化

//        return R.ok().data("item",hospitalSet).data("aa","1").data("bb","2");
//        Map<String,Object> map = new HashMap<>();
//        map.put("aa",1);
//        map.put("bb",2);
//        map.put("item",hospitalSet);

        return R.ok().message("根据id查询成功").data("item", hospitalSet);
    }


    /**
     * 根据id删除某个医院设置
     *
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    public R deleteById(@PathVariable Long id) {
        boolean b = hospitalSetService.removeById(id);
        return b ? R.ok().message("根据id删除成功") : R.error().message("根据id删除失败");
    }

}

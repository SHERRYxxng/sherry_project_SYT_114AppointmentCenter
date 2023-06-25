package sherry.hosp.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


//mapper接口所在的包名
@EnableTransactionManagement//开启事务
@MapperScan(basePackages = "com.atguigu.yygh.hosp.mapper")
@Configuration //当前是一个配置类（可以配置bean组件）
public class HospConfig {

    //分页插件
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }

}

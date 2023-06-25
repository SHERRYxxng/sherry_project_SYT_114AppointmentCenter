package sherry.hosp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Description:
 * @Author: SHERRY
 * @email: <a href="mailto:SherryTh743779@gmail.com">TianHai</a>
 * @Date: 2023/6/25 18:44
*/
 @SpringBootApplication
 @ComponentScan(basePackages = {"sherry"})
public class ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class, args);
    }
}
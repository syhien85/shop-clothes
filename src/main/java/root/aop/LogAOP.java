package root.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogAOP {

    // *1: Kiểu trả về, *2: các tham số truyền vào của function
    // chặn trước khi gọi function getById()
    @Before("execution(* root.service.CategoryService.getById(*))")
    public void getByCategoryId(JoinPoint joinPoint) {
        int id = (int) joinPoint.getArgs()[0]; // array các tham số

        log.warn("JOIN POINT service.CategoryService.getById : " + id);

    }
}

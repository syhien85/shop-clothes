package root.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    // duyệt các request trước khi gọi lên handler
    @Override
    public boolean preHandle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler
    ) {
        // log.info("INTERCEPTOR");
        log.warn(request.getMethod() + ":" + request.getServletPath());

        return true; // true: di tiep, false: dung lai
    }

    // sau khi các request được thực thi xong, trước khi response về client
    @Override
    public void afterCompletion(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler,
        Exception ex
    ) {
        // log.info("INTERCEPTOR DONE");
    }
}

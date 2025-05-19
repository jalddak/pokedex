package pokemon.pokedex._common.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import pokemon.pokedex._common.log.LogTrace;
import pokemon.pokedex._common.log.TraceStatus;

import java.util.Stack;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogTraceAspect {

    private static final String INTERCEPTOR_TRACE_STATUS = "interceptorTraceStatus";
    private final LogTrace logTrace;

    @Pointcut("execution(* pokemon.pokedex..*(..))")
    public void myProject() {
    }

    @Pointcut("bean(*Controller*))")
    public void controller() {
    }

    @Pointcut("bean(*Service*)")
    public void service() {
    }

    @Pointcut("bean(*Repository*)")
    public void repository() {
    }

    @Pointcut("bean(*Registry*)")
    public void registry() {
    }

    @Pointcut("bean(*Listener*)")
    public void listener() {
    }

    @Pointcut("bean(*Filter*)")
    public void filter() {
    }

    @Pointcut("bean(*Interceptor*)")
    public void interceptor() {
    }

    @Pointcut("controller() || service() || repository() || registry() || listener() || filter()")
    public void core() {
    }

    @Around("myProject() && core()")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        TraceStatus status = null;
        try {
            String message = joinPoint.getSignature().toShortString();
            status = logTrace.begin(message);

            //로직 호출
            Object result = joinPoint.proceed();

            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }

    @Around("myProject() && interceptor() && execution(* preHandle(..)) && args(request, ..)")
    public Object interceptorPreHandle(ProceedingJoinPoint joinPoint, HttpServletRequest request) throws Throwable {
        TraceStatus status = null;
        try {
            String message = joinPoint.getTarget().getClass().getSimpleName();
            status = logTrace.begin(message);

            log.info("{}", joinPoint.getSignature().toShortString());
            // 인터페이스 계층별로 requestAttribute에 넣어줘야함
            if (request.getAttribute(INTERCEPTOR_TRACE_STATUS) == null) {
                request.setAttribute(INTERCEPTOR_TRACE_STATUS, new Stack<TraceStatus>());
            }
            Stack<TraceStatus> stack = (Stack<TraceStatus>) request.getAttribute(INTERCEPTOR_TRACE_STATUS);
            stack.push(status);

            Object result = joinPoint.proceed();
            // 만약 preHandle에서 false 반환하면 afterCompletion을 불러오지 않으므로 stack에서 status를 바로 빼줘야함
            if (!(boolean) result) stack.pop();

            long resultTimeMs = System.currentTimeMillis() - status.getStartTimeMs();
            log.info("{} time={}ms", joinPoint.getSignature().toShortString(), resultTimeMs);

            boolean checkResult = (boolean) result;
            if (!checkResult) logTrace.end(status);

            return result;
        } catch (Exception e) {
            long resultTimeMs = System.currentTimeMillis() - status.getStartTimeMs();
            log.info("{} time={}ms ex={}", joinPoint.getSignature().toShortString(), resultTimeMs, e.toString());
            throw e;
        }
    }

    @Around("myProject() && interceptor() && execution(* postHandle(..)) && args(request, ..)")
    public Object interceptorPostHandle(ProceedingJoinPoint joinPoint, HttpServletRequest request) throws Throwable {
        Long methodStartTimeMs = System.currentTimeMillis();
        try {
            log.info("{}", joinPoint.getSignature().toShortString());

            Object result = joinPoint.proceed();

            long resultTimeMs = System.currentTimeMillis() - methodStartTimeMs;
            log.info("{} time={}ms", joinPoint.getSignature().toShortString(), resultTimeMs);

            return result;
        } catch (Exception e) {
            long resultTimeMs = System.currentTimeMillis() - methodStartTimeMs;
            log.info("{} time={}ms ex={}", joinPoint.getSignature().toShortString(), resultTimeMs, e.toString());
            throw e;
        }
    }

    @Around("myProject() && interceptor() && execution(* afterCompletion(..)) && args(request, ..)")
    public Object interceptorAfterCompletion(ProceedingJoinPoint joinPoint, HttpServletRequest request) throws Throwable {
        Stack<TraceStatus> stack = (Stack<TraceStatus>) request.getAttribute(INTERCEPTOR_TRACE_STATUS);
        TraceStatus status = stack.pop();
        Long methodStartTimeMs = System.currentTimeMillis();
        try {
            log.info("{}", joinPoint.getSignature().toShortString());

            Object result = joinPoint.proceed();

            long resultTimeMs = System.currentTimeMillis() - methodStartTimeMs;
            log.info("{} time={}ms", joinPoint.getSignature().toShortString(), resultTimeMs);
            logTrace.end(status);

            return result;
        } catch (Exception e) {
            long resultTimeMs = System.currentTimeMillis() - methodStartTimeMs;
            log.info("{} time={}ms", joinPoint.getSignature().toShortString(), resultTimeMs);
            logTrace.exception(status, e);
            throw e;
        }
    }

}

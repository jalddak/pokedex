package pokemon.pokedex._common.log;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Slf4jLogTrace implements LogTrace {

    private static final String START_PREFIX = "|-->| ";
    private static final String COMPLETE_PREFIX = "|<--| ";
    private static final String EX_PREFIX = "|<X-| ";
    private static final String MID_PREFIX = "|   | ";

    ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("|   ");
        }
        sb.append(prefix);
        return sb.toString();
    }

    @Override
    public TraceStatus begin(String message) {
        syncTraceId();
        Long startTimeMs = System.currentTimeMillis();
        int level = traceIdHolder.get().getLevel();

        MDC.put("indent", addSpace(START_PREFIX, level));
        log.info("{}", message);
        MDC.put("indent", addSpace(MID_PREFIX, level));
        return new TraceStatus(startTimeMs, message);
    }

    private void syncTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId == null) {
            traceId = new TraceId();
            traceIdHolder.set(traceId);
            MDC.put("traceId", traceId.getId());
        } else {
            traceIdHolder.set(traceId.createNextId());
        }
    }

    @Override
    public void end(TraceStatus status) {
        complete(status, null);
    }

    @Override
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }

    private void complete(TraceStatus status, Exception e) {
        Long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        int level = traceIdHolder.get().getLevel();
        if (e == null) {
            MDC.put("indent", addSpace(COMPLETE_PREFIX, level));
            log.info("{} time={}ms", status.getMessage(), resultTimeMs);
        } else {
            MDC.put("indent", addSpace(EX_PREFIX, level));
            log.warn("{} time={}ms ex={}", status.getMessage(), resultTimeMs, e.toString());
        }
        releaseTraceId();
    }

    private void releaseTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId.isFirstLevel()) {
            traceIdHolder.remove();
            MDC.clear();
        } else {
            traceIdHolder.set(traceId.createPreviousId());
            MDC.put("indent", addSpace(MID_PREFIX, traceIdHolder.get().getLevel()));
        }
    }
}

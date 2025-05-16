package pokemon.pokedex._common.log;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class Slf4jLogTraceTest {

    Slf4jLogTrace logTrace;

    @BeforeEach
    void setUp() {
        logTrace = new Slf4jLogTrace();
        MDC.clear();
    }

    @Test
    void begin_shouldInitializeTraceIdAndSetMDC() {
        TraceStatus status = logTrace.begin("Test Message");
        assertNotNull(status);
        assertNotNull(MDC.get("traceId"));
    }

    @Test
    void end_shouldClearOrDecreaseLevelInTraceId() {
        TraceStatus status = logTrace.begin("Test Message");
        logTrace.end(status);
        assertNull(MDC.get("traceId"));
        assertNull(MDC.get("indent"));
    }

    @Test
    void exception_shouldSetExceptionLog() {
        TraceStatus status = logTrace.begin("Test Exception");
        logTrace.exception(status, new RuntimeException("error"));
        assertNull(MDC.get("traceId"));
        assertNull(MDC.get("indent"));
    }
}

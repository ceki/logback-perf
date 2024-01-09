package ch.qos.logback.perf;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

public class NOPAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    public int count = 0;

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        count++;
    }
}

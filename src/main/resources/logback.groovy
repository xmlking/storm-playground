import ch.qos.logback.classic.AsyncAppender
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.WARN

// for debugging logback. keep this line first.
//statusListener(OnConsoleStatusListener)

scan( '10 minutes' )

jmxConfigurator( 'storm' )

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        //pattern = "%green(%d{HH:mm:ss.SSS}) [%thread] %highlight(%-5level) %cyan(%logger{36}) - %msg%n"
        pattern = "%green(%d{HH:mm:ss.SSS}) %highlight(%-5level)[%thread] %cyan(%logger{36}) - %msg%n"
    }
}

appender("FILE", RollingFileAppender) {
    file = "logs/storm.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS/zzz} [%thread] %-5level %logger{36} - %msg%n"
    }
    rollingPolicy(FixedWindowRollingPolicy) {
        maxIndex = 5
        fileNamePattern = "logs/storm.%i.log"
    }
    triggeringPolicy(SizeBasedTriggeringPolicy) {
        maxFileSize = "1MB"
    }
}

appender("CONSOLE-ASYNC", AsyncAppender) {
    appenderRef('CONSOLE')
}
appender("FILE-ASYNC", AsyncAppender) {
    appenderRef('FILE')
}

logger 'storm', DEBUG
logger 'org.sumo.storm', DEBUG

if(System.getProperty("spring.profiles.active")?.equalsIgnoreCase("prod")) {
    root WARN, ["CONSOLE-ASYNC", "FILE-ASYNC"]
} else {
    root WARN, ["CONSOLE"]
}


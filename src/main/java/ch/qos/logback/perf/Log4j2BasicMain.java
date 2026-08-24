package ch.qos.logback.perf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log4j2BasicMain {


    public static void main(String[] args) {
        System.setProperty("log4j2.configurationFile", "classpath:log4j2-minimal.xml");
        Logger log4j2Logger = LogManager.getLogger(Log4j2BasicMain.class);
        log4j2Logger.warn("This is a WARN message");

        System.Logger logger = System.getLogger(Log4j2BasicMain.class.getName());
        logger.log(System.Logger.Level.OFF, "Hello from PlatformLogger");

    }
}

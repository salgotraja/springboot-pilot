package io.js.actuator.domain;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ScheduledJobs {
    //private final Logger log = LoggerFactory.getLogger(ScheduledJobs.class);

    @Scheduled(fixedRate = 30000)
    public void every30SecondsJob() {
        log.info("Every30seconds job is running at "+ LocalDateTime.now());
    }

    @Scheduled(cron = "0 * * * * MON-FRI")
    public void everyMinuteJob() {
        log.info("everyMinuteJob job is running at "+ LocalDateTime.now());
    }
}

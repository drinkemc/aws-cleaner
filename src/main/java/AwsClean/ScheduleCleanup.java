package AwsClean;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleCleanup {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ScheduleCleanup.class);
    private Cleaner cleaner;

    @Autowired
    public ScheduleCleanup(Cleaner cleaner) {
        this.cleaner = cleaner;
    }

    @Scheduled(fixedRate = 30000)
    public void removeOldFiles() {
        LOG.info("Beginning Disk Cleanup Processing");

        cleaner.cleanupIngestMount();
    }
}

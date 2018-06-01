package AwsClean;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;

@Component
public class Cleaner {

    private static final Logger LOG = LoggerFactory.getLogger(Cleaner.class);
    private DateTime timeOfDemarcation = DateTime.now().minusHours(48);

    @Value("${cleaner.mountpoint}")
    File directory;

    public void cleanupIngestMount() {
        long percentDiskRemaining = calculatePercentRemaining();

        if (percentDiskRemaining < 20L) {
            removeFilesGreaterThan48HoursOld();
        }
    }

    protected long calculatePercentRemaining() {
        long freeDisk = directory.getFreeSpace();
        long totalDisk = directory.getTotalSpace();

        return (freeDisk*100)/totalDisk;
    }

    protected void removeFilesGreaterThan48HoursOld() {
        LOG.info("Looking for files older than 48 hours to remove");

        File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().contains(".avro") || pathname.getName().endsWith(".gz")
                        || pathname.getName().startsWith("dt=");
            }
        });

        if (files != null) {
            for (File file : files) {
                DateTime fileDateTime = new DateTime(file.lastModified());
                if (fileDateTime.isBefore(timeOfDemarcation)) {
                    if (file.isDirectory() && (file.listFiles() != null)) {
                        LOG.info(file.getName() + " is a directory which is not empty and will not be deleted.");
                    } else {
                        file.delete();
                        LOG.info("The following file was deleted from the /mnt/data/ad_activity: " + file.getName());
                    }
                }
            }
        }
    }
}

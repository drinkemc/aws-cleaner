package AwsClean;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.FileFilter;

@RunWith(MockitoJUnitRunner.class)
public class CleanerTest {

    @InjectMocks
    private Cleaner cleaner;

    @Mock
    private File directory;

    Cleaner spyCleaner;

    File[] files;

    @Before
    public void setup() {
        spyCleaner = Mockito.spy(cleaner);

        File file1 = new File("testfile");
        file1.setLastModified(DateTime.now().minusHours(48).getMillis());

        File file2 = new File("testFile.gz");
        file2.setLastModified(Instant.now().minus(172800001L).getMillis());

        files = new File[] {file1, file2};
    }

    @Test
    public void calculatePercentRemaining_FreeDisk20TotalDisk100_ResultIs20() {
        Mockito.when(directory.getFreeSpace()).thenReturn(20L);
        Mockito.when(directory.getTotalSpace()).thenReturn(1000L);

        Long result = cleaner.calculatePercentRemaining();

        assert result.equals(2L);
    }

    @Test
    public void calculatePercentRemaining_FreeDisk19TotalDisk100_ResultIs19() {
        Mockito.when(directory.getFreeSpace()).thenReturn(19L);
        Mockito.when(directory.getTotalSpace()).thenReturn(1000L);

        Long result = cleaner.calculatePercentRemaining();

        assert result.equals(1L);
    }

    @Test
    public void cleanupIngestMount_UnderThreshold_NoFilesCleaned() {
        Mockito.when(directory.getFreeSpace()).thenReturn(30L);
        Mockito.when(directory.getTotalSpace()).thenReturn(100L);

        spyCleaner.cleanupIngestMount();

        Mockito.verify(spyCleaner, Mockito.never()).removeFilesGreaterThan48HoursOld();
    }

    @Test
    public void cleanupIngestMount_OverThreshold_FilesCleaned() {
        Mockito.when(directory.getFreeSpace()).thenReturn(10L);
        Mockito.when(directory.getTotalSpace()).thenReturn(100L);

        spyCleaner.cleanupIngestMount();

        Mockito.verify(spyCleaner, Mockito.times(1)).removeFilesGreaterThan48HoursOld();
    }


    //Finish Me!
    @Test
    public void removeFilesGreaterThan48HoursOld_FilesOlderThan48Hours_FilesRemoved() {
        Mockito.when(directory.listFiles(Mockito.any(FileFilter.class))).thenReturn(files);

        cleaner.removeFilesGreaterThan48HoursOld();
        assert files.length == 1;
    }
}

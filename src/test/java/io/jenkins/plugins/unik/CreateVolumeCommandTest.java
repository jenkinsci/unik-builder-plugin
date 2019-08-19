package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.CreateImageCommand;
import io.jenkins.plugins.unik.cmd.CreateVolumeCommand;
import org.junit.Test;

public class CreateVolumeCommandTest extends AbstractUnikCommandTest {

    @Test
    public void testBuildAllParamsSuccess() throws Exception {
        successTest(new CreateVolumeCommand("volumeName", "ext2", true, true, "provider", "100", "data"));
    }

    @Test
    public void testBuildMinimalWithDataParamsSuccess() throws Exception {
        successTest(new CreateVolumeCommand("volumeName", "ext2", true, true, "provider", null, "data"));
    }


    @Test
    public void testBuildMinimalWithSizeParamsSuccess() throws Exception {
        successTest(new CreateVolumeCommand("volumeName", "ext2", false, true, "provider", "100", null));
    }

    @Test
    public void testBuildSizeNaNSuccess() throws Exception {
        successTest(new CreateVolumeCommand("volumeName", "ext2", false, true, "provider", "NaN", "data"),"Not a valid volume size");
    }

    @Test
    public void testBuildSizeNegativeSuccess() throws Exception {
        successTest(new CreateVolumeCommand("volumeName", "ext2", false, true, "provider", "-1", "data"),"Not a valid volume size");
    }

    @Test
    public void testBuildMissingDataAndInvalidSizeFailure() throws Exception {
        failureTest(new CreateVolumeCommand("volumeName", "ext2", true, true, "provider", "NaN", null), "Either a data or a volume size must be specified");
    }

    @Test
    public void testBuildMissingDataAndSizeFailure() throws Exception {
        failureTest(new CreateVolumeCommand("volumeName", "ext2", true, true, "provider", null, null), "Either a data or a volume size must be specified");
    }

    @Test
    public void testBuildMissingVolumeNameFailure() throws Exception {
        failureTest(new CreateVolumeCommand(null, "ext2", true, true, "provider", "100", null), "Volume name can not be empty");
    }

    @Test
    public void testBuildMissingProviderFailure() throws Exception {
        failureTest(new CreateImageCommand("folder", "imageName", null, "base", "language", false, false, null, null), "Provider can not be empty");
    }

    @Test
    public void testBuildMissingBaseFailure() throws Exception {
        failureTest(new CreateImageCommand("folder", "imageName", "provider", null, "language", false, false, null, null), "Unikernel base can not be empty");
    }

    @Test
    public void testBuildMissingLanguageFailure() throws Exception {
        failureTest(new CreateImageCommand("folder", "imageName", "provider", "base", null, false, false, null, null), "Language can not be empty");
    }
}

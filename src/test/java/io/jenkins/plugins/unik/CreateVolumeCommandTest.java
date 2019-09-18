package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.CreateVolumeCommand;
import org.junit.Test;

public class CreateVolumeCommandTest extends AbstractUnikCommandTest {

    @Test
    public void testBuildAllParamsSuccess() throws Exception {
        CreateVolumeCommand command = new CreateVolumeCommand("volumeName", "provider");
        command.setRaw(true);
        command.setNoCleanup(true);
        command.setType("ext2");
        command.setSize("100");
        command.setData("data");

        successTest(command);
    }

    @Test
    public void testBuildMinimalWithDataParamsSuccess() throws Exception {
        CreateVolumeCommand command = new CreateVolumeCommand("volumeName", "provider");
        command.setRaw(true);
        command.setNoCleanup(true);
        command.setType("ext2");
        command.setSize(null);
        command.setData("data");

        successTest(command);
    }


    @Test
    public void testBuildMinimalWithSizeParamsSuccess() throws Exception {
        CreateVolumeCommand command = new CreateVolumeCommand("volumeName", "provider");
        command.setRaw(false);
        command.setNoCleanup(true);
        command.setType("ext2");
        command.setSize("100");
        command.setData(null);
        successTest(command);
    }

    @Test
    public void testBuildSizeNaNSuccess() throws Exception {
        CreateVolumeCommand command = new CreateVolumeCommand("volumeName", "provider");
        command.setRaw(false);
        command.setNoCleanup(true);
        command.setType("ext2");
        command.setSize("NaN");
        command.setData("data");
        successTest(command, "Not a valid volume size");
    }

    @Test
    public void testBuildSizeNegativeSuccess() throws Exception {
        CreateVolumeCommand command = new CreateVolumeCommand("volumeName", "provider");
        command.setRaw(true);
        command.setNoCleanup(true);
        command.setType("ext2");
        command.setSize("-1");
        command.setData("data");
        successTest(command, "Not a valid volume size");
    }

    @Test
    public void testBuildMissingDataAndInvalidSizeFailure() throws Exception {
        CreateVolumeCommand command = new CreateVolumeCommand("volumeName", "provider");
        command.setRaw(true);
        command.setNoCleanup(true);
        command.setType("ext2");
        command.setSize("NaN");
        command.setData(null);
        failureTest(command, "Either a data or a volume size greater 0 must be specified");
    }

    @Test
    public void testBuildMissingDataAndSizeFailure() throws Exception {
        CreateVolumeCommand command = new CreateVolumeCommand("volumeName", "provider");
        command.setRaw(true);
        command.setNoCleanup(true);
        command.setType("ext2");
        command.setSize(null);
        command.setData(null);
        failureTest(command, "Either a data or a volume size greater 0 must be specified");
    }

    @Test
    public void testBuildMissingVolumeNameFailure() throws Exception {
        CreateVolumeCommand command = new CreateVolumeCommand(null, "provider");
        command.setRaw(true);
        command.setNoCleanup(true);
        command.setType("ext2");
        command.setSize("100");
        command.setData(null);
        failureTest(command, "Volume name can not be empty");
    }

    @Test
    public void testBuildMissingProviderFailure() throws Exception {
        CreateVolumeCommand command = new CreateVolumeCommand("volumeName", null);
        command.setRaw(true);
        command.setNoCleanup(true);
        command.setType("ext2");
        command.setSize("100");
        command.setData("data");
        failureTest(command, "Provider can not be empty");
    }
}

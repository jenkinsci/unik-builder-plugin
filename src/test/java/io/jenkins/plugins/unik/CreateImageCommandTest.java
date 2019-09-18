package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.CreateImageCommand;
import org.junit.Test;

public class CreateImageCommandTest extends AbstractUnikCommandTest {

    @Test
    public void testBuildAllParamsSuccess() throws Exception {
        CreateImageCommand command = new CreateImageCommand("folder", "imageName", "provider", "base", "language");
        command.setForce(true);
        command.setNoCleanup(true);
        command.setArgs("arg1 arg2");
        command.setMounts("mount1 mount2");
        successTest(command);
    }

    @Test
    public void testBuildMinimalParamsSuccess() throws Exception {
        CreateImageCommand command = new CreateImageCommand("folder", "imageName", "provider", "base", "language");

        successTest(command);
    }

    @Test
    public void testBuildMissingUnikFolderFailure() throws Exception {
        CreateImageCommand command = new CreateImageCommand(null, "imageName", "provider", "base", "language");
        command.setForce(true);
        command.setNoCleanup(true);
        command.setArgs("arg1 arg2");
        command.setMounts("mount1 mount2");
        failureTest(command, "Application location can not be empty");
    }

    @Test
    public void testBuildMissingImageNameFailure() throws Exception {
        CreateImageCommand command = new CreateImageCommand("folder", null, "provider", "base", "language");
        command.setForce(true);
        command.setNoCleanup(true);
        command.setArgs("arg1 arg2");
        command.setMounts("mount1 mount2");
        failureTest(command, "Image name can not be empty");
    }

    @Test
    public void testBuildMissingProviderFailure() throws Exception {
        CreateImageCommand command = new CreateImageCommand("folder", "imageName", null, "base", "language");
        command.setForce(true);
        command.setNoCleanup(true);
        command.setArgs("arg1 arg2");
        command.setMounts("mount1 mount2");
        failureTest(command, "Provider can not be empty");
    }

    @Test
    public void testBuildMissingBaseFailure() throws Exception {
        CreateImageCommand command = new CreateImageCommand("folder", "imageName", "provider", null, "language");
        command.setForce(true);
        command.setNoCleanup(true);
        command.setArgs("arg1 arg2");
        command.setMounts("mount1 mount2");
        failureTest(command, "Unikernel base can not be empty");
    }

    @Test
    public void testBuildMissingLanguageFailure() throws Exception {
        CreateImageCommand command = new CreateImageCommand("folder", "imageName", "provider", "base", null);
        command.setForce(true);
        command.setNoCleanup(true);
        command.setArgs("arg1 arg2");
        command.setMounts("mount1 mount2");
        failureTest(command, "Language can not be empty");
    }
}

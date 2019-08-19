package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.CreateImageCommand;
import org.junit.Test;

public class CreateImageCommandTest extends AbstractUnikCommandTest {

    @Test
    public void testBuildAllParamsSuccess() throws Exception {
        successTest(new CreateImageCommand("folder", "imageName", "provider", "base", "language", true, true, "arg1 arg2", "mount1 mount2"));
    }

    @Test
    public void testBuildMinimalParamsSuccess() throws Exception {
        successTest(new CreateImageCommand("folder", "imageName", "provider", "base", "language", false, false, null, null));
    }

    @Test
    public void testBuildMissingUnikFolderFailure() throws Exception {
        failureTest(new CreateImageCommand(null, "imageName", "provider", "base", "language", false, false, null, null), "Application location can not be empty");
    }

    @Test
    public void testBuildMissingImageNameFailure() throws Exception {
        failureTest(new CreateImageCommand("folder", null, "provider", "base", "language", false, false, null, null), "Image name can not be empty");
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

package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.RemoveInstanceCommand;
import org.junit.Test;

public class RemoveInstanceCommandTest extends AbstractUnikCommandTest {

    @Test
    public void testBuildSuccess() throws Exception {
        successTest(new RemoveInstanceCommand("instanceName", true));
    }

    @Test
    public void testBuildMissingInstanceNameFailure() throws Exception {
        failureTest(new RemoveInstanceCommand(null, true), "Instance name can not be empty");
    }
}

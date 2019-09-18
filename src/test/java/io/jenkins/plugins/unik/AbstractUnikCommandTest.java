package io.jenkins.plugins.unik;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import io.jenkins.plugins.unik.cmd.UnikCommand;
import io.jenkins.plugins.unik.utils.CompressUtils;
import it.mathiasmah.junik.client.*;
import it.mathiasmah.junik.client.models.Image;
import it.mathiasmah.junik.client.models.Instance;
import it.mathiasmah.junik.client.models.Volume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UnikBuilder.class, CompressUtils.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.crypto.*", "sun.security.ssl.*", "javax.net.ssl.*"})
public abstract class AbstractUnikCommandTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Before
    public void setup() throws Exception {
        //mock unik client
        Client clientMock = PowerMockito.mock(Client.class);
        Instances instancesMock = PowerMockito.mock(Instances.class);
        Images imagesMock = PowerMockito.mock(Images.class);
        Volumes volumesMock = PowerMockito.mock(Volumes.class);
        Compilers compilersMock = PowerMockito.mock(Compilers.class);
        Providers providersMock = PowerMockito.mock(Providers.class);
        Hubs hubMock = PowerMockito.mock(Hubs.class);

        PowerMockito.whenNew(Client.class).withAnyArguments().thenReturn(clientMock);

        Mockito.when(clientMock.instances()).thenReturn(instancesMock);
        Mockito.when(clientMock.images()).thenReturn(imagesMock);
        Mockito.when(clientMock.volumes()).thenReturn(volumesMock);
        Mockito.when(clientMock.compilers()).thenReturn(compilersMock);
        Mockito.when(clientMock.providers()).thenReturn(providersMock);
        Mockito.when(clientMock.hubs()).thenReturn(hubMock);

        Mockito.when(instancesMock.run(any())).thenReturn(new Instance());
        Mockito.when(imagesMock.create(any())).thenReturn(new Image());
        Mockito.when(volumesMock.create(any())).thenReturn(new Volume());
        Mockito.when(compilersMock.getAllAvailable()).thenReturn(new ArrayList<>());
        Mockito.when(providersMock.getAllAvailable()).thenReturn(new ArrayList<>());

        //mock file access
        PowerMockito.mockStatic(CompressUtils.class);
        Mockito.when(CompressUtils.createTarGz(any(), any())).thenReturn("test/path/image.tar.gz");
    }

    void successTest(UnikCommand command) throws Exception {
        successTest(command, "[Unik]");
    }

    void successTest(UnikCommand command, String expectedLog) throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        UnikBuilder builder = new UnikBuilder(command);
        project.getBuildersList().add(builder);

        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains(expectedLog, build);
    }

    void failureTest(UnikCommand command, String expectedLog) throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        UnikBuilder builder = new UnikBuilder(command);
        project.getBuildersList().add(builder);

        FreeStyleBuild build = jenkins.assertBuildStatus(Result.FAILURE, project.scheduleBuild2(0));
        jenkins.assertLogContains(expectedLog, build);
    }

}

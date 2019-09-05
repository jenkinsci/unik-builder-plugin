package io.jenkins.plugins.unik.cmd;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Run;
import hudson.util.FormValidation;
import io.jenkins.plugins.unik.log.ConsoleLogger;
import io.jenkins.plugins.unik.utils.CompressUtils;
import io.jenkins.plugins.unik.utils.Resolver;
import io.jenkins.plugins.unik.validator.ValidatorUtils;
import it.mathiasmah.junik.client.exceptions.UnikException;
import it.mathiasmah.junik.client.models.CreateImage;
import it.mathiasmah.junik.client.models.Image;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * An implementation of {@link UnikCommand} equivalent to the <i>unik build</i> CLI command
 *
 * @see UnikCommand
 */
public class CreateImageCommand extends UnikCommand {

    private String unikFolder;
    private String imageName;
    private String provider;
    private String base;
    private String language;
    private boolean noCleanup;
    private boolean force;
    private String args;
    private String mounts;

    @DataBoundConstructor
    public CreateImageCommand(String unikFolder, String imageName, String provider, String base, String language, boolean noCleanup, boolean force, String args, String mounts) {
        this.unikFolder = unikFolder;
        this.imageName = imageName;
        this.provider = provider;
        this.base = base;
        this.language = language;
        this.noCleanup = noCleanup;
        this.force = force;
        this.args = args;
        this.mounts = mounts;
    }

    public String getUnikFolder() {
        return unikFolder;
    }

    public String getImageName() {
        return imageName;
    }

    public String getProvider() {
        return provider;
    }

    public String getBase() {
        return base;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isNoCleanup() {
        return noCleanup;
    }

    public boolean isForce() {
        return force;
    }

    public String getArgs() {
        return args;
    }

    public String getMounts() {
        return mounts;
    }

    @Override
    public void execute(Launcher launcher, Run<?, ?> run, ConsoleLogger console) throws UnikException {
        console.logInfo("Execute Command: " + getDescriptor().getDisplayName());

        final String unikFolderRes = Resolver.buildVar(run, unikFolder);
        if (StringUtils.isBlank(unikFolderRes)) {
            throw new IllegalArgumentException("Application location can not be empty");
        }

        final String imageNameRes = Resolver.buildVar(run, imageName);
        if (StringUtils.isBlank(imageNameRes)) {
            throw new IllegalArgumentException("Image name can not be empty");
        }

        final String providerRes = Resolver.buildVar(run, provider);
        if (StringUtils.isBlank(providerRes)) {
            throw new IllegalArgumentException("Provider can not be empty");
        }

        final String baseRes = Resolver.buildVar(run, base);
        if (StringUtils.isBlank(baseRes)) {
            throw new IllegalArgumentException("Unikernel base can not be empty");
        }

        final String languageRes = Resolver.buildVar(run, language);
        if (StringUtils.isBlank(languageRes)) {
            throw new IllegalArgumentException("Language can not be empty");
        }

        final String argsRawRes = Resolver.buildVar(run, args);
        final String mountsRawRes = Resolver.buildVar(run, mounts);

        final String tarArchive;
        try {
            console.logInfo("Creating tar archive of " + unikFolderRes);
            tarArchive = CompressUtils.CreateTarGz(unikFolderRes, imageNameRes);
        } catch (IOException e) {
            throw new UnikException("Could not create archive", e);
        }

        CreateImage createImage = new CreateImage();
        createImage.setTarFile(tarArchive);
        createImage.setName(imageNameRes);
        createImage.setProvider(providerRes);
        createImage.setBase(baseRes);
        createImage.setLanguage(languageRes);
        createImage.setNoCleanup(noCleanup);
        createImage.setForce(force);
        if (argsRawRes != null) {
            createImage.setArgs(Arrays.asList(argsRawRes.split("[ |\n]")));
        }
        if (mountsRawRes != null) {
            createImage.setMounts(Arrays.asList(mountsRawRes.split("[ |\n]")));
        }

        final Image image = getClient().images().create(createImage);
        console.logInfo("Created new image");
        console.logInfo(image.toString());

        //clean up
        new File(tarArchive).delete();
    }

    @Extension
    public static class CreateImageCommandDescriptor extends UnikCommandDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.CreateImageCommand_DescriptorImpl_DisplayName();
        }

        @POST
        public FormValidation doCheckUnikFolder(@QueryParameter String unikFolder) {
            return ValidatorUtils.validateStringNotEmpty(unikFolder);
        }

        @POST
        public FormValidation doCheckImageName(@QueryParameter String imageName) {
            return ValidatorUtils.validateStringNotEmpty(imageName);
        }

        @POST
        public FormValidation doCheckProvider(@QueryParameter String provider) {
            return ValidatorUtils.validateStringNotEmpty(provider);
        }

        @POST
        public FormValidation doCheckBase(@QueryParameter String base) {
            return ValidatorUtils.validateStringNotEmpty(base);
        }

        @POST
        public FormValidation doCheckLanguage(@QueryParameter String language) {
            return ValidatorUtils.validateStringNotEmpty(language);
        }
    }
}

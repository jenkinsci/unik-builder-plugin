package io.jenkins.plugins.unik.cmd;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Run;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.unik.log.ConsoleLogger;
import io.jenkins.plugins.unik.utils.CompressUtils;
import io.jenkins.plugins.unik.utils.Resolver;
import io.jenkins.plugins.unik.validator.ValidatorUtils;
import it.mathiasmah.junik.client.exceptions.UnikException;
import it.mathiasmah.junik.client.models.CreateVolume;
import it.mathiasmah.junik.client.models.Volume;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of {@link UnikCommand} equivalent to the <i>unik create-volume</i> CLI command
 *
 * @see UnikCommand
 */
public class CreateVolumeCommand extends UnikCommand {

    private static java.util.logging.Logger LOGGER = Logger.getLogger(CreateVolumeCommand.class.getName());

    private String volumeName;
    private String type;
    private boolean raw;
    private boolean noCleanup;
    private String provider;
    private String size;
    private String data;

    @DataBoundConstructor
    public CreateVolumeCommand(String volumeName, String type, boolean raw, boolean noCleanup, String provider, String size, String data) {
        this.volumeName = volumeName;
        this.type = type;
        this.raw = raw;
        this.noCleanup = noCleanup;
        this.provider = provider;
        this.size = size;
        this.data = data;
    }

    public String getVolumeName() {
        return volumeName;
    }

    public String getType() {
        return type;
    }

    public boolean isRaw() {
        return raw;
    }

    public boolean isNoCleanup() {
        return noCleanup;
    }

    public String getProvider() {
        return provider;
    }

    public String getSize() {
        return size;
    }

    public String getData() {
        return data;
    }

    @Override
    public void execute(Launcher launcher, Run<?, ?> run, ConsoleLogger console) throws UnikException {
        console.logInfo("Execute Command: " + getDescriptor().getDisplayName());

        final String volumeNameRes = Resolver.buildVar(run, volumeName);
        if (StringUtils.isBlank(volumeNameRes)) {
            throw new IllegalArgumentException("Volume name can not be empty");
        }

        final String providerRes = Resolver.buildVar(run, provider);
        if (StringUtils.isBlank(providerRes)) {
            throw new IllegalArgumentException("Provider can not be empty");
        }

        final String sizeRawRes = Resolver.buildVar(run, size);
        int sizeRes;
        try {
            sizeRes = Integer.valueOf(sizeRawRes);
            if (sizeRes <= 0) {
                sizeRes = 0;
                console.logWarn("Not a valid volume size " + sizeRes + ", will be ignored");
            }
        } catch (NumberFormatException e) {
            sizeRes = 0;
            console.logWarn("Not a valid volume size " + sizeRes + ", will be ignored");
        }

        String dataRes = Resolver.buildVar(run, data);

        if (!raw && !StringUtils.isBlank(data)) {
            try {
                console.logInfo("Create tar archive of " + dataRes);
                dataRes = CompressUtils.CreateTarGz(dataRes, volumeName);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Could not create tar archive", e);
                console.logWarn("Could not create tar archive, cause: " + e.getMessage());
                dataRes = "";
            }
        }

        if (StringUtils.isBlank(dataRes) && sizeRes <= 0) {
            throw new IllegalArgumentException("Either a data or a volume size greater 0 must be specified");
        }

        final String typeRes = Resolver.buildVar(run, type);

        final CreateVolume createVolume = new CreateVolume();
        createVolume.setName(volumeNameRes);
        createVolume.setProvider(providerRes);
        createVolume.setRaw(raw);
        createVolume.setNoCleanup(noCleanup);
        createVolume.setSize(sizeRes);
        createVolume.setTarFile(dataRes);
        createVolume.setType(typeRes);

        final Volume volume = getClient().volumes().create(createVolume);
        console.logInfo("Volume " + volumeNameRes + "is created");
        console.logInfo(volume.toString());
    }


    @Extension
    public static class CreateVolumeCommandDescriptor extends UnikCommandDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.CreateVolumeCommand_DescriptorImpl_DisplayName();
        }

        public ListBoxModel doFillTypeItems() {
            return new ListBoxModel().add("ext2").add("FAT");
        }

        public FormValidation doCheckVolumeName(@QueryParameter String volumeName) {
            return ValidatorUtils.validateStringNotEmpty(volumeName);
        }

        public FormValidation doCheckProvider(@QueryParameter String provider) {
            return ValidatorUtils.validateStringNotEmpty(provider);
        }

        public FormValidation doCheckSize(@QueryParameter String size, @QueryParameter String data) {
            FormValidation sizeValidation = ValidatorUtils.validateStringNotEmpty(size);
            FormValidation dataValidation = ValidatorUtils.validateStringNotEmpty(data);
            if (!FormValidation.Kind.OK.equals(sizeValidation.kind) && !FormValidation.Kind.OK.equals(dataValidation.kind)) {
                return FormValidation.error(Messages.CreateVolumeCommand_DescriptorImpl_validateSizeAndData_error());
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckData(@QueryParameter String data, @QueryParameter String size) {
            return doCheckSize(size, data);
        }
    }
}

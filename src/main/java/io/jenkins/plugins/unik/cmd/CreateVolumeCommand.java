package io.jenkins.plugins.unik.cmd;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Item;
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
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

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
    public CreateVolumeCommand(String volumeName, String provider) {
        this.volumeName = volumeName;
        this.provider = provider;
        this.type = StringUtils.EMPTY;
        this.raw = false;
        this.noCleanup = false;
        this.size = "0";
        this.data = StringUtils.EMPTY;
    }

    public String getVolumeName() {
        return volumeName;
    }

    public String getType() {
        return type;
    }

    @DataBoundSetter
    public void setType(String type) {
        this.type = type;
    }

    public boolean isRaw() {
        return raw;
    }

    @DataBoundSetter
    public void setRaw(boolean raw) {
        this.raw = raw;
    }

    public boolean isNoCleanup() {
        return noCleanup;
    }

    @DataBoundSetter
    public void setNoCleanup(boolean noCleanup) {
        this.noCleanup = noCleanup;
    }

    public String getProvider() {
        return provider;
    }

    public String getSize() {
        return size;
    }

    @DataBoundSetter
    public void setSize(String size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    @DataBoundSetter
    public void setData(String data) {
        this.data = data;
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


    @Symbol("create-volume")
    @Extension
    public static class CreateVolumeCommandDescriptor extends UnikCommandDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.CreateVolumeCommand_DescriptorImpl_DisplayName();
        }

        public ListBoxModel doFillTypeItems() {
            return new ListBoxModel().add("ext2").add("FAT");
        }

        @POST
        public FormValidation doCheckVolumeName(@QueryParameter String volumeName, @AncestorInPath Item item) {
            if (item == null) {
                return FormValidation.ok();
            }
            item.checkPermission(Item.CONFIGURE);

            return ValidatorUtils.validateStringNotEmpty(volumeName);
        }

        @POST
        public FormValidation doCheckProvider(@QueryParameter String provider, @AncestorInPath Item item) {
            if (item == null) {
                return FormValidation.ok();
            }
            item.checkPermission(Item.CONFIGURE);

            return ValidatorUtils.validateStringNotEmpty(provider);
        }

        @POST
        public FormValidation doCheckSize(@QueryParameter String size, @QueryParameter String data, @AncestorInPath Item item) {
            if (item == null) {
                return FormValidation.ok();
            }
            item.checkPermission(Item.CONFIGURE);

            FormValidation sizeValidation = ValidatorUtils.validateStringNotEmpty(size);
            FormValidation dataValidation = ValidatorUtils.validateStringNotEmpty(data);
            if (!FormValidation.Kind.OK.equals(sizeValidation.kind) && !FormValidation.Kind.OK.equals(dataValidation.kind)) {
                return FormValidation.error(Messages.CreateVolumeCommand_DescriptorImpl_validateSizeAndData_error());
            }
            return FormValidation.ok();
        }

        @POST
        public FormValidation doCheckData(@QueryParameter String data, @QueryParameter String size, @AncestorInPath Item item) {
            if (item == null) {
                return FormValidation.ok();
            }
            item.checkPermission(Item.CONFIGURE);

            return doCheckSize(size, data, item);
        }
    }
}

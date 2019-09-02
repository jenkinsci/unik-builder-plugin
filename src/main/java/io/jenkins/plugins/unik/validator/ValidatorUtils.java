package io.jenkins.plugins.unik.validator;

import hudson.util.FormValidation;
import io.jenkins.plugins.unik.Messages;
import org.apache.commons.lang.StringUtils;

public class ValidatorUtils {

    public static FormValidation validateStringNotEmpty(String value) {
        if (StringUtils.isBlank(value)) {
            return FormValidation.error(Messages.ValidatorUtils_stringValidation_error());
        }
        return FormValidation.ok();
    }
}

package com.github.nramc.dev.journey.api.web.validation.validator;

import com.github.nramc.dev.journey.api.model.security.Visibility;
import com.github.nramc.dev.journey.api.web.validation.ValidateVisibilities;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Set;

public class ValidateVisibilitiesValidator implements ConstraintValidator<ValidateVisibilities, Set<Visibility>> {

    @Override
    public boolean isValid(Set<Visibility> visibilities, ConstraintValidatorContext context) {
        return CollectionUtils.isNotEmpty(visibilities) && isDefaultVisibilityExists(visibilities);
    }

    private boolean isDefaultVisibilityExists(Set<Visibility> visibilities) {
        return visibilities.containsAll(Set.of(Visibility.MYSELF, Visibility.ADMINISTRATOR));
    }
}

package io.hiwepy.boot.autoconfigure.datascope;

import io.hiwepy.boot.autoconfigure.datascope.annotation.RequiresDataPermissions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.biz.utils.SpringContextUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * 数据权限校验
 */
@Slf4j
public class RequiresDataPermissionsValidator implements ConstraintValidator<RequiresDataPermissions, Object> {

    private DataType dataType;
    private DataScopeProvider provider;

    @Override
    public void initialize(RequiresDataPermissions annotation) {
        this.dataType = annotation.dataType();
        this.provider = SpringContextUtils.getContext().getInstance(DataScopeProvider.class);
    }

    @Override
    public boolean isValid(Object data, ConstraintValidatorContext constraintValidatorContext) {
        // Check if the data has value
        if (Objects.isNull(data)) {
            return Boolean.FALSE;
        }
        // Get the data permission provider
        if (Objects.isNull(provider)) {
            log.warn("DataScopeProvider is not found.");
            return Boolean.FALSE;
        }
        // Check if the data has permissions
        return provider.hasPermissions(dataType, data);
    }

}

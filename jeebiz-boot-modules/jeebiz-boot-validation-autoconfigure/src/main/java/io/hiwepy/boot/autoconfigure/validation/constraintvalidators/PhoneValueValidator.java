package io.hiwepy.boot.autoconfigure.validation.constraintvalidators;


import io.hiwepy.boot.autoconfigure.validation.constraints.PhoneNumber;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 数据校验注解实现类
 * @author hiwepy
 * @since 2021-03-08
 */
public class PhoneValueValidator implements ConstraintValidator<PhoneNumber, String> {

    private static final PhoneNumberUtil PHONE_NUMBER_UTIL = PhoneNumberUtil.getInstance();

    private PhoneNumber phoneValue;

    @Override
    public void initialize(PhoneNumber annotation) {
        this.phoneValue = annotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        Phonenumber.PhoneNumber referencePhonenumber = new Phonenumber.PhoneNumber();
        try {

            referencePhonenumber = PHONE_NUMBER_UTIL.parse(value, phoneValue.lang());
            boolean flag = PHONE_NUMBER_UTIL.isPossibleNumber(referencePhonenumber);
            if (!flag) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(phoneValue.message())
                        .addConstraintViolation();
            }
            return flag;
        } catch (NumberParseException e) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(phoneValue.message())
                    .addConstraintViolation();
            return false;
        }
    }

}

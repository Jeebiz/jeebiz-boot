package io.hiwepy.boot.autoconfigure.validation.constraintvalidators;

import io.hiwepy.boot.autoconfigure.validation.constraints.StringDateValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 字符串日期格式校验器
 * @author hiwepy
 * @since 2021-03-08
 */
public class StringDateValueValidator implements ConstraintValidator<StringDateValue, String> {

    private static Logger logger = LoggerFactory.getLogger(StringDateValueValidator.class);

    private StringDateValue dateValue;

    @Override
    public void initialize(StringDateValue annotation) {
        this.dateValue = annotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        boolean res = false;
        String msg = "";
        String pattern = dateValue.pattern();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        // 设置lenient为false.
        // 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
        simpleDateFormat.setLenient(false);
        try {
            simpleDateFormat.parse(value);
            res = true;
        } catch (ParseException e) {
            logger.error("字符串日期解析出错");
            msg = dateValue.message() + "字符串日期格式出错";
        }
        if (res == false) { // res为false表明有错误提示输出
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
        }
        return res;
    }
}

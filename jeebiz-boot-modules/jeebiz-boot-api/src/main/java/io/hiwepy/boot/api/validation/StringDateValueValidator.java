package io.hiwepy.boot.api.validation;

import io.hiwepy.boot.api.annotation.StringDateValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class StringDateValueValidator implements ConstraintValidator<StringDateValue, String> {

	private static Logger logger = LoggerFactory.getLogger(StringDateValueValidator.class);

	private StringDateValue dateValue;

	@Override
	public void initialize(StringDateValue annotation) {
		this.dateValue = annotation;
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
		if (StringUtils.isBlank(value)) {
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

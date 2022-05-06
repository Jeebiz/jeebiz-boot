package io.hiwepy.boot.autoconfigure.jackson;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 将字符串转为Long
 */
@Slf4j
public class ToLongDeserializer extends JsonDeserializer<Long> {

	@Override
	public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
			throws IOException, JsonProcessingException {
		String value = jsonParser.getText();
		try {
			return StringUtils.hasText(value) ? new BigDecimal(value).longValue() : null;
		} catch (NumberFormatException e) {
			log.error("解析长整形错误", e);
			return null;
		}
	}

}

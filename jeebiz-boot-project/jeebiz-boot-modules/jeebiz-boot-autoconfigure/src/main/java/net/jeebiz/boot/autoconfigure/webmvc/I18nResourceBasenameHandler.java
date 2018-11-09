/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.autoconfigure.webmvc;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.springframework.biz.context.support.ResourceBasenameHandler;
import org.springframework.core.io.Resource;

public class I18nResourceBasenameHandler implements ResourceBasenameHandler {

	@Override
	public String handle(Resource resource) throws IOException {
		
		URL url = resource.getURL();
		
		String filepath = url.getPath();
		return "i18n" + FilenameUtils.getFullPath(filepath).split("i18n")[1] + FilenameUtils.getBaseName(filepath);
	}

}

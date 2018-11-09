/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.jeebiz.boot.datax.setup.config;

import java.util.List;

import org.pf4j.PluginManager;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;  
  
public class DataOutputItemWriter implements ItemWriter<Object> {  

	@Autowired
	private PluginManager pluginManager;
	
	@Override
	public void write(List<? extends Object> items) throws Exception {
		
		
		getPluginManager().getExtensions("");
		
		
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public void setPluginManager(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}  
	
}  
package net.jeebiz.boot.api.webmvc;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseMapperController extends BaseController {

	@Autowired
	private DozerBeanMapper beanMapper; 
	
	public DozerBeanMapper getBeanMapper() {
		return beanMapper;
	}

	public void setBeanMapper(DozerBeanMapper beanMapper) {
		this.beanMapper = beanMapper;
	}
	
}

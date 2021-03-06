

/**
 */
package net.jeebiz.boot.demo.web.mvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.biz.utils.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.jeebiz.boot.api.annotation.BusinessLog;
import net.jeebiz.boot.api.annotation.BusinessType;
import net.jeebiz.boot.api.web.BaseMapperController;
import net.jeebiz.boot.demo.dao.entities.DemoModel;
import net.jeebiz.boot.demo.service.IDemoService;
import net.jeebiz.boot.demo.setup.data.LogConstant;
import net.jeebiz.boot.demo.web.dto.DemoDTO;
import net.jeebiz.boot.demo.web.dto.DemoNewDTO;

@RestController
@RequestMapping("demo")
public class DemoController extends BaseMapperController{
	
	@Autowired
	private IDemoService demoService;
    
	/**
	 * 增加逻辑实现
	 */
	@ApiOperation(value = "创建xxx信息", notes = "根据DemoVo创建xxx", httpMethod = "POST")
	@ApiImplicitParam(name = "demoVo", value = "xxx数据传输对象", required = true, dataType = "DemoNewDTO")
	@BusinessLog(module = LogConstant.Module.N01, business = LogConstant.BUSINESS.N010001, opt = BusinessType.INSERT)
	@PostMapping("new")
	@ResponseBody
	public Object newDemo(@Valid DemoNewDTO dto) {
		try {
			
			DemoModel demoModel = new DemoModel();
			
			//如果自己较少，采用手动设置方式
			demoModel.setName(dto.getName());
			//如果自动较多，采用对象拷贝方式；该方式不支持文件对象拷贝
			//PropertyUtils.copyProperties(demoModel, demoVo);

			getDemoService().insert(demoModel);
			return success("demo.new.success");
		} catch (Exception e) {
			logException(this, e);
			return fail("demo.new.fail");
		}
	}
	
	/**
	 * 修改逻辑实现
	 */
	@ApiOperation(value = "修改xxx信息", notes = "修改xxx", httpMethod = "POST")
	@ApiImplicitParam(name = "demoVo", value = "xxx数据传输对象", required = true, dataType = "DemoVo")
	@BusinessLog(module = LogConstant.Module.N01, business = LogConstant.BUSINESS.N010001, opt = BusinessType.UPDATE)
	@PostMapping("renew")
	@ResponseBody
	public Object renew(@Valid DemoDTO demoVo) throws Exception {
		try {
			 
			DemoModel demoModel = new DemoModel();
			
			//如果自己较少，采用手动设置方式
			demoModel.setName(demoVo.getName());
			//如果自动较多，采用对象拷贝方式；该方式不支持文件对象拷贝
			//PropertyUtils.copyProperties(demoModel, demoVo);
			
			getDemoService().update(demoModel);
			return success("demo.renew.success");
		} catch (Exception e) {
			logException(this, e);
			return fail("demo.renew.fail");
		}
	}

	/**
	 * 删除逻辑实现
	 */
	@ApiOperation(value = "删除xxx信息", notes = "根据ID删除xxx", httpMethod = "POST")
	@ApiImplicitParam(name = "ids", value = "ID集合，多个使用,拼接", required = true, dataType = "String")
	@BusinessLog(module = LogConstant.Module.N01, business = LogConstant.BUSINESS.N010001, opt = BusinessType.DELETE)
	@PostMapping("delete")
	@ResponseBody
	public Object delete(@RequestParam(value = "ids") String ids, HttpServletRequest request) throws Exception {
		try {
			if (StringUtils.isEmpty(ids)) {
				return fail("demo.delete.fail");
			}
			List<String> list = Stream.of(StringUtils.tokenizeToStringArray(ids)).collect(Collectors.toList());
			// 批量删除数据库配置记录
			getDemoService().batchDelete(list);
			return success("demo.delete.success");
		} catch (Exception e) {
			logException(this, e);
			return fail("demo.delete.fail");
		}
	}
	 

	public IDemoService getDemoService() {
		return demoService;
	}

	public void setDemoService(IDemoService demoService) {
		this.demoService = demoService;
	}
	
}

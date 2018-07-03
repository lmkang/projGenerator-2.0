package ${controllerPackage};

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wandaima.annotation.FormScope;
import org.wandaima.util.ClassUtils;
import org.wandaima.util.FormUtils;

import com.ld.model.CommonResult;
import com.ld.model.Pagination;

import ${modelPackage}.${ModelName};
import ${servicePackage}.${ModelName}Service;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/${modelName}")
@Slf4j
public class ${ModelName}Controller {

	@Autowired
	private ${ModelName}Service ${modelName}Service;

	/**
	 * 添加<br/>
	 * 200 : 成功<br/>
	 * 400 : 表单校验不通过<br/>
	 * 500 : 添加失败<br/>
	 * @param ${modelName}
	 * @return
	 */
	@PostMapping("/add")
	public Object add${ModelName}(${ModelName} ${modelName}) {
		// 判断表单数据是否都为空
		if(FormUtils.isEmptyObject(${modelName})) {
			return new CommonResult(400, "表单数据不能全为空!");
		}
		// 校验表单数据
		Map<String, String> errorMap = FormUtils.valid(${modelName}, FormScope.ADD);
		if(errorMap == null) {// 表单校验成功
			int result = 0;
			try {
				result = ${modelName}Service.add${ModelName}(${modelName});
			} catch (Exception e) {
				log.error("添加失败,该条记录可能已经存在.");
				e.printStackTrace();
			}
			if(result != 0) {// 添加成功
				return new CommonResult();
			} else {// 添加失败
				return new CommonResult(500, "该条记录可能已经存在!");
			}
		} else {// 表单校验错误
			return new CommonResult(400, "消息提示", errorMap);
		}
	}

	/**
	 * 修改<br/>
	 * 200 : 成功<br/>
	 * 400 : 表单校验不通过<br/>
	 * 401 : 回显对象不存在<br/>
	 * 500 : 修改失败<br/>
	 * @param ${modelName}
	 * @param request
	 * @return
	 */
	@PostMapping("/edit")
	public Object edit${ModelName}(${ModelName} ${modelName}, HttpServletRequest request) {
		// 从session中获取回显对象
		HttpSession session = request.getSession();
		${ModelName} echo${ModelName} = (${ModelName}) session.getAttribute("echo${ModelName}");
		if(echo${ModelName} != null) {
			// 从session中移除回显对象
			session.removeAttribute("echo${ModelName}");
			// 判断表单数据是否都为空
			if(FormUtils.isEmptyObject(${modelName})) {
				return new CommonResult(400, "表单数据不能全为空!");
			}
			// 校验表单数据
			Map<String, String> errorMap = FormUtils.valid(${modelName}, FormScope.EDIT);
			if(errorMap == null) {// 表单校验通过
				${ModelName} diff${ModelName} = ClassUtils.getDiffObject(echo${ModelName}, ${modelName});
				if(!ClassUtils.isNull(diff${ModelName})) {// 有修改数据
					// 设置id(主键)
					diff${ModelName}.setId(echo${ModelName}.getId());
					int result = 0;
					try {
						result = ${modelName}Service.edit${ModelName}(${modelName});
					} catch (Exception e) {
						log.error("修改失败,该条记录可能已经存在.");
						e.printStackTrace();
					}
					if(result != 0) {// 修改成功
						return new CommonResult();
					} else {// 修改失败
						return new CommonResult(500, "该条记录可能已经存在!");
					}
				} else {// 未修改数据,返回成功
					return new CommonResult();
				}
			} else {// 表单校验错误
				return new CommonResult(400, "消息提示", errorMap);
			}
		} else {// 回显对象不存在
			return new CommonResult(401, "回显对象不存在,请刷新页面重试!");
		}
	}
	
	/**
	 * 获取回显信息<br/>
	 * 200 : 成功<br/>
	 * 400 : 该条记录不存在<br/>
	 * @param id
	 * @param request
	 * @return
	 */
	@GetMapping("/echo")
	public Object getEcho${ModelName}(@RequestParam("id") Long id, HttpServletRequest request) {
		String[] fieldList = {"id", ""};
		${ModelName} echo${ModelName} = ${modelName}Service.get${ModelName}ById(id, fieldList);
		if(echo${ModelName} != null) {// 回显对象存在
			request.getSession().setAttribute("echo${ModelName}", echo${ModelName});
			return new CommonResult(echo${ModelName});
		} else {
			return new CommonResult(400, "该条记录不存在!");
		}
	}
	
	/**
	 * 获取详情信息<br/>
	 * 200 : 成功<br/>
	 * @param id
	 * @param request
	 * @return
	 */
	@GetMapping("/get")
	public Object get${ModelName}(@RequestParam("id") Long id, HttpServletRequest request) {
		String[] fieldList = {"id", ""};
		${ModelName} ${modelName} = ${modelName}Service.get${ModelName}ById(id, fieldList);
		return new CommonResult(${modelName});
	}

	/**
	 * 删除<br/>
	 * 200 : 成功<br/>
	 * 400 : 参数错误<br/>
	 * 500 : 未删除任何数据<br/>
	 * @param ids 逗号分隔的id
	 * @return
	 */
	@GetMapping("/delete")
	public Object deleteByIds(@RequestParam("ids") String ids) {
		if(ids != null) {
			String[] values = ids.split(",");
			if(values != null && values.length > 0) {
				Long[] idArr = new Long[values.length];
				for(int i = 0; i < values.length; i++) {
					if(values[i] != null && values[i].trim().length() > 0) {
						try {
							long id = Long.parseLong(values[i]);
							idArr[i] = id;
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
				}
				int result = ${modelName}Service.deleteByIds(idArr);
				if(result != 0) {
					return new CommonResult();
				} else {// 未删除任何数据
					return new CommonResult(500, "未删除任何数据");
				}
			}
		}
		return new CommonResult(400, "参数传递错误!");
	}
	
	/**
	 * 列表
	 * @param pageNum 分页页码
	 * @return
	 */
	@GetMapping("/list")
	public Object list${ModelName}(@RequestParam(value = "page", defaultValue = "1") Integer pageNum) {
		String[] fieldList = { "id", "" };
		Pagination<${ModelName}> pagination = ${modelName}Service.get${ModelName}Pagination(pageNum, fieldList);
		return new CommonResult(pagination);
	}
}
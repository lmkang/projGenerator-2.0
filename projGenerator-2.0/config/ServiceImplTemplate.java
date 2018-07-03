package ${serviceImplPackage};

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.wandaima.model.Criteria;
import com.ld.model.Pagination;

import ${modelPackage}.${ModelName};
import ${mapperPackage}.${ModelName}Mapper;
import ${servicePackage}.${ModelName}Service;

@Service
public class ${ModelName}ServiceImpl implements ${ModelName}Service {

	@Value("${pagination.pageSize:10}")
	private Integer pageSize;
	
	@Autowired
	private ${ModelName}Mapper ${modelName}Mapper;

	@Transactional
	@Override
	public int add${ModelName}(${ModelName} ${modelName}) {
		if(${modelName} != null) {
			return ${modelName}Mapper.insert(${modelName});
		}
		return 0;
	}

	@Transactional
	@Override
	public int edit${ModelName}(${ModelName} ${modelName}) {
		if(${modelName} != null) {
			return ${modelName}Mapper.update(${modelName}, null);
		}
		return 0;
	}

	@Transactional
	@Override
	public int deleteByIds(Long... ids) {
		int result = 0;
		if(ids != null && ids.length > 0) {
			for(int i = 0; i < ids.length; i++) {
				if(ids[i] != null) {
					Criteria criteria = new Criteria();
					criteria.eq("${pkFieldName}", ids[i]);
					result += ${modelName}Mapper.delete(criteria);
				}
			}
		}
		return result;
	}

	@Override
	public ${ModelName} get${ModelName}ById(Long id, String... fieldList) {
		if(id != null) {
			Criteria criteria = new Criteria();
			criteria.eq("${pkFieldName}", id);
			return ${modelName}Mapper.selectOne(criteria, fieldList);
		}
		return null;
	}
	
	@Override
	public Pagination<${ModelName}> get${ModelName}Pagination(Integer pageNum, String... fieldList) {
		if(pageNum < 1) {
			pageNum = 1;
		}
		Criteria criteria = new Criteria();
		long recordCount = ${modelName}Mapper.countPagination(criteria);
		List<${ModelName}> recordList = ${modelName}Mapper.selectPagination((pageNum - 1) * pageSize, pageSize, criteria, fieldList);
		return new Pagination<${ModelName}>(recordList, recordCount, pageSize, pageNum);
	}

}
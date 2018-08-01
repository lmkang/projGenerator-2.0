package org.wandaima.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.wandaima.util.FileUtils;
import org.wandaima.util.JdbcUtils;
import org.wandaima.util.TypeUtils;
import org.wandaima.util.XmlUtils;

public class CodeGenerator {

	private static Map<String, DbTableInfo> dbTableInfoMap;
	
	static {
		dbTableInfoMap = new HashMap<String, DbTableInfo>();
		initDbTableInfo();
	}
	
	private static void initDbTableInfo() {
		Connection conn = JdbcUtils.getConnection();
		Map<String, XmlTableInfo> xmlTableInfoMap = XmlUtils.getXmlTableInfoMap();
		try {
			DatabaseMetaData metaData = conn.getMetaData();
			String catalog = conn.getCatalog();
			System.out.println("catalog: " + catalog);
			String schema = conn.getSchema();
			System.out.println("schema: " + schema);
			for(Map.Entry<String, XmlTableInfo> me : xmlTableInfoMap.entrySet()) {
				ResultSet tableRs = metaData.getTables(catalog, schema, me.getKey(), new String[] {"TABLE"});
				if(tableRs.next()) {
					XmlTableInfo xmlTableInfo = me.getValue();
					List<XmlFkInfo> fkInfoList = xmlTableInfo.getFkInfoList();
					DbTableInfo dbTableInfo = new DbTableInfo();
					dbTableInfo.setTableName(me.getKey());
					System.out.println("-->tableName: " + me.getKey());
					ResultSet pkRs = metaData.getPrimaryKeys(catalog, schema, me.getKey());
					if(pkRs.next()) {// 只支持单一主键
						String pkColumnName = pkRs.getString("COLUMN_NAME");
						dbTableInfo.setPkName(pkColumnName);
					}
					Map<String, String> columnInfo = new LinkedHashMap<String, String>();
					ResultSet columnRs = metaData.getColumns(catalog, schema, me.getKey(), null);
					while(columnRs.next()) {
						String columnName = columnRs.getString("COLUMN_NAME");
						String columnType = columnRs.getString("TYPE_NAME");
						if(columnName.equals(dbTableInfo.getPkName())) {// 主键
							dbTableInfo.setPkType(columnName);
						}
						if(!isFkColumn(fkInfoList, columnName)) {// 不是外键
							columnInfo.put(columnName, columnType.toUpperCase());
							System.out.println("columnName: " + columnName + ", columnType: " + columnType);
						}
					}
					dbTableInfo.setColumnInfo(columnInfo);
					dbTableInfoMap.put(me.getKey(), dbTableInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.release(conn, null, null);
		}
	}
	
	public void generateModel() {
		Map<String, XmlTableInfo> xmlTableInfoMap = XmlUtils.getXmlTableInfoMap();
		if(xmlTableInfoMap != null && xmlTableInfoMap.size() > 0) {
			Map<String, String> basePackageInfo = XmlUtils.getBasePackageInfo();
			String modelBasePackage = basePackageInfo.get("model.basePackage");
			String modelPath = modelBasePackage.replaceAll("\\.", "/");
			File modelDir = new File(modelPath);
			if(!modelDir.exists()) {
				modelDir.mkdirs();
			}
			for(Map.Entry<String, XmlTableInfo> me : xmlTableInfoMap.entrySet()) {
				DbTableInfo dbTableInfo = dbTableInfoMap.get(me.getKey());
				XmlTableInfo xmlTableInfo = me.getValue();
				String modelName = xmlTableInfo.getModelName();
				Map<String, String> columnInfo = dbTableInfo.getColumnInfo();
				List<XmlFkInfo> fkInfoList = xmlTableInfo.getFkInfoList();
				StringBuilder importBuilder = new StringBuilder();
				StringBuilder codeBuilder = new StringBuilder();
				BufferedWriter bufw = null;
				try {
					bufw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(modelPath + "/" + modelName + ".java"), "utf-8"));
					bufw.write("package " + modelBasePackage + ";\r\n\r\n");
					importBuilder.append("import lombok.EqualsAndHashCode;\r\n");
					importBuilder.append("import lombok.Getter;\r\n");
					importBuilder.append("import lombok.Setter;\r\n");
					importBuilder.append("import org.wandaima.annotation.Table;\r\n");
					codeBuilder.append("@Getter\r\n");
					codeBuilder.append("@Setter\r\n");
					codeBuilder.append("@EqualsAndHashCode\r\n");
					codeBuilder.append(String.format("@Table(name = \"%s\")\r\n", me.getKey()));
					codeBuilder.append("public class " + modelName + " {\r\n\r\n");
					if(columnInfo != null && columnInfo.size() > 0) {
						for(Map.Entry<String, String> cme : columnInfo.entrySet()) {
							String columnName = cme.getKey();
							String columType = cme.getValue();
							String fieldName = getJavaFieldName(columnName);
							String fieldType = TypeUtils.getTypeInfo().get(columType);
							if(TypeUtils.isImport(fieldType)) {
								String str = "import " + fieldType + ";\r\n";
								importBuilder.append(str);
							}
							if(fieldName != null) {
								codeBuilder.append("\tprivate " + TypeUtils.getSimpleTypeName(fieldType) + " " + fieldName + ";\r\n\r\n");
							}
						}
					}
					if(fkInfoList != null && fkInfoList.size() > 0) {
						for(XmlFkInfo xmlFkInfo : fkInfoList) {
							String property = xmlFkInfo.getProperty();
							String javaType = xmlFkInfo.getJavaType();
							String simpleTypeName = TypeUtils.getSimpleTypeName(javaType);
							importBuilder.append("import " + modelBasePackage + "." + simpleTypeName + ";\r\n");
							codeBuilder.append("\tprivate " + simpleTypeName + " " + property + ";\r\n\r\n");
						}
					}
					importBuilder.append("\r\n");
					codeBuilder.append("}");
					bufw.write(importBuilder.toString());
					bufw.write(codeBuilder.toString());
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					if(bufw != null) {
						try {
							bufw.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public void generateMapper() {
		Map<String, XmlTableInfo> xmlTableInfoMap = XmlUtils.getXmlTableInfoMap();
		if(xmlTableInfoMap != null && xmlTableInfoMap.size() > 0) {
			BufferedWriter bufw = null;
			String modelBasePackage = XmlUtils.getBasePackageInfo().get("model.basePackage");
			String mapperBasePackage = XmlUtils.getBasePackageInfo().get("mapper.basePackage");
			String mapperPath = mapperBasePackage.replaceAll("\\.", "/");
			File mapperDir = new File(mapperPath);
			if(!mapperDir.exists()) {
				mapperDir.mkdirs();
			}
			for(Map.Entry<String, XmlTableInfo> me : xmlTableInfoMap.entrySet()) {
				try {
					XmlTableInfo xmlTableInfo = me.getValue();
					String modelName = xmlTableInfo.getModelName();
					String mapperName = modelName + "Mapper";
					bufw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperPath + "/" + mapperName + ".java")));
					StringBuilder builder = new StringBuilder();
					builder.append("package " + mapperBasePackage + ";\r\n\r\n");
					builder.append("import org.apache.ibatis.annotations.Mapper;\r\n");
					builder.append("import org.wandaima.core.BaseMapper;\r\n");
					builder.append("import " + modelBasePackage + "." + modelName + ";\r\n\r\n");
					builder.append("@Mapper\r\n");
					builder.append(String.format("public interface %s extends BaseMapper<%s> {\r\n\r\n", mapperName, modelName));
					builder.append("}");
					bufw.write(builder.toString());
				} catch(Exception e) {
					
				} finally {
					if(bufw != null) {
						try {
							bufw.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public void generateMapperXml() {
		Map<String, XmlTableInfo> xmlTableInfoMap = XmlUtils.getXmlTableInfoMap();
		if(xmlTableInfoMap != null && xmlTableInfoMap.size() > 0) {
			BufferedWriter bufw = null;
			String modelBasePackage = XmlUtils.getBasePackageInfo().get("model.basePackage");
			String mapperBasePackage = XmlUtils.getBasePackageInfo().get("mapper.basePackage");
			String mapperXmlBasePackage = XmlUtils.getBasePackageInfo().get("mapperXml.basePackage");
			String mapperXmlPath = mapperXmlBasePackage.replaceAll("\\.", "/");
			File mapperXmlDir = new File(mapperXmlPath);
			if(!mapperXmlDir.exists()) {
				mapperXmlDir.mkdirs();
			}
			for(Map.Entry<String, XmlTableInfo> me : xmlTableInfoMap.entrySet()) {
				try {
					XmlTableInfo xmlTableInfo = me.getValue();
					String modelName = xmlTableInfo.getModelName();
					List<XmlFkInfo> fkInfoList = xmlTableInfo.getFkInfoList();
					DbTableInfo dbTableInfo = dbTableInfoMap.get(me.getKey());
					String pkName = dbTableInfo.getPkName();
					Map<String, String> columnInfo = dbTableInfo.getColumnInfo();
					String mapperName = modelName + "Mapper";
					bufw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperXmlPath + "/" + mapperName + ".xml")));
					StringBuilder builder = new StringBuilder();
					builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
					builder.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\r\n");
					builder.append(String.format("<mapper namespace=\"%s\">\r\n", mapperBasePackage + "." + mapperName));
					builder.append(String.format("\t<resultMap id=\"BaseResultMap\" type=\"%s\">\r\n", modelBasePackage + "." + modelName));
					builder.append(String.format("\t\t<id column=\"%s\" property=\"%s\"/>\r\n", pkName, getJavaFieldName(pkName)));
					if(columnInfo != null && columnInfo.size() > 0) {// 普通字段
						for(Map.Entry<String, String> cme : columnInfo.entrySet()) {
							String columnName = cme.getKey();
							if(!pkName.equals(columnName)) {// 其他字段
								builder.append(String.format("\t\t<result column=\"%s\" property=\"%s\"/>\r\n", columnName, getJavaFieldName(columnName)));
							}
						}
					}
					if(fkInfoList != null && fkInfoList.size() > 0) {
						int index = 0;
						for(XmlFkInfo xmlFkInfo : fkInfoList) {
							String property = xmlFkInfo.getProperty();
							String column = xmlFkInfo.getColumn();
							String javaType = xmlFkInfo.getJavaType();
							builder.append(String.format("\t\t<association column=\"%s\" property=\"%s\" javaType=\"%s\" columnPrefix=\"fk%s_\">\r\n", column, property, modelBasePackage + "." + TypeUtils.getSimpleTypeName(javaType), index));
							DbTableInfo fkDbTableInfo = getDbTableInfo(xmlTableInfoMap, TypeUtils.getSimpleTypeName(javaType));
							String fkPkName = fkDbTableInfo.getPkName();
							Map<String, String> fkColumnInfo = fkDbTableInfo.getColumnInfo();
							builder.append(String.format("\t\t\t<id column=\"%s\" property=\"%s\"/>\r\n", fkPkName, getJavaFieldName(fkPkName)));
							if(fkColumnInfo != null && fkColumnInfo.size() > 0) {
								for(Map.Entry<String, String> fkme : fkColumnInfo.entrySet()) {
									String fkColumnName = fkme.getKey();
									if(!fkPkName.equals(fkColumnName)) {// 非主键
										builder.append(String.format("\t\t\t<result column=\"%s\" property=\"%s\"/>\r\n", fkColumnName, getJavaFieldName(fkColumnName)));
									}
								}
							}
							builder.append("\t\t</association>\r\n");
							++index;
						}
					}
					builder.append("\t</resultMap>\r\n");
					builder.append("</mapper>");
					bufw.write(builder.toString());
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					if(bufw != null) {
						try {
							bufw.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public void generateService() {
		Map<String, XmlTableInfo> xmlTableInfoMap = XmlUtils.getXmlTableInfoMap();
		if(xmlTableInfoMap != null && xmlTableInfoMap.size() > 0) {
			BufferedWriter bufw = null;
			String modelBasePackage = XmlUtils.getBasePackageInfo().get("model.basePackage");
			String serviceBasePackage = XmlUtils.getBasePackageInfo().get("service.basePackage");
			String servicePath = serviceBasePackage.replaceAll("\\.", "/");
			File serviceDir = new File(servicePath);
			if(!serviceDir.exists()) {
				serviceDir.mkdirs();
			}
			for(Map.Entry<String, XmlTableInfo> me : xmlTableInfoMap.entrySet()) {
				try {
					XmlTableInfo xmlTableInfo = me.getValue();
					String modelName = xmlTableInfo.getModelName();
					String serviceName = modelName + "Service";
					bufw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(servicePath + "/" + serviceName + ".java")));
					StringBuilder builder = new StringBuilder();
					builder.append("package " + serviceBasePackage + ";\r\n\r\n");
					builder.append(String.format("import %s.%s;\r\n", modelBasePackage, modelName));
					builder.append("import com.ld.model.Pagination;\r\n\r\n");
					builder.append(String.format("public interface %s {\r\n\r\n", serviceName));
					builder.append(String.format("\tint add%s(%s %s);\r\n\r\n", modelName, modelName, getLowerFieldName(modelName)));
					builder.append(String.format("\tint edit%s(%s %s);\r\n\r\n", modelName, modelName, getLowerFieldName(modelName)));
					builder.append("\tint deleteByIds(Long... ids);\r\n\r\n");
					builder.append(String.format("\t%s get%sById(Long id, String... fieldList);\r\n\r\n", modelName, modelName));
					builder.append(String.format("\tPagination<%s> get%sPagination(Integer pageNum, String... fieldList);\r\n\r\n", modelName, modelName));
					builder.append("}");
					bufw.write(builder.toString());
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					if(bufw != null) {
						try {
							bufw.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public void generateServiceImpl() {
		Map<String, XmlTableInfo> xmlTableInfoMap = XmlUtils.getXmlTableInfoMap();
		if(xmlTableInfoMap != null && xmlTableInfoMap.size() > 0) {
			BufferedWriter bufw = null;
			String modelBasePackage = XmlUtils.getBasePackageInfo().get("model.basePackage");
			String mapperBasePackage = XmlUtils.getBasePackageInfo().get("mapper.basePackage");
			String serviceBasePackage = XmlUtils.getBasePackageInfo().get("service.basePackage");
			String serviceImplPath = (serviceBasePackage + ".impl").replaceAll("\\.", "/");
			File serviceImplDir = new File(serviceImplPath);
			if(!serviceImplDir.exists()) {
				serviceImplDir.mkdirs();
			}
			for(Map.Entry<String, XmlTableInfo> me : xmlTableInfoMap.entrySet()) {
				try {
					XmlTableInfo xmlTableInfo = me.getValue();
					String modelName = xmlTableInfo.getModelName();
					DbTableInfo dbTableInfo = getDbTableInfo(xmlTableInfoMap, modelName);
					String pkFieldName = getJavaFieldName(dbTableInfo.getPkName());
					String serviceImplName = modelName + "ServiceImpl";
					bufw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(serviceImplPath + "/" + serviceImplName + ".java")));
					String template = FileUtils.getServiceImplTemplate();
					template = template.replaceAll("\\$\\{modelPackage\\}", modelBasePackage)
							.replaceAll("\\$\\{mapperPackage\\}", mapperBasePackage)
							.replaceAll("\\$\\{servicePackage\\}", serviceBasePackage)
							.replaceAll("\\$\\{serviceImplPackage\\}", serviceBasePackage + ".impl")
							.replaceAll("\\$\\{ModelName\\}", modelName)
							.replaceAll("\\$\\{modelName\\}", getLowerFieldName(modelName))
							.replaceAll("\\$\\{pkFieldName\\}", pkFieldName);
					bufw.write(template);
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					if(bufw != null) {
						try {
							bufw.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public void generateController() {
		Map<String, XmlTableInfo> xmlTableInfoMap = XmlUtils.getXmlTableInfoMap();
		if(xmlTableInfoMap != null && xmlTableInfoMap.size() > 0) {
			BufferedWriter bufw = null;
			String modelBasePackage = XmlUtils.getBasePackageInfo().get("model.basePackage");
			String serviceBasePackage = XmlUtils.getBasePackageInfo().get("service.basePackage");
			String controllerBasePackage = XmlUtils.getBasePackageInfo().get("controller.basePackage");
			String controllerPath = controllerBasePackage.replaceAll("\\.", "/");
			File controllerDir = new File(controllerPath);
			if(!controllerDir.exists()) {
				controllerDir.mkdirs();
			}
			for(Map.Entry<String, XmlTableInfo> me : xmlTableInfoMap.entrySet()) {
				try {
					XmlTableInfo xmlTableInfo = me.getValue();
					String modelName = xmlTableInfo.getModelName();
					String controllerName = modelName + "Controller";
					bufw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(controllerPath + "/" + controllerName + ".java")));
					String template = FileUtils.getControllerTemplate();
					template = template.replaceAll("\\$\\{modelPackage\\}", modelBasePackage)
						.replaceAll("\\$\\{controllerPackage\\}", controllerBasePackage)
						.replaceAll("\\$\\{servicePackage\\}", serviceBasePackage)
						.replaceAll("\\$\\{ModelName\\}", modelName)
						.replaceAll("\\$\\{modelName\\}", getLowerFieldName(modelName));
					bufw.write(template);
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					if(bufw != null) {
						try {
							bufw.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public void generateCommonResult() {
		String modelBasePackage = XmlUtils.getBasePackageInfo().get("model.basePackage");
		String filePath = modelBasePackage.replaceAll("\\.", "/");
		File file = new File(filePath);
		if(!file.exists()) {
			file.mkdirs();
		}
		BufferedWriter bufw = null;
		try {
			bufw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath + "/CommonResult.java")));
			String commonResult = FileUtils.getCommonResult();
			bufw.write(commonResult);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(bufw != null) {
				try {
					bufw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void generatePagination() {
		String modelBasePackage = XmlUtils.getBasePackageInfo().get("model.basePackage");
		String filePath = modelBasePackage.replaceAll("\\.", "/");
		File file = new File(filePath);
		if(!file.exists()) {
			file.mkdirs();
		}
		BufferedWriter bufw = null;
		try {
			bufw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath + "/Pagination.java")));
			String pagination = FileUtils.getPagination();
			bufw.write(pagination);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(bufw != null) {
				try {
					bufw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static String getJavaFieldName(String columnName) {
		if(columnName != null && columnName.trim().length() > 0) {
			String[] values = columnName.trim().split("_");
			if(values != null && values.length > 0) {
				StringBuilder builder = new StringBuilder(values[0]);
				for(int i = 1; i < values.length; i++) {
					char ch = values[i].charAt(0);
					builder.append((ch + "").toUpperCase());
					builder.append(values[i].substring(1, values[i].length()));
				}
				return builder.toString();
			}
		}
		return null;
	}
	
	private static String getLowerFieldName(String className) {
		if(className != null && className.trim().length() > 0) {
			className = className.trim();
			char ch = className.charAt(0);
			return (ch + "").toLowerCase() + className.substring(1);
		}
		return null;
	}
	
	private static boolean isFkColumn(List<XmlFkInfo> fkInfoList, String columnName) {
		boolean result = false;
		if(fkInfoList != null && fkInfoList.size() > 0) {
			for(XmlFkInfo xmlFkInfo : fkInfoList) {
				String name = xmlFkInfo.getColumn();
				if(name != null && name.equals(columnName)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
	
	private static DbTableInfo getDbTableInfo(Map<String, XmlTableInfo> xmlTableInfoMap, String modelName) {
		DbTableInfo result = null;
		if(xmlTableInfoMap != null && xmlTableInfoMap.size() > 0) {
			for(Map.Entry<String, XmlTableInfo> me : xmlTableInfoMap.entrySet()) {
				XmlTableInfo xmlTableInfo = me.getValue();
				if(xmlTableInfo.getModelName().equals(modelName)) {
					result =  dbTableInfoMap.get(me.getKey());
					break;
				}
			}
		}
		return result;
	}
	
}

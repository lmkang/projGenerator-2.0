package org.wandaima.util;

import java.util.HashMap;
import java.util.Map;

public class TypeUtils {

	private static Map<String, String> typeInfo = new HashMap<String, String>();
	
	static {
		typeInfo.put("INT", "Integer");
		typeInfo.put("BIGINT", "Long");
		typeInfo.put("FLOAT", "Float");
		typeInfo.put("DOUBLE", "Double");
		typeInfo.put("DECIMAL", "java.math.BigDecimal");
		typeInfo.put("CHAR", "String");
		typeInfo.put("VARCHAR", "String");
		typeInfo.put("TEXT", "String");
		typeInfo.put("LONGTEXT", "String");
		typeInfo.put("DATE", "java.util.Date");
		typeInfo.put("DATETIME", "java.util.Date");
		typeInfo.put("TIME", "java.util.Date");
		typeInfo.put("TIMESTAMP", "java.util.Date");
	}
	
	public static Map<String, String> getTypeInfo() {
		return typeInfo;
	}
	
	public static boolean isImport(String type) {
		boolean result = true;
		if("Integer".equals(type)) {
			result = false;
		} else if("Long".equals(type)) {
			result = false;
		} else if("Float".equals(type)) {
			result = false;
		} else if("Double".equals(type)) {
			result = false;
		} else if("String".equals(type)) {
			result = false;
		}
		return result;
	}
	
	public static String getSimpleTypeName(String javaTypeName) {
		if(javaTypeName != null && javaTypeName.trim().length() > 0) {
			int index = javaTypeName.lastIndexOf(".");
			if(index != -1) {
				return javaTypeName.substring(index + 1);
			} else {
				return javaTypeName;
			}
		}
		return null;
	}
	
}

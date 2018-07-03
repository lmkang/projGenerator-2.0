package org.wandaima.core;

import java.util.Map;

public class DbTableInfo {

	private String tableName;
	private String pkName;
	private String pkType;
	private Map<String, String> columnInfo;
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getPkName() {
		return pkName;
	}
	public void setPkName(String pkName) {
		this.pkName = pkName;
	}
	public String getPkType() {
		return pkType;
	}
	public void setPkType(String pkType) {
		this.pkType = pkType;
	}
	public Map<String, String> getColumnInfo() {
		return columnInfo;
	}
	public void setColumnInfo(Map<String, String> columnInfo) {
		this.columnInfo = columnInfo;
	}
	
}

package org.wandaima.core;

import java.util.List;

public class XmlTableInfo {

	private String tableName;
	private String modelName;
	private List<XmlFkInfo> fkInfoList;
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public List<XmlFkInfo> getFkInfoList() {
		return fkInfoList;
	}
	public void setFkInfoList(List<XmlFkInfo> fkInfoList) {
		this.fkInfoList = fkInfoList;
	}
	
}

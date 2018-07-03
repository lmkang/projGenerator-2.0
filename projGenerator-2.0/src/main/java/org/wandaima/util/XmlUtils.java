package org.wandaima.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.wandaima.core.XmlFkInfo;
import org.wandaima.core.XmlTableInfo;

public class XmlUtils {

	private static Map<String, String> databaseInfo = new HashMap<String, String>();
	
	private static Map<String, String> basePackageInfo = new HashMap<String, String>();
	
	private static Map<String, XmlTableInfo> xmlTableInfoMap = new HashMap<String, XmlTableInfo>();
	
	static {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read("config/config.xml");
			parseDatabaseInfo(document);
			parseBasePackageInfo(document);
			parseTableInfo(document);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public static void parseDatabaseInfo(Document document) {
		Element urlEle = (Element) document.selectSingleNode("//property[@name='database.url']");
		Element driverClassNameEle = (Element) document.selectSingleNode("//property[@name='database.driverClassName']");
		Element usernameEle = (Element) document.selectSingleNode("//property[@name='database.username']");
		Element passwordEle = (Element) document.selectSingleNode("//property[@name='database.password']");
		databaseInfo.put(urlEle.attributeValue("name"), urlEle.attributeValue("value"));
		databaseInfo.put(driverClassNameEle.attributeValue("name"), driverClassNameEle.attributeValue("value"));
		databaseInfo.put(usernameEle.attributeValue("name"), usernameEle.attributeValue("value"));
		databaseInfo.put(passwordEle.attributeValue("name"), passwordEle.attributeValue("value"));
	}
	
	public static void parseBasePackageInfo(Document document) {
		Element modelEle = (Element) document.selectSingleNode("//property[@name='model.basePackage']");
		Element mapperEle = (Element) document.selectSingleNode("//property[@name='mapper.basePackage']");
		Element xmlEle = (Element) document.selectSingleNode("//property[@name='mapperXml.basePackage']");
		Element serviceEle = (Element) document.selectSingleNode("//property[@name='service.basePackage']");
		Element controllerEle = (Element) document.selectSingleNode("//property[@name='controller.basePackage']");
		basePackageInfo.put(modelEle.attributeValue("name"), modelEle.attributeValue("value"));
		basePackageInfo.put(mapperEle.attributeValue("name"), mapperEle.attributeValue("value"));
		basePackageInfo.put(xmlEle.attributeValue("name"), xmlEle.attributeValue("value"));
		basePackageInfo.put(serviceEle.attributeValue("name"), serviceEle.attributeValue("value"));
		basePackageInfo.put(controllerEle.attributeValue("name"), controllerEle.attributeValue("value"));
	}

	public static void parseTableInfo(Document document) {
		List<?> nodeList = document.selectNodes("//table");
		if(nodeList != null && nodeList.size() > 0) {
			for(Object node : nodeList) {
				Element ele = (Element) node;
				XmlTableInfo xmlTableInfo = new XmlTableInfo();
				String tableName = ele.attributeValue("tableName");
				xmlTableInfo.setTableName(tableName);
				xmlTableInfo.setModelName(ele.attributeValue("modelName"));
				List<?> elements = ele.elements();
				if(elements != null && elements.size() > 0) {
					List<XmlFkInfo> fkInfoList = new ArrayList<XmlFkInfo>(elements.size());
					for(Object obj : elements) {
						Element e = (Element) obj;
						XmlFkInfo xmlFkInfo = new XmlFkInfo();
						xmlFkInfo.setColumn(e.attributeValue("column"));
						xmlFkInfo.setProperty(e.attributeValue("property"));
						xmlFkInfo.setJavaType(e.attributeValue("javaType"));
						fkInfoList.add(xmlFkInfo);
					}
					xmlTableInfo.setFkInfoList(fkInfoList);
				}
				xmlTableInfoMap.put(tableName, xmlTableInfo);
			}
		}
	}
	
	public static Map<String, String> getDatabaseInfo() {
		return databaseInfo;
	}
	
	public static Map<String, String> getBasePackageInfo() {
		return basePackageInfo;
	}
	
	public static Map<String, XmlTableInfo> getXmlTableInfoMap() {
		return xmlTableInfoMap;
	}
	
}

package org.wandaima.main;

import org.wandaima.core.CodeGenerator;

public class MainFrame {
	
	public static void main(String[] args) {
		CodeGenerator codeGenerator = new CodeGenerator();
		codeGenerator.generateModel();// Model
		codeGenerator.generateMapper();// Mapper
		codeGenerator.generateMapperXml();// Mapper XML
		codeGenerator.generateService();// Service
		codeGenerator.generateServiceImpl();// ServiceImpl
		codeGenerator.generateController();// Controller
		codeGenerator.generateCommonResult();// CommonResult
		codeGenerator.generatePagination();// Pagination
	}
	
}

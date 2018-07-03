package org.wandaima.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtils {
	
	public static String getControllerTemplate() {
		StringBuilder builder = new StringBuilder();
		BufferedReader bufr = null;
		try {
			bufr = new BufferedReader(new InputStreamReader(new FileInputStream("config/ControllerTemplate.java"), "UTF-8"));
			String line = null;
			while((line = bufr.readLine()) != null) {
				builder.append(line + "\r\n");
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(bufr != null) {
				try {
					bufr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return builder.toString();
	}
	
	public static String getServiceImplTemplate() {
		StringBuilder builder = new StringBuilder();
		BufferedReader bufr = null;
		try {
			bufr = new BufferedReader(new InputStreamReader(new FileInputStream("config/ServiceImplTemplate.java"), "UTF-8"));
			String line = null;
			while((line = bufr.readLine()) != null) {
				builder.append(line + "\r\n");
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(bufr != null) {
				try {
					bufr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return builder.toString();
	}
	
	public static String getCommonResult() {
		StringBuilder builder = new StringBuilder();
		BufferedReader bufr = null;
		try {
			bufr = new BufferedReader(new InputStreamReader(new FileInputStream("config/CommonResult.java"), "UTF-8"));
			String line = null;
			while((line = bufr.readLine()) != null) {
				builder.append(line + "\r\n");
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(bufr != null) {
				try {
					bufr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return builder.toString();
	}

	public static String getPagination() {
		StringBuilder builder = new StringBuilder();
		BufferedReader bufr = null;
		try {
			bufr = new BufferedReader(new InputStreamReader(new FileInputStream("config/Pagination.java"), "UTF-8"));
			String line = null;
			while((line = bufr.readLine()) != null) {
				builder.append(line + "\r\n");
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(bufr != null) {
				try {
					bufr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return builder.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(getControllerTemplate().toString());
	}
}

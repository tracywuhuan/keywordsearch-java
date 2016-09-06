package com.ibm.copyright;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;


public class configReader {
	private ArrayList<String> filetype = new ArrayList<>();
	private ArrayList<String> keywords = new ArrayList<>();
	private ArrayList<String> scanDir = new ArrayList<>();
	private ArrayList<String> logDir = new ArrayList<>();
	private ArrayList<String> exclude = new ArrayList<>();

	public configReader(String configPath) {
		LineNumberReader lineReader = null;
		try {
			lineReader = new LineNumberReader(new FileReader(configPath));
			String readLine = null;
			int flag = 0;
			while ((readLine = lineReader.readLine()) != null) {
				if (readLine.indexOf("[exclude]") != -1) {
					flag = -2;
					continue;
				}
				if (readLine.indexOf("[scanDir]") != -1) {
					flag = -1;
					continue;
				}
				if (readLine.indexOf("[logDir]") != -1) {
					flag = 0;
					continue;
				}
				if (readLine.indexOf("[fileType]") != -1) {
					flag = 1;
					continue;
				}
				if (readLine.indexOf("[keyWord]") != -1) {
					flag = 2;
					continue;
				}
				if (readLine.indexOf("[end]") != -1)
					break;
				if (flag == -2)
					exclude.add(readLine);
				if (flag == -1)
					scanDir.add(readLine);
				if (flag == 0)
					logDir.add(readLine);
				if (flag == 1)
					filetype.add(readLine);
				if (flag == 2)
					keywords.add(readLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}

	}

	public ArrayList<String> getfileType() {
		return filetype;
	}

	public ArrayList<String> getkeywords() {
		return keywords;
	}

	public ArrayList<String> getscanDir() {
		return scanDir;
	}

	public ArrayList<String> getlogDir() {
		return logDir;
	}
	
	public ArrayList<String> getexclude() {
		return exclude;
	}
}

package com.ibm.copyright;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

public class searchFile {

	private String logPath;
	private File logfile;
	private FileWriter writer;
	private ArrayList<String> exclude;
	private ArrayList<String> keywords;
	private int searchedfilesnum = 0;
	private int matchedfilesnum = 0;

	private ArrayList<String> matchedfiles;
	private ArrayList<String> unmatchedfiles;

	/**
	 * 功能：在指定的文件或目录中搜索关键字
	 * 
	 * @param file
	 *            指定的路径的文件或目录对象
	 * @param fileFormat
	 *            以分号(;)连接的文件格式字符串
	 * @throws IOException
	 */
	public searchFile(String log, ArrayList<String> e, ArrayList<String> k)
			throws IOException {
		logPath = log;
		exclude = e;
		keywords = k;
		logfile = new File(logPath);
		if (!logfile.exists()) {
			logfile.createNewFile();
		} else {
			FileWriter temp = new FileWriter(logfile);
			temp.write("");
			temp.close();
		}
		writer = new FileWriter(logfile, true);
		matchedfiles = new ArrayList<String>();
		unmatchedfiles = new ArrayList<String>();
	}

	private boolean matchdir(String path) {
		for (String tempString : exclude) {
			if (path.contains(tempString))
				return true;
		}
		return false;
	}

	public void search(File file) throws FileNotFoundException {
		if (file.isFile()) {
			SearchKeyword(file);
		} else {
			if (matchdir(file.getAbsolutePath()))
				return;
			File[] files = file.listFiles();
			for (File f : files) {
				if (f.isFile()) {
					SearchKeyword(f);
				} else {
					search(f);
				}
			}
		}
	}

	public void search(File file, ArrayList<String> filetype)
			throws FileNotFoundException {
		if (file.isFile()) {
			String s = file.getName();
			String extnameString = FilenameUtils.getExtension(s);
			if (filetype.contains(extnameString.toLowerCase()))
				SearchKeyword(file);
		} else {
			if (matchdir(file.getAbsolutePath()))
				return;
			File[] files = file.listFiles();
			for (File f : files) {
				if (f.isFile()) {
					String s = f.getName();
					String extnameString = FilenameUtils.getExtension(s);
					if (filetype.contains(extnameString.toLowerCase()))
						SearchKeyword(f);
				} else {
					search(f, filetype);
				}
			}
		}
	}

	private void SearchKeyword(File file) throws FileNotFoundException {
		// 二进制文件直接跳过
		if (isBinary(file)) {
			searchedfilesnum++;
			return;
		}
		// 行读取
		LineNumberReader lineReader = null;
		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		// ArrayList<Integer> linenums = new ArrayList<Integer>();
		// ArrayList<String> buffer = new ArrayList<String>();
		int times = 0;// 出现的次数
		searchedfilesnum++;
		try {
			lineReader = new LineNumberReader(new FileReader(file));
			String readLine = null;
			while ((readLine = lineReader.readLine()) != null) {

				for (String keyword : keywords) {
					// 判断次数
					ArrayList<String> value = new ArrayList<String>();
					if (readLine.indexOf(keyword) != -1) {
						value.add(Integer.toString(lineReader.getLineNumber()));
						value.add(readLine);
						// linenums.add(lineReader.getLineNumber());
						// buffer.add(readLine);
						times++;
					}
					if (!value.isEmpty()) {
						if (map.containsKey(keyword)) {
							ArrayList<String> t = map.get(keyword);
							t.addAll(value);
							map.put(keyword, t);
						} else {
							map.put(keyword, value);
						}
					}
				}
			}

			if (times > 0) {
				matchedfilesnum++;
				matchedfiles.add(file.getAbsolutePath());
				writer.write("**********************************************************************\r\n");
				writer.write(file.getAbsolutePath() + "\r\n");
				for (Map.Entry<String, ArrayList<String>> entry : map
						.entrySet()) {
					writer.write("keyword:" + entry.getKey() + "\r\n\r\n");
					ArrayList<String> temp = entry.getValue();
					for (int i = 0; i < temp.size(); i++) {
						if (i % 2 == 0) {
							writer.write("Line:" + temp.get(i) + "\r\n");
						} else {
							writer.write(temp.get(i) + "\r\n");
						}
					}
				}

				writer.write("**********************************************************************\r\n\r\n");
			} else {
				unmatchedfiles.add(file.getAbsolutePath());
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭流
			close(lineReader);
		}
	}

	public void writesummary() throws IOException {
		writer.write("Summary=======================================================================================\r\n");
		int unmatched = searchedfilesnum - matchedfilesnum;
		writer.write(searchedfilesnum + "files checked, " + matchedfilesnum
				+ " files matched, " + unmatched + " files unmatched\r\n");
		if (matchedfilesnum != 0) {
			writer.write("#################################\r\n");
			writer.write("The list of matched files:\r\n");
			for (String temp : matchedfiles) {
				writer.write(temp + "\r\n");
			}
		}
		if (searchedfilesnum != matchedfilesnum) {
			writer.write("#################################\r\n");
			writer.write("The list of unmatched files:\r\n");
			for (String temp : unmatchedfiles) {
				writer.write(temp + "\r\n");
			}
		}
	}

	/**
	 * 关闭流 <br>
	 * Date: 2014年11月5日
	 */
	private void close(Closeable able) {
		if (able != null) {
			try {
				able.close();
			} catch (IOException e) {
				e.printStackTrace();
				able = null;
			}
		}
	}

	public void closewriter() throws IOException {
		if (writer != null) {
			writer.close();
		}
	}

	private boolean isBinary(File file) {
		boolean isBinary = false;
		try {
			FileInputStream fin = new FileInputStream(file);
			long len = file.length();
			for (int i = 0; i < len; i++) {
				int t = fin.read();
				if (t < 32 && t != 9 && t != 10 && t != 13) {
					isBinary = true;
					break;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return isBinary;
	}

}

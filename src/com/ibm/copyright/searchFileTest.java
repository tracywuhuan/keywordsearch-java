package com.ibm.copyright;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class searchFileTest {
	public static boolean isFind = false;
	private static ArrayList<String> filetype;
	private static ArrayList<String> keywords;

	private static ArrayList<String> filePath;
	private static ArrayList<String> logPath;
	private static ArrayList<String> exclude;

	public static void main(String[] args) throws IOException {
		File f = new File(args[0]);
		if (!f.exists()) {
			System.out.println("config.ini not found!");
			return;
		}
		configReader cr = new configReader(args[0]);
		filetype = cr.getfileType();
		keywords = cr.getkeywords();
		filePath = cr.getscanDir();
		logPath = cr.getlogDir();
		exclude = cr.getexclude();
		searchFile sf = new searchFile(logPath.get(0),exclude,keywords);
		for (String filepath : filePath) {

			if (filetype.get(0).equals("*")) {
				File file = new File(filepath);
				if (!file.exists()) {
					System.out.println("scandir not found!");
					return;
				}
				try {
						sf.search(file);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					System.out.println(e.toString());
				}
			} else {
				File file = new File(filepath);
				if (!file.exists()) {
					System.out.println("scandir not found!");
					return;
				}
				try {
						sf.search(file, filetype);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					System.out.println(e.toString());
				}
			}
		}
		sf.writesummary();
		sf.closewriter();
	}

}

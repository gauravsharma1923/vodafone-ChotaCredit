package chotacredit;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;

/**
 * @author Himanshu Sharma
 *
 */
public class LoggerClass {

	public static void logFile(Calendar objLogCalTime, String strCircleName, String filename, String logdata) {
		FileWriter out = null;
		try {
			String strFileName = "/home/ussdaps/ussd_log/ChotaCreditIdea/" + objLogCalTime.get(Calendar.YEAR) + "-"
					+ (objLogCalTime.get(Calendar.MONTH) + 1) + "-" + objLogCalTime.get(Calendar.DAY_OF_MONTH) + "/"
					+ strCircleName;
			String strLogCalTime = objLogCalTime.get(Calendar.YEAR) + "-" + (objLogCalTime.get(Calendar.MONTH) + 1)
					+ "-" + objLogCalTime.get(Calendar.DAY_OF_MONTH) + " " + objLogCalTime.get(Calendar.HOUR_OF_DAY)
					+ ":" + objLogCalTime.get(Calendar.MINUTE) + ":" + objLogCalTime.get(Calendar.SECOND) + ":"
					+ objLogCalTime.get(Calendar.MILLISECOND) + ",";

			createDateDir(strFileName);
			strFileName = strFileName + "/" + filename;
			out = new FileWriter(strFileName, true);
			out.write(strLogCalTime + logdata + "\n");

		} catch (Exception e) {
			System.out.println("error in logging" + e.getMessage());
		} finally {
			try {
				out.close();
			} catch (Exception ex) {
				System.out.println("error in obj closing--- " + ex.getMessage());
			}
		}
	}

	private synchronized static void createDateDir(String Dir) {
		try {
			String folderName = Dir;
			new File(folderName).mkdirs();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

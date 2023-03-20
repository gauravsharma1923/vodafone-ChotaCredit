package chotacredit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Himanshu Sharma
 *
 */
public class ChotaCredit extends HttpServlet {

	static HashMap<String, String> map = new HashMap<String, String>();

	public void readConfiguration() {
		try {
			ServletContext cntxt = getServletContext();
			InputStream ins = cntxt.getResourceAsStream("/WEB-INF/Config.txt");
			String line;
			String[] strArr = new String[2];
			if (ins == null) {
				System.out.println("Could not read Properties file");
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader(ins));
				while ((line = br.readLine()) != null) {

					strArr = line.split("=");
					map.put(strArr[0], strArr[1]);
				}
				br.close();
			}
		} catch (Exception ex) {
			System.out.println("Could not read Properties file");
			ex.printStackTrace();
		}
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String strMsisdn = "";
		String strCircleId = "";
		String strSessionId = "";
		String strServiceId = "";
		Calendar objLogCalTimeReq = null;
		Calendar objLogCalTimeResp = null;
		String strLogData = "";
		PrintWriter out = null;
		String Response = "";
		String strURL = "";
		String strData = "";
		String strSO_TimeOut = "";
		String strCON_TimeOut = "";
		String strCircleName = "";
		String strIP = "";
		String strPort = "";
		String eaiCircle = "";
		String[] strArr;
		String requestType = "";
		String brand = "";
		String refill = "";
		String amount = "";
		String serviceStatus = "";
		try {
			strMsisdn = request.getParameter("msisdn");
			strMsisdn = strMsisdn.trim();
			strCircleId = request.getParameter("circleid");
			strCircleId = strCircleId.trim();
			requestType = request.getParameter("requesttype");
			requestType = requestType.trim();
			strServiceId = request.getParameter("serviceid");
			if (requestType.equals("NONSEG")) {
				strServiceId = strServiceId.trim();
			}
			strSessionId = request.getParameter("sessionid");
			strSessionId = strSessionId.trim();
			brand = request.getParameter("brand");
			refill = request.getParameter("refill");
			amount = request.getParameter("amount");
			serviceStatus = request.getParameter("servicestatus");

			if (map.isEmpty()) {
				readConfiguration();
			}
			strData = map.get(strCircleId);
			strIP = map.get("IP");
			strPort = map.get("ACT_PORT");
			strCON_TimeOut = map.get("CONNECTION_TIMEOUT");
			strSO_TimeOut = map.get("SO_TIMEOUT");
			strArr = strData.split("~");
			strCircleName = strArr[0];
			eaiCircle = strArr[1];

			if (strMsisdn.length() == 12) {
				strMsisdn = strMsisdn.substring(2, 12);
			} else {
				strMsisdn = strMsisdn;
			}

			strURL = "https://" + strIP + ":" + strPort;
			out = response.getWriter();
			response.setContentType("text/html;charset=UTF-8");
			response.setHeader("Cache-Control", "no-cache");
			System.out.println("Activation Url=> " +strURL);
			objLogCalTimeReq = Calendar.getInstance();
			strLogData = strSessionId + ",CreditActivation," + strCircleId + "," + strMsisdn;
			LoggerClass.logFile(objLogCalTimeReq, strCircleName, "Req.txt", strLogData);
			try {
				ChotaCreditService objChotaCreditService = new ChotaCreditService(strURL, strCON_TimeOut,
						strSO_TimeOut);
				Response = objChotaCreditService.getModifyServices(strMsisdn, strCircleId, strCircleName, strServiceId,
						eaiCircle, strSessionId, requestType,brand,refill,amount,serviceStatus);
				objLogCalTimeResp = Calendar.getInstance();
				strLogData = strSessionId + ",CreditActivation," + strCircleId + "," + strMsisdn + ",S,RespMsg= "
						+ Response + "," + (objLogCalTimeResp.getTimeInMillis() - objLogCalTimeReq.getTimeInMillis());
				LoggerClass.logFile(objLogCalTimeResp, strCircleName, "Resp.txt", strLogData);
			} catch (Exception ex) {
				ex.printStackTrace();
				Response = "exception";
				objLogCalTimeResp = Calendar.getInstance();
				strLogData = strSessionId + ",CreditActivation," + strCircleId + "," + strMsisdn + "," + ",F,RespMsg= "
						+ Response + "," + (objLogCalTimeResp.getTimeInMillis() - objLogCalTimeReq.getTimeInMillis());
				LoggerClass.logFile(objLogCalTimeResp, strCircleName, "Resp.txt", strLogData);
			}
			out.println("msg=" + Response);
		} catch (Exception e) {
			System.out.println("Exception=" + e);
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * Handles the HTTP <code>GET</code> method.
	 * 
	 * @param request  servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);

	}
}

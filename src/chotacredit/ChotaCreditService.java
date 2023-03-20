package chotacredit;

import com.vodafone.eai.eai_ivr_interface.CLSConsumerReqInfo;
import com.vodafone.eai.eai_ivr_interface.CLSChotaCreditReq;
import com.vodafone.eai.eai_ivr_interface.CLSService;
import com.vodafone.eai.eai_ivr_interface.CLSServiceRequestParameters;
import com.vodafone.eai.eai_ivr_interface.ReqParametersChotaCreditDocument;
import com.vodafone.eai.eai_ivr_interface.ReqParametersChotaCreditDocument.ReqParametersChotaCredit;
import com.vodafone.eai.eai_ivr_interface.RespParametersChotaCreditDocument;
import com.vodafone.eai.eai_ivr_interface.UDHMetaInfo;
import com.vodafone.eai.eai_ivr_interface.UDHMetaInfoResp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.axis2.AxisFault;
import org.apache.axis2.ChotaCredit.Eai_Ivr_InterfaceStub;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.Constants;

/**
 * @author Himanshu Sharma
 *
 */
public class ChotaCreditService {

	Eai_Ivr_InterfaceStub stub = null;
	Calendar objLogCalTimeReq;
	Calendar objLogCalTimeResp;
	String strLogData = "";

	public ChotaCreditService(String strURL, String strCON_TimeOut, String strSO_TimeOut) {
		try {
			// System.setProperty("javax.net.ssl.keyStorePassword", "chota1234");
			System.setProperty("javax.net.ssl.trustStorePassword", "chota1234");
			System.setProperty("javax.net.ssl.trustStore",
					"/home/ussdaps/Tomcat_ChotaCredit_Idea/KeyStore/ChotaCredit/certificate.jks");
			stub = new Eai_Ivr_InterfaceStub(strURL);
			ServiceClient sClient = stub._getServiceClient();
			Options options = sClient.getOptions();
			// options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Constants.VALUE_TRUE);
            //options.setProperty(AddressingConstants.WS_ADDRESSING_VERSION, AddressingConstants.Submission.WSA_NAMESPACE);
			options.setProperty(org.apache.axis2.transport.http.HTTPConstants.HTTP_PROTOCOL_VERSION,
					org.apache.axis2.transport.http.HTTPConstants.HEADER_PROTOCOL_10);
			options.setCallTransportCleanup(true);
			options.setProperty(HTTPConstants.CONNECTION_TIMEOUT, Integer.parseInt(strCON_TimeOut));
			options.setProperty(HTTPConstants.SO_TIMEOUT, Integer.parseInt(strSO_TimeOut));
			sClient.setOptions(options);
		} catch (AxisFault ex) {
			Logger.getLogger(Eai_Ivr_InterfaceStub.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public String getModifyServices(String strMsisdn, String strCircleId, String strCircleName, String strServiceId,
			String eaiCircle, String strSessionId, String requestType,String brand,String refill,String amount,String serviceStatus) {
		String strResult = "";
		System.out.println(strMsisdn+","+serviceStatus);
		if(serviceStatus == null) {
			serviceStatus = "A";
		} 
		try {
			ReqParametersChotaCreditDocument objReqParametersChotaCreditDocument = ReqParametersChotaCreditDocument.Factory
					.newInstance();
			ReqParametersChotaCredit objReqParameterChotaCredit = objReqParametersChotaCreditDocument
					.addNewReqParametersChotaCredit();
			UDHMetaInfo objUDHMetaInfo = objReqParameterChotaCredit.addNewMetaInfo();
            //CLSSegmentedChotaCreditReq cLSSegmentedChotaCreditReq=objReqParameterChotaCredit.addNewSRVSegmentedChotaCreditReq();     
			CLSConsumerReqInfo objCLSConsumerReqInfo = objUDHMetaInfo.addNewConsumerReqInfo();
			objCLSConsumerReqInfo.setCircleId(eaiCircle);
			if (requestType.equals("NONSEG"))
				objCLSConsumerReqInfo.setServiceName("NonSegmentedChotaCredit");
			else
				objCLSConsumerReqInfo.setServiceName("SegmentedChotaCredit");
			objCLSConsumerReqInfo.setChannelName("USSD");
			objCLSConsumerReqInfo.setSegment("PREPAID");
			String[] arrKey = { "123" };
			objCLSConsumerReqInfo.setKeyArray(arrKey);
			objCLSConsumerReqInfo.setUserID("MOBMEUSSD");
			objCLSConsumerReqInfo.setToken("TU9CTUVVU1NEcmVxdWVzdEZvckRhdGFDaG90YUNyZWRpdA==");
			objUDHMetaInfo.setConsumerReqInfo(objCLSConsumerReqInfo);
			CLSChotaCreditReq cLSChotaCreditReq = objReqParameterChotaCredit.addNewSRVChotaCreditReq();
			cLSChotaCreditReq.setMSISDN(Long.parseLong(strMsisdn));
			cLSChotaCreditReq.setApplicationId("USSD");
			cLSChotaCreditReq.setApplicationRequestId(strSessionId);
			cLSChotaCreditReq.setReasonName("LAK");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
			Date date = new Date();
			sdf.format(date);
			Calendar cal = Calendar.getInstance();
			cal = sdf.getCalendar();
			cLSChotaCreditReq.setEffectiveDate(cal);
			cLSChotaCreditReq.setRefillProfileId(refill);
			cLSChotaCreditReq.setBrandIdentifier(brand);
			cLSChotaCreditReq.setAmount(amount);
			cLSChotaCreditReq.setServiceStatus(serviceStatus);

			CLSService cLSService1 = cLSChotaCreditReq.addNewService();
			if (requestType.equals("NONSEG"))
				cLSService1.setServiceId(Long.parseLong(strServiceId));
			cLSChotaCreditReq.setService(cLSService1);

			objReqParameterChotaCredit.setSRVChotaCreditReq(cLSChotaCreditReq);

			objLogCalTimeReq = Calendar.getInstance();
			strLogData = strSessionId + ",CreditActivation," + strCircleId + "," + strMsisdn + "," + strServiceId;
			LoggerClass.logFile(objLogCalTimeReq, strCircleName, "INReq.txt", strLogData);

			System.out.println("Request :: " + objReqParametersChotaCreditDocument.toString());
			//System.out.println("Request xml => " + objReqParametersChotaCreditDocument.xmlText());

			RespParametersChotaCreditDocument objRespParametersChotaCreditDocument = stub
					.chotaCreditOperation(objReqParametersChotaCreditDocument);
			System.out.println("Response :: " + objRespParametersChotaCreditDocument.toString());
			UDHMetaInfoResp objUDHMetaInfoResp = objRespParametersChotaCreditDocument.getRespParametersChotaCredit()
					.getMetaInfo();
			objLogCalTimeResp = Calendar.getInstance();
			strLogData = strSessionId + ",CreditActivation," + strCircleId + "," + strMsisdn + "," + strServiceId
					+ ",S," + (objLogCalTimeResp.getTimeInMillis() - objLogCalTimeReq.getTimeInMillis());
			LoggerClass.logFile(objLogCalTimeResp, strCircleName, "INResp.txt", strLogData);

			strResult = objUDHMetaInfoResp.getStatusInfo().getErrorStatus().trim() + "#"
					+ objUDHMetaInfoResp.getStatusInfo().getErrorDesc().trim() + "#"
					+ objUDHMetaInfoResp.getStatusInfo().getErrorCategory().trim();
			
		} catch (Exception e) {
			e.printStackTrace();
			objLogCalTimeResp = Calendar.getInstance();
			strLogData = strSessionId + ",CreditActivation," + strCircleId + "," + strMsisdn + "," + strServiceId
					+ ",F," + e.getMessage() + ","
					+ (objLogCalTimeResp.getTimeInMillis() - objLogCalTimeReq.getTimeInMillis());
			LoggerClass.logFile(objLogCalTimeResp, strCircleName, "INError.txt", strLogData);

			strResult = "1#NA#NA";
		} finally {
			try {
				stub.cleanup();
			} catch (Exception ex) {
				System.out.println("exception ::" + ex.getMessage());
			}
		}

		return strResult;
	}
}

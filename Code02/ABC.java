package com.bytter.bis.abc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.bytter.bis.BISException;
import com.bytter.bis.Configuration;
import com.bytter.bis.Logging;
import com.bytter.bis.PropertiesProcessor;
import com.bytter.bis.Util;
import com.bytter.bis.XMLProcessor;
import com.bytter.bis.base.BankInterfaceBase;
import com.bytter.bis.beans.Account;
import com.bytter.bis.beans.AccountBalance;
import com.bytter.bis.beans.AccountBill;
import com.bytter.bis.beans.TradeResult;
import com.bytter.bis.beans.Voucher;

/**
 * ũҵ����ר��
 * @author fangyb
 *
 */
public class ABC extends BankInterfaceBase {
	//��ҵ��
	private static String corpNo;
	//��֤��
	private static String authNo;
	//����Ա
	private static String opNo;
	private static boolean staticFlag = true;
	private static Exception exception;
	private static String templetPath;
	//�����ļ�·��
	private static String recFilePath;
	//������
	private static XMLProcessor areaCodeFile;
	//��������Ip��ַ
	private static String bankConnectionIP;
	//�������Ӷ˿ں�
	private static String bankConnectionPort;

	public static HashMap<String, String> pathMap = null;
	public static HashMap<String,String> reCodes = null;
	private static String filePath;

	static {
		try {
			InitializeStaticVariables();
		} catch (Exception e) {
			exception = e;
			staticFlag = false;
		}
	}

	public ABC() throws Exception {
		if (!(staticFlag)) {
			exception.printStackTrace();
			throw new BISException("��ʼ���ӿڱ�������", exception);
		}
	}
    /**
     * ��ʼ������
     * @throws Exception
     */
	protected static void InitializeStaticVariables() throws Exception {
		PropertiesProcessor propFile = new PropertiesProcessor(Configuration.getBiConfigPath().trim());
		templetPath = propFile.getPropertiesValue("templet.path",".\\templet\\abc");
		pathMap = getPathForFile();
		if (Util.stringIsEmpty(templetPath)){
			templetPath = ".\\templet\\abc";
		}
		if (!(new File(templetPath).isDirectory())){
			throw new BISException("ģ���ļ��в����ڡ�");
		}
		if (!(templetPath.endsWith("\\"))){
			templetPath = templetPath.concat("\\");
		}
		authNo = propFile.getPropertiesValue("AuthNo");
		opNo = propFile.getPropertiesValue("OpNo");
		if (Util.stringIsEmpty(opNo)){
			throw new BISException("�����ļ��в���Ա��Ϊ�գ�");
		}
		corpNo = propFile.getPropertiesValue("CorpNo");
		if (Util.stringIsEmpty(corpNo)){
			throw new BISException("�����ļ��м����ල�ֺ�Ϊ�գ�");
		}
		recFilePath = pathMap.get("dtlPath");
		if (Util.stringIsEmpty(recFilePath)){
			throw new BISException("�����ļ��н����ļ�·��Ϊ��!");
		}
		// �ϴ��ļ�·��
		filePath = pathMap.get("payOffPath");
		if (Util.stringIsEmpty(filePath)){
			throw new BISException("�����ļ���filePathΪ��");
		}
		bankConnectionIP = propFile.getPropertiesValue("BankConnectionIP");
		if (Util.stringIsEmpty(bankConnectionIP)){
			throw new BISException("�����ļ�������IPΪ��!");
		}
		bankConnectionPort = propFile.getPropertiesValue("BankConnectionPort");
		if (Util.stringIsEmpty(bankConnectionPort)){
			throw new BISException("�����ļ������ж˿�Ϊ��!");
		}
		try {
			areaCodeFile = new XMLProcessor(new File(templetPath+ "AreaCodeList.xml"));
		} catch (Exception e) {
			new Logging(Configuration.getBankID()).writeLog("û��ȡ���������ļ��������Զ���ȡ��������Ϣ��", e);
		}
	}
	/**
	 * ��Ž����ļ�·��,��ϸ�ļ�·���ͽ����ļ�·��
	 * @return
	 */
	private static HashMap<String, String> getPathForFile() {
		HashMap<String, String> pathMap = new HashMap<String, String>();
		try {
			String pathString = Util.readFile(".\\templet\\abc\\path.txt");
			String[] paths = pathString.split("\r\n");
			for (int i = 0; i < paths.length; i++) {
				String[] path = paths[i].split("=");
				pathMap.put(path[0], path[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pathMap;
	}
	/**
	 * ������Ӧ�����
	 * @param reCode
	 * @return
	 */
	private static HashMap<String, String> getReCode(String reCode) {
		HashMap<String, String> reCodes = new HashMap<String, String>();
		try {
			String pathString = Util.readFile(".\\templet\\abc\\responseCode.txt");
			String[] paths = pathString.split("\r\n");
			for (int i = 0; i < paths.length; i++) {
				String[] path = paths[i].split("=");
				reCodes.put(path[0], path[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reCodes;
	}
	/**
	 * ��ȡ�ļ�����
	 * @param fi
	 * @return
	 * @throws Exception
	 */
	public static String getFileContext(File fi) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(fi));
		String context = "";
		String line = "";
		while ((line = br.readLine()) != null){
			context = context + line;
		}
		return context;
	}
	/**
	 * ��ȡʡ�е�����
	 * @param account
	 * @return
	 * @throws BISException
	 */
	public static String getAreaCode(Account account) throws BISException {
		String areaCode = null;
		if ((account.getArea() == null)|| ((Util.stringIsEmpty(account.getArea().getProvince())) && (Util.stringIsEmpty(account.getArea().getCity())))){
			throw new BISException("û��ά��ʡ����Ϣ,�˺�Ϊ:" + account.getAccountNumber());
		}
		String province = account.getArea().getProvince().trim().replaceAll("ʡ", "");
	    String city = account.getArea().getCity().trim().replaceAll("��", "");
		areaCode = areaCodeFile.getChildTextByKeyNode("/abc/area", "name",city, "code");
		if (areaCode == null){
			areaCode = areaCodeFile.getChildTextByKeyNode("/abc/area", "name",province, "code");
		}
		return areaCode;
	}
	/**
	 * �������
	 * @return
	 */
	public  static String getReqSeqNo() {
		Calendar calendar = Calendar.getInstance();
		String dayOfYear = Util.padString(Integer.toString(calendar.get(6)), 3,false, "0");
		String year = Integer.toString(calendar.get(1)).substring(3, 4);
		String time = Util.getFormattingToday(3);
		String id = year + dayOfYear + time;
		id = Util.padString(id, 12, false, "0");
		return id;
	}
	/**
	 * ��ѯ�ʽ���˻����
	 */
	public TradeResult queryBalancePool(AccountBalance balance) {
		return new QueryBalPool(this.logName).execute(balance);
	}
	/**
	 * ��ѯ�������
	 */
	public TradeResult queryCurrentBalance(AccountBalance balance) {
		return new QueryCurBal(this.logName).execute(balance);
	}
    /**
     * ������ѯ�������
     */
	public TradeResult batchQueryCurrentBalance(ArrayList balances) {
		return new BatchQueryBalance(this.logName).excute(balances);
	}
    /**
     * ��ѯ��ʷ��ϸ
     */
    public TradeResult queryHistoryDetail(AccountBill bill) {
		  String type = bill.getAccount().getAccountType();
		    if ("09".equals(type)) {
		    	//��ѯ���⻧��ϸ
		      return new QueryYYTNoDtl(this.logName).execute(bill);
		    }
		    return new QueryHisDtl(this.logName).execute(bill);
		  }

     /**
      *��ѯ������ϸ
      */
	 public TradeResult queryCurrentDetail(AccountBill bill) {
		    String type = bill.getAccount().getAccountType();
		    if ("09".equals(type)) {
		    	//��ѯ���⻧��ϸ
		      return new QueryYYTNoDtl(this.logName).execute(bill);
		    }
		    return new QueryHisDtl(this.logName).execute(bill);
	  }
	 /**
	  * ����֧��
	  */
	public TradeResult transfer(Voucher voucher) {
		 String type = voucher.getType();
		 //35 ����֧��   10  ����  
	    if (type.equals("35"))
	    {
	      return new PayCenter(this.logName).execute(voucher);
	    }else if(type.equals("10")){
	    	return new AutoGather(this.logName).execute(voucher);
	    }
		return new Transfer(this.logName).execute(voucher);
	}
    /**
     * ��ѯ����֧�����׽��
     */
	public TradeResult queryTransfer(Voucher voucher) {
		return new QueryTransfer(this.logName).execute(voucher);
	}
    /**
     * �ʽ��²�
     */
	public TradeResult appropriate(Voucher voucher) {
		voucher.setUrgent(true);
		return new AutoGather(this.logName).execute(voucher);
	}
   /**
    * �ʽ�鼯
    */
	public TradeResult gather(Voucher voucher) {
		voucher.setUrgent(true);
		return new AutoGather(this.logName).execute(voucher);
	}
    /**
     * ��ѯ�ʽ��²����׽��
     */
	public TradeResult queryAppropriate(Voucher voucher) {
		return new QueryTransfer(this.logName).execute(voucher);
	}
    /**
     * ��ѯ�ʽ�鼯���׽��
     */
	public TradeResult queryGather(Voucher voucher) {
		return new QueryTransfer(this.logName).execute(voucher);
	}

	/**
	 * ������˽����
	 */
	public TradeResult batchPayPrivate(ArrayList vouchers) {
		Voucher voucher = (Voucher)vouchers.get(0);
		//48 ��������  36 ������˽
		if(Util.stringIsEmpty(voucher.getType()) || voucher.getType().equals("48") || voucher.getType().equals("36")){
			//voucher_type=48Ϊ��������
			return new PayOff(this.logName).excute(vouchers);
		}
		if (voucher.isDifBank()) {
			//���˱������ʿ��ж�˽
	        return new Transfer(this.logName).excute(vouchers);
	    }else{
	    	//���˱�������ͬ�ж�˽
			return new SinglePayOff(this.logName).excute(vouchers);
	    }
		
	}

	/**
	 * ��ѯ������˽���׽��
	 */
	public TradeResult batchQueryPrivate(ArrayList vouchers) {
		Voucher voucher = (Voucher)vouchers.get(0);
		//48 ��������  36 ������˽
		if(Util.stringIsEmpty(voucher.getType()) || voucher.getType().equals("48") || voucher.getType().equals("36")){
			return new QueryPayOff(this.logName).excute(vouchers);
		}else{
			return new QueryTransfer(this.logName).excute(vouchers);
		}
	}
	public static String getTempletPath() {
		return templetPath;
	}

	public static Logging getLog() {
		return new Logging(Configuration.getBankID(), Configuration.getLogPath());
	}

	public static String getReqTime() {
		return Util.getFormattingDateTime(new Date(), 2);
	}

	public static String getReqDate() {
		return Util.getFormattingDateTime(new Date(), 0);
	}

	public static String getAuthNo() {
		return authNo;
	}

	public static String getOpNo() {
		return opNo;
	}

	public static String getCorpNo() {
		return corpNo;
	}

	public static String getRecFilePath() {
		return recFilePath;
	}
	public static String getBankIP() {
		return bankConnectionIP;
	}

	public static String getBankPort() {
		return bankConnectionPort;
	}
	public static String getFilePath() {
		return filePath;
	}

	public static void setFilePath(String filePath) {
		ABC.filePath = filePath;
	}

	public static HashMap<String, String> getReCodes() {
		return reCodes;
	}

	public static void setReCodes(HashMap<String, String> reCodes) {
		ABC.reCodes = reCodes;
	}
}
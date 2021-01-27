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
 * 农业银行专线
 * @author fangyb
 *
 */
public class ABC extends BankInterfaceBase {
	//企业号
	private static String corpNo;
	//认证码
	private static String authNo;
	//操作员
	private static String opNo;
	private static boolean staticFlag = true;
	private static Exception exception;
	private static String templetPath;
	//接收文件路径
	private static String recFilePath;
	//地区码
	private static XMLProcessor areaCodeFile;
	//银行连接Ip地址
	private static String bankConnectionIP;
	//银行连接端口号
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
			throw new BISException("初始化接口变量错误。", exception);
		}
	}
    /**
     * 初始化参数
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
			throw new BISException("模板文件夹不存在。");
		}
		if (!(templetPath.endsWith("\\"))){
			templetPath = templetPath.concat("\\");
		}
		authNo = propFile.getPropertiesValue("AuthNo");
		opNo = propFile.getPropertiesValue("OpNo");
		if (Util.stringIsEmpty(opNo)){
			throw new BISException("配置文件中操作员号为空！");
		}
		corpNo = propFile.getPropertiesValue("CorpNo");
		if (Util.stringIsEmpty(corpNo)){
			throw new BISException("配置文件中技术监督局号为空！");
		}
		recFilePath = pathMap.get("dtlPath");
		if (Util.stringIsEmpty(recFilePath)){
			throw new BISException("配置文件中接受文件路径为空!");
		}
		// 上传文件路径
		filePath = pathMap.get("payOffPath");
		if (Util.stringIsEmpty(filePath)){
			throw new BISException("配置文件中filePath为空");
		}
		bankConnectionIP = propFile.getPropertiesValue("BankConnectionIP");
		if (Util.stringIsEmpty(bankConnectionIP)){
			throw new BISException("配置文件中银行IP为空!");
		}
		bankConnectionPort = propFile.getPropertiesValue("BankConnectionPort");
		if (Util.stringIsEmpty(bankConnectionPort)){
			throw new BISException("配置文件中银行端口为空!");
		}
		try {
			areaCodeFile = new XMLProcessor(new File(templetPath+ "AreaCodeList.xml"));
		} catch (Exception e) {
			new Logging(Configuration.getBankID()).writeLog("没有取到机构号文件，不能自动获取机构号信息。", e);
		}
	}
	/**
	 * 存放接收文件路径,明细文件路径和交易文件路径
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
	 * 错误响应码解析
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
	 * 读取文件内容
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
	 * 获取省市地区码
	 * @param account
	 * @return
	 * @throws BISException
	 */
	public static String getAreaCode(Account account) throws BISException {
		String areaCode = null;
		if ((account.getArea() == null)|| ((Util.stringIsEmpty(account.getArea().getProvince())) && (Util.stringIsEmpty(account.getArea().getCity())))){
			throw new BISException("没有维护省市信息,账号为:" + account.getAccountNumber());
		}
		String province = account.getArea().getProvince().trim().replaceAll("省", "");
	    String city = account.getArea().getCity().trim().replaceAll("市", "");
		areaCode = areaCodeFile.getChildTextByKeyNode("/abc/area", "name",city, "code");
		if (areaCode == null){
			areaCode = areaCodeFile.getChildTextByKeyNode("/abc/area", "name",province, "code");
		}
		return areaCode;
	}
	/**
	 * 交易序号
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
	 * 查询资金池账户余额
	 */
	public TradeResult queryBalancePool(AccountBalance balance) {
		return new QueryBalPool(this.logName).execute(balance);
	}
	/**
	 * 查询当日余额
	 */
	public TradeResult queryCurrentBalance(AccountBalance balance) {
		return new QueryCurBal(this.logName).execute(balance);
	}
    /**
     * 批量查询当日余额
     */
	public TradeResult batchQueryCurrentBalance(ArrayList balances) {
		return new BatchQueryBalance(this.logName).excute(balances);
	}
    /**
     * 查询历史明细
     */
    public TradeResult queryHistoryDetail(AccountBill bill) {
		  String type = bill.getAccount().getAccountType();
		    if ("09".equals(type)) {
		    	//查询虚拟户明细
		      return new QueryYYTNoDtl(this.logName).execute(bill);
		    }
		    return new QueryHisDtl(this.logName).execute(bill);
		  }

     /**
      *查询当日明细
      */
	 public TradeResult queryCurrentDetail(AccountBill bill) {
		    String type = bill.getAccount().getAccountType();
		    if ("09".equals(type)) {
		    	//查询虚拟户明细
		      return new QueryYYTNoDtl(this.logName).execute(bill);
		    }
		    return new QueryHisDtl(this.logName).execute(bill);
	  }
	 /**
	  * 对外支付
	  */
	public TradeResult transfer(Voucher voucher) {
		 String type = voucher.getType();
		 //35 代理支付   10  拨付  
	    if (type.equals("35"))
	    {
	      return new PayCenter(this.logName).execute(voucher);
	    }else if(type.equals("10")){
	    	return new AutoGather(this.logName).execute(voucher);
	    }
		return new Transfer(this.logName).execute(voucher);
	}
    /**
     * 查询对外支付交易结果
     */
	public TradeResult queryTransfer(Voucher voucher) {
		return new QueryTransfer(this.logName).execute(voucher);
	}
    /**
     * 资金下拨
     */
	public TradeResult appropriate(Voucher voucher) {
		voucher.setUrgent(true);
		return new AutoGather(this.logName).execute(voucher);
	}
   /**
    * 资金归集
    */
	public TradeResult gather(Voucher voucher) {
		voucher.setUrgent(true);
		return new AutoGather(this.logName).execute(voucher);
	}
    /**
     * 查询资金下拨交易结果
     */
	public TradeResult queryAppropriate(Voucher voucher) {
		return new QueryTransfer(this.logName).execute(voucher);
	}
    /**
     * 查询资金归集交易结果
     */
	public TradeResult queryGather(Voucher voucher) {
		return new QueryTransfer(this.logName).execute(voucher);
	}

	/**
	 * 批量对私交易
	 */
	public TradeResult batchPayPrivate(ArrayList vouchers) {
		Voucher voucher = (Voucher)vouchers.get(0);
		//48 代发工资  36 批量对私
		if(Util.stringIsEmpty(voucher.getType()) || voucher.getType().equals("48") || voucher.getType().equals("36")){
			//voucher_type=48为代发工资
			return new PayOff(this.logName).excute(vouchers);
		}
		if (voucher.isDifBank()) {
			//个人报销单笔跨行对私
	        return new Transfer(this.logName).excute(vouchers);
	    }else{
	    	//个人报销单笔同行对私
			return new SinglePayOff(this.logName).excute(vouchers);
	    }
		
	}

	/**
	 * 查询批量对私交易结果
	 */
	public TradeResult batchQueryPrivate(ArrayList vouchers) {
		Voucher voucher = (Voucher)vouchers.get(0);
		//48 代发工资  36 批量对私
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
package com.bytter.bis.abc;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.jdom.Element;
import org.jdom.xpath.XPath;

import com.bytter.bis.BISException;
import com.bytter.bis.Constants;
import com.bytter.bis.XMLProcessor;
import com.bytter.bis.base.BusinessBase;
import com.bytter.bis.beans.AccountBalance;
import com.bytter.bis.beans.TradeData;
/**
 * 查询批量账户余额解析类
 * @author fangyb
 *
 */
public class BatchQueryBalanceParser extends ABCParser {
	public void execute(TradeData tradeData, ArrayList businesses) throws Exception {
		// TODO 自动生成方法存根
		String xpathCmp = "/ap/Cmp";
		String xpathRoot = "/ap";
		String xpathCme = "/ap/Cme";
		try {
			XMLProcessor processor = null;
			try {
				processor = getValidData(tradeData);
			} catch (Exception e) {
				throw new BISException("银行传回XML格式无效！原因：", e);
			}
			this.setReturnInfo(processor, tradeData);
			if (!Constants.TRADE_SUCCEED.equals(tradeData.getBankResult())){
				return;
			}
			Element cmp = (Element) XPath.selectSingleNode(processor.getRoot(), xpathCmp);
			Element root = (Element) XPath.selectSingleNode(processor.getRoot(), xpathRoot);
			Element cme = (Element) XPath.selectSingleNode(processor.getRoot(), xpathCme);
			String isFile = root.getChildText("FileFlag").trim();
			String retContext = null;
			// 如果文件标记为0则数据以串方式传输否则以文件方式传输
			if (isFile.equals("0")) {
				retContext = cmp.getChildText("RespPrvData");
			} else {
				File fi = new File(ABC.getRecFilePath());
				retContext = ABC.getFileContext(fi);
			}
			int fieldNum = Integer.parseInt(cme.getChildText("FieldNum"));
			int recNum = Integer.parseInt(cme.getChildText("RecordNum"));
			StringTokenizer st = new StringTokenizer(retContext, "[|]");
			int totalNum = st.countTokens();
			if (fieldNum * recNum + fieldNum == totalNum) {
				String[][] lis = prvParse(retContext, fieldNum, recNum);
				for (int i = 0; i <recNum; i++) {
					try {
						AccountBalance balance = (AccountBalance) businesses.get(i);
						balance.getAccount().getArea().setAreaCode(lis[i][0]);
					    balance.getAccount().setAccountNumber(lis[i][1]);
						balance.getAccount().setCurrencyType(lis[i][2]);
						//账户余额
						balance.setBalance(new Double(lis[i][4]));
						//昨天余额
						balance.setLastBalance(new Double(lis[i][4]));
						//可用余额
						balance.setUsableBalance(new Double(lis[i][5]));
					} catch (Exception e) {
						ABC.getLog().writeLog("多用户查询在解析帐户：" + lis[i][1] + "时出错！", e,logName);
					}
				}
				tradeData.setBankResult(Constants.TRADE_SUCCEED);
			} else {
				tradeData.setBankResult(Constants.TRADE_PART_SUCCEED);
				throw new BISException("银行返回数据包不完整！");
			}
		} catch (Exception e) {
			throw new BISException("系统解析多用户查询结果时出错！",e);
		}
	}

	public void execute(TradeData tradeData, BusinessBase business) throws Exception {
		// 不用实现
	}

}

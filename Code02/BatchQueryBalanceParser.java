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
 * ��ѯ�����˻���������
 * @author fangyb
 *
 */
public class BatchQueryBalanceParser extends ABCParser {
	public void execute(TradeData tradeData, ArrayList businesses) throws Exception {
		// TODO �Զ����ɷ������
		String xpathCmp = "/ap/Cmp";
		String xpathRoot = "/ap";
		String xpathCme = "/ap/Cme";
		try {
			XMLProcessor processor = null;
			try {
				processor = getValidData(tradeData);
			} catch (Exception e) {
				throw new BISException("���д���XML��ʽ��Ч��ԭ��", e);
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
			// ����ļ����Ϊ0�������Դ���ʽ����������ļ���ʽ����
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
						//�˻����
						balance.setBalance(new Double(lis[i][4]));
						//�������
						balance.setLastBalance(new Double(lis[i][4]));
						//�������
						balance.setUsableBalance(new Double(lis[i][5]));
					} catch (Exception e) {
						ABC.getLog().writeLog("���û���ѯ�ڽ����ʻ���" + lis[i][1] + "ʱ����", e,logName);
					}
				}
				tradeData.setBankResult(Constants.TRADE_SUCCEED);
			} else {
				tradeData.setBankResult(Constants.TRADE_PART_SUCCEED);
				throw new BISException("���з������ݰ���������");
			}
		} catch (Exception e) {
			throw new BISException("ϵͳ�������û���ѯ���ʱ����",e);
		}
	}

	public void execute(TradeData tradeData, BusinessBase business) throws Exception {
		// ����ʵ��
	}

}

package com.bytter.bis.abc;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.bytter.bis.BISException;
import com.bytter.bis.Constants;
import com.bytter.bis.XMLProcessor;
import com.bytter.bis.base.BusinessBase;
import com.bytter.bis.base.ParserBase;
import com.bytter.bis.beans.TradeData;

/**
 * ��������
 * 
 * @author fangyb
 * 
 */
public abstract class ABCParser extends ParserBase {
	public abstract void execute(TradeData tradeData, ArrayList businesses)throws Exception;

	public abstract void execute(TradeData tradeData, BusinessBase business)throws Exception;
	String bankRetCodePath = "/ap/RespCode";
	String bankRetMessPath = "/ap/RespInfo";
	/**
	 * ��֤���ĺϷ���
	 * 
	 * @param tradeData
	 * @return
	 * @throws Exception
	 */
	protected XMLProcessor getValidData(TradeData tradeData) throws Exception {
		XMLProcessor result = null;
		try {
			tradeData.setReceivedData(tradeData.getReceivedData().replaceAll("[\r,\n]", ""));
			result = new XMLProcessor(tradeData.getReceivedData());
		} catch (Exception e) {
			throw new BISException("�������󣬿����Ƿ������ݰ����Ǻ�ʽ��xml");
		}
		return result;
	}

	/**
	 * ��������ͷ��Ϣ
	 * 
	 * @param xml
	 * @param tradeData
	 * @throws Exception
	 */
	protected void setReturnInfo(XMLProcessor xml, TradeData tradeData)throws Exception {
		tradeData.setBankReturnCode(xml.getNodeText(this.bankRetCodePath));
		tradeData.setBankReturnMessage(xml.getNodeText(this.bankRetMessPath));
		// 0,0000,0001 ��ʾ��Ӧ�ɹ� -99 ��Ӧʧ��
		if ("0".equals(tradeData.getBankReturnCode())|| "0000".equals(tradeData.getBankReturnCode())|| "0001".equals(tradeData.getBankReturnCode())) {
			tradeData.setBankResult(Constants.TRADE_SUCCEED);
		} else {
			if ("-99".equals(tradeData.getBankReturnCode())) {
				tradeData.setBankResult(Constants.TRANSFER_STATUS_WAIT_SEND);
			} else {
				tradeData.setBankResult(Constants.TRADE_FAILURE);
			}
		}
	}
	/**
	 * ˽�����ݽ������
	 * @param context
	 * @param fieldNum
	 * @param recNum
	 * @return
	 * @throws Exception
	 */
	public String[][] prvParse(String context, int fieldNum, int recNum)throws Exception {
		String[][] retCont = null;
		StringTokenizer st = new StringTokenizer(context, "|");
		ArrayList context1 = new ArrayList();
		while (st.hasMoreTokens()) {
			context1.add(st.nextToken());
		}
		recNum = context1.size() / fieldNum;
		if (context1.size() < (recNum) * fieldNum) {
			throw new BISException("���ݲ�������"+context1.size()+"-"+(recNum) * fieldNum);
		}
		/*if (recNum == 1) {
			return null;
		}*/
		retCont = new String[recNum][fieldNum];
		for (int i = 1; i < recNum; i++) {
			for (int j = 0; j < fieldNum; j++) {
				retCont[i][j] = (String) context1.get(i * fieldNum + j);
			}
		}
		return retCont;

	}

}

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
 * 解析基类
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
	 * 验证报文合法性
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
			throw new BISException("解析错误，可能是返回数据包不是合式的xml");
		}
		return result;
	}

	/**
	 * 解析报文头信息
	 * 
	 * @param xml
	 * @param tradeData
	 * @throws Exception
	 */
	protected void setReturnInfo(XMLProcessor xml, TradeData tradeData)throws Exception {
		tradeData.setBankReturnCode(xml.getNodeText(this.bankRetCodePath));
		tradeData.setBankReturnMessage(xml.getNodeText(this.bankRetMessPath));
		// 0,0000,0001 表示响应成功 -99 响应失败
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
	 * 私有数据解包方法
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
			throw new BISException("数据不完整。"+context1.size()+"-"+(recNum) * fieldNum);
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

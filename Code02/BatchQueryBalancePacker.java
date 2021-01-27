package com.bytter.bis.abc;

import java.util.ArrayList;

import com.bytter.bis.BISException;
import com.bytter.bis.base.BusinessBase;
import com.bytter.bis.beans.AccountBalance;
import com.bytter.bis.beans.TradeData;

/**
 * 批量查询账户余额组包类
 * @author fangyb
 *
 */
public class BatchQueryBalancePacker extends ABCPacker {
	String head = "Prov|AccNo|Cur|";
	String pathCmp = "/ap/Cmp";
	String pathCme = "/ap/Cme";
	String pathRoot = "/ap";
	public void execute(TradeData tradeData, ArrayList businesses) throws Exception {
		try {
			tradeData.setTradeID("queryBal");
			processor = getTemplet(tradeData);
			String prvData = "";
			for(int i=0;i<businesses.size();i++){
				AccountBalance balance=(AccountBalance)businesses.get(i);
				//地区码
				prvData += balance.getAccount().getArea().getAreaCode() + "|";
				//查询账号
				prvData += balance.getAccount().getAccountNumber()+"|";
				//币别类型
				prvData += balance.getAccount().getCurrencyType()+"|";
			}
			processor.setNodeText(pathCmp + "/RespPrvData", prvData);
			processor.setNodeText(pathCme + "/FieldNum", "3");
			//总笔数
			processor.setNodeText(pathCme + "/RecordNum", Integer.toString(businesses.size()));
			tradeData.setSendData(processor.getDocText(false));
		} catch (Exception e) {
			throw new BISException("多用户查询余额组包出错！",e);
		}
	}

	public void execute(TradeData tradeData, BusinessBase business) throws Exception {
		// 不用实现
	}

}

package com.bytter.bis.abc;

import com.bytter.bis.BISException;
import com.bytter.bis.XMLProcessor;
import com.bytter.bis.base.BusinessBase;
import com.bytter.bis.beans.TradeData;
import com.bytter.bis.beans.Voucher;
import java.util.ArrayList;

/**
 * 批量对私交易或代发工资解析类
 * 
 * @author fangyb
 * 
 */
public class PayOffParser extends ABCParser {
	public void execute(TradeData tradeData, ArrayList businesses)
			throws Exception {
		XMLProcessor processor;
		try {
			Voucher vo;
			processor = getValidData(tradeData);
			setReturnInfo(processor, tradeData);
			if (!("0".equals(tradeData.getBankResult()))) {
				for (int row = 0; row < businesses.size(); ++row) {
					vo = (Voucher) businesses.get(row);
					vo.setTransResult("1");
					vo.getAccessionalInfo().put("UpdateMessage",tradeData.getBankReturnMessage());
				}
				return;
			}
			for (int row = 0; row < businesses.size(); ++row) {
				vo = (Voucher) businesses.get(row);
				vo.setTransResult("1");
				vo.getAccessionalInfo().put("UpdateMessage",tradeData.getBankReturnMessage());
			}
		} catch (Exception e) {
			throw new BISException("系统在个人报帐解析时出错", e);
		}
	}

	public void execute(TradeData tradeData, BusinessBase business)
			throws Exception {
	}
}
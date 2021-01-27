package com.bytter.bis.abc;

import java.util.ArrayList;
import com.bytter.bis.BISException;
import com.bytter.bis.Constants;
import com.bytter.bis.base.BusinessBase;
import com.bytter.bis.beans.TradeData;
import com.bytter.bis.beans.Voucher;
/**
 * 资金下拨解析类
 * @author fangyb
 *
 */
public class AppropriateParser extends ABCParser {
	public void execute(TradeData tradeData, BusinessBase business) throws Exception {
		try {
			//方增琪：全部的付款解析都把结果置成等待查询，由查询结果线程去查
			Voucher vo = (Voucher) business;
			vo.setTransResult(Constants.TRANSFER_STATUS_WAIT_SEND);			
			tradeData.setBankResult(Constants.TRADE_SUCCEED);
		} catch (Exception ee) {
			tradeData.setBankResult(Constants.TRADE_FAILURE);
			throw new BISException("系统在解析下拨返回数据时出错！原因：", ee);
		}
	}

	public void execute(TradeData tradeData, ArrayList businesses) throws Exception {

	}

}
package com.bytter.bis.abc;

import java.util.ArrayList;

import com.bytter.bis.BISException;
import com.bytter.bis.Constants;
import com.bytter.bis.base.BusinessBase;
import com.bytter.bis.beans.TradeData;
import com.bytter.bis.beans.Voucher;
/**
 * 自主归集解析类
 * @author fangyb
 *
 */
public class AutoGatherParser extends ABCParser {

	@Override
	public void execute(TradeData tradeData, ArrayList businesses)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute(TradeData tradeData, BusinessBase business)
			throws Exception {
		try {
			Voucher voucher = (Voucher)business;
			voucher.setTransResult(Constants.TRANSFER_STATUS_WAIT_SEND);
			tradeData.setBankResult(Constants.TRADE_SUCCEED);
		} catch (Exception e) {
			throw new BISException("系统在自主归集解包错误!",e);
		}

	}

}

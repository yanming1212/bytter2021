package com.bytter.bis.abc;

import java.util.ArrayList;

import com.bytter.bis.BISException;
import com.bytter.bis.Constants;
import com.bytter.bis.XMLProcessor;
import com.bytter.bis.base.BusinessBase;
import com.bytter.bis.beans.TradeData;
import com.bytter.bis.beans.Voucher;
/**
 * 代理支付解析类
 * @author fangyb
 *
 */
public class PayCenterParser extends ABCParser{
	public void execute(TradeData tradeData, ArrayList businesses) throws Exception {
		// TODO Auto-generated method stub
	}
	public void execute(TradeData tradeData, BusinessBase business) throws Exception {
		// TODO Auto-generated method stub
		try {
			XMLProcessor processor = null;
			try {
				processor = getValidData(tradeData);
			} catch (Exception e) {
				throw new BISException("银行返回XML格式无效！");
			}
			setReturnInfo(processor,tradeData);
			Voucher vo = (Voucher) business;
			if("0001".equals(tradeData.getBankReturnCode())||"0000".equals(tradeData.getBankReturnCode())){
				vo.setTransResult(Constants.TRANSFER_STATUS_WAIT_SEND);
			}else{
				vo.setTransResult(Constants.TRANSFER_STATUS_WAIT_SEND);
			}
			tradeData.setBankResult(Constants.TRADE_SUCCEED);
			
		} catch (Exception ee) {
			throw new BISException("解析代理付款返回数据出错！", ee);
		}
	}

}

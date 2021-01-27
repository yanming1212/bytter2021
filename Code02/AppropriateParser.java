package com.bytter.bis.abc;

import java.util.ArrayList;
import com.bytter.bis.BISException;
import com.bytter.bis.Constants;
import com.bytter.bis.base.BusinessBase;
import com.bytter.bis.beans.TradeData;
import com.bytter.bis.beans.Voucher;
/**
 * �ʽ��²�������
 * @author fangyb
 *
 */
public class AppropriateParser extends ABCParser {
	public void execute(TradeData tradeData, BusinessBase business) throws Exception {
		try {
			//��������ȫ���ĸ���������ѽ���óɵȴ���ѯ���ɲ�ѯ����߳�ȥ��
			Voucher vo = (Voucher) business;
			vo.setTransResult(Constants.TRANSFER_STATUS_WAIT_SEND);			
			tradeData.setBankResult(Constants.TRADE_SUCCEED);
		} catch (Exception ee) {
			tradeData.setBankResult(Constants.TRADE_FAILURE);
			throw new BISException("ϵͳ�ڽ����²���������ʱ����ԭ��", ee);
		}
	}

	public void execute(TradeData tradeData, ArrayList businesses) throws Exception {

	}

}
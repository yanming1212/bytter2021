package com.bytter.bis.abc;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.bytter.bis.BISException;
import com.bytter.bis.Constants;
import com.bytter.bis.base.BusinessBase;
import com.bytter.bis.beans.TradeData;
import com.bytter.bis.beans.Voucher;

/**
 * 资金下拨组包类
 * 
 * @author fangyb
 * 
 */
public class AppropriatePacker extends ABCPacker {
	public void execute(TradeData tradeData, BusinessBase business)
			throws Exception {
		try {
			String pathRoot = "/ap";
			String pathCmp = "/ap/Cmp";
			String pathCorp = "/ap/Corp";
			Voucher vo = (Voucher) business;
			tradeData.setTradeID("Appropriate");
			processor = getTemplet(tradeData);
			DecimalFormat decFormat = new java.text.DecimalFormat("###0.00");
			//交易金额
			processor.setNodeText(pathRoot + "/Amt",decFormat.format(vo.getAmount().doubleValue()));
            //预约日期
			processor.setNodeText(pathCorp + "/BookingDate", ABC.getReqDate());
			//预约时间
			processor.setNodeText(pathCorp + "/BookingTime", ABC.getReqTime());
			//摘要
			processor.setNodeText(pathCorp + "/Abstract ", vo.getRemark());
			//附言
			processor.setNodeText(pathCorp + "/Postscript",vo.getPostScript());
			//收款账号
			processor.setNodeText(pathCmp + "/CrAccNo", vo.getPayeeAccount().getAccountNumber());
			//省市地区码
			processor.setNodeText(pathCmp + "/CrProv", ABC.getAreaCode(vo.getPayeeAccount()));
			vo.getAccessionalInfo().put(Constants.DB_REVERSE1,processor.getNodeText("/ap/ReqSeqNo"));
			tradeData.setSendData(processor.getDocText(false));
		} catch (Exception e) {
			throw new BISException("系统在下拨组包时出错！", e);
		}
	}

	public void execute(TradeData tradeData, ArrayList businesses)
			throws Exception {

	}
}
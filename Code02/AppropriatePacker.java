package com.bytter.bis.abc;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.bytter.bis.BISException;
import com.bytter.bis.Constants;
import com.bytter.bis.base.BusinessBase;
import com.bytter.bis.beans.TradeData;
import com.bytter.bis.beans.Voucher;

/**
 * �ʽ��²������
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
			//���׽��
			processor.setNodeText(pathRoot + "/Amt",decFormat.format(vo.getAmount().doubleValue()));
            //ԤԼ����
			processor.setNodeText(pathCorp + "/BookingDate", ABC.getReqDate());
			//ԤԼʱ��
			processor.setNodeText(pathCorp + "/BookingTime", ABC.getReqTime());
			//ժҪ
			processor.setNodeText(pathCorp + "/Abstract ", vo.getRemark());
			//����
			processor.setNodeText(pathCorp + "/Postscript",vo.getPostScript());
			//�տ��˺�
			processor.setNodeText(pathCmp + "/CrAccNo", vo.getPayeeAccount().getAccountNumber());
			//ʡ�е�����
			processor.setNodeText(pathCmp + "/CrProv", ABC.getAreaCode(vo.getPayeeAccount()));
			vo.getAccessionalInfo().put(Constants.DB_REVERSE1,processor.getNodeText("/ap/ReqSeqNo"));
			tradeData.setSendData(processor.getDocText(false));
		} catch (Exception e) {
			throw new BISException("ϵͳ���²����ʱ����", e);
		}
	}

	public void execute(TradeData tradeData, ArrayList businesses)
			throws Exception {

	}
}
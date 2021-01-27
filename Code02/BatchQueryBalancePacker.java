package com.bytter.bis.abc;

import java.util.ArrayList;

import com.bytter.bis.BISException;
import com.bytter.bis.base.BusinessBase;
import com.bytter.bis.beans.AccountBalance;
import com.bytter.bis.beans.TradeData;

/**
 * ������ѯ�˻���������
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
				//������
				prvData += balance.getAccount().getArea().getAreaCode() + "|";
				//��ѯ�˺�
				prvData += balance.getAccount().getAccountNumber()+"|";
				//�ұ�����
				prvData += balance.getAccount().getCurrencyType()+"|";
			}
			processor.setNodeText(pathCmp + "/RespPrvData", prvData);
			processor.setNodeText(pathCme + "/FieldNum", "3");
			//�ܱ���
			processor.setNodeText(pathCme + "/RecordNum", Integer.toString(businesses.size()));
			tradeData.setSendData(processor.getDocText(false));
		} catch (Exception e) {
			throw new BISException("���û���ѯ����������",e);
		}
	}

	public void execute(TradeData tradeData, BusinessBase business) throws Exception {
		// ����ʵ��
	}

}

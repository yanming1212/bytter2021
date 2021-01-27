package com.bytter.bis.abc;

import com.bytter.bis.BISException;
import com.bytter.bis.Constants;
import com.bytter.bis.Util;
import com.bytter.bis.base.BusinessBase;
import com.bytter.bis.beans.TradeData;
import com.bytter.bis.beans.Voucher;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
/**
 * ������˽���׻�������������
 * @author fangyb
 *
 */
public class PayOffPacker extends ABCPacker {
	public void execute(TradeData tradeData, ArrayList businesses)throws Exception {
		List vouchers;
		try {
			vouchers = businesses;
			tradeData.setTradeID("payOff");
			Voucher sumVo = (Voucher) businesses.get(0);
			this.processor = getTemplet(tradeData);
			//������ˮ��
			String reqNo=ABC.getCorpNo()+sumVo.getAccessionalInfo().get(Constants.DB_REVERSE1).toString().trim();
			//�����������
			processor.setNodeText("/ap/ReqSeqNo",reqNo);
			//���ʡ�е�����
			this.processor.setNodeText(this.root + "/Cmp/DbProv",ABC.getAreaCode(sumVo.getPayAccount()));
			//����˺�
			this.processor.setNodeText(this.root + "/Cmp/DbAccNo",((Voucher) vouchers.get(0)).getPayAccount().getAccountNumber());
			//�ұ���
			this.processor.setNodeText(this.root + "/Cmp/DbCur","01");
			//�ϴ��ļ���
			this.processor.setNodeText(this.root + "/Cmp/BatchFileName",((Voucher) vouchers.get(0)).getVoucherNo() + ".txt");
			//����˻�����
			this.processor.setNodeText(this.root + "/Corp/DbAccName",((Voucher) vouchers.get(0)).getPayAccount().getAccountName());
			//����,��ע
			this.processor.setNodeText(this.root + "/Corp/Postscript",((Voucher) vouchers.get(0)).getRemark());
			StringBuffer sb = new StringBuffer();
			double totalValue = 0;
			//ָ������������
			for (int i = 0; i < vouchers.size(); ++i) {
				Voucher voucher = (Voucher) vouchers.get(i);
				totalValue =  totalValue + voucher.getAmount().doubleValue();
				//���
				sb.append(Util.padString(ABC.getCorpNo(), 20, " "));
				//�տ�˻�����
				sb.append(Util.padString(voucher.getPayeeAccount().getAccountName(), 32, " "));
				//�տ�˺�
				sb.append(Util.padString(voucher.getPayeeAccount().getAccountNumber(),30," "));
				//���׽��
				sb.append(Util.padString(voucher.getAmount().toString(),18," "));
				//��ע
				sb.append(Util.padString(voucher.getRemark(),30," "));
				sb.append("\r\n");
			}
			//�����ܽ��
			this.processor.setNodeText(this.root + "/Amt", totalValue + "");
			//�����ܱ���
			this.processor.setNodeText(this.root + "/Corp/TotalNum",vouchers.size() + "");
			//�ϴ��ļ�·��
			String filePath = ABC.getFilePath()+ ((Voucher) vouchers.get(0)).getVoucherNo()+".txt";
			File myFilePath = null;
			myFilePath = new File(filePath);
			myFilePath.createNewFile();
			FileWriter resultFile = new FileWriter(myFilePath);
			PrintWriter myFile = new PrintWriter(resultFile);
			myFile.print(sb.toString());
			myFile.close();
			resultFile.close();
			tradeData.setSendData(this.processor.getDocText(false));
		} catch (Exception e) {
			throw new BISException("ϵͳ�ڸ��˱������ʱ����", e);
		}
	}

	public void execute(TradeData tradeData, BusinessBase business)
			throws Exception {
	}
	
	
}
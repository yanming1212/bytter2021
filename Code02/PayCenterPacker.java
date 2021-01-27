package com.bytter.bis.abc;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.bytter.bis.BISException;
import com.bytter.bis.Util;
import com.bytter.bis.base.BusinessBase;
import com.bytter.bis.beans.Account;
import com.bytter.bis.beans.TradeData;
import com.bytter.bis.beans.Voucher;
/**
 * ����֧�������
 * @author fangyb
 *
 */
public class PayCenterPacker extends ABCPacker{
	public void execute(TradeData tradeData, ArrayList businesses) throws Exception {
		// TODO Auto-generated method stub
	}
	public void execute(TradeData tradeData, BusinessBase business) throws Exception {
		// TODO Auto-generated method stub
		try {
			String pathCmp = "/ap/Cmp";
			String pathCorp = "/ap/Corp";
			String pathRoot = "/ap";
			Voucher vo=(Voucher)business;
			tradeData.setTradeID("PayCenter");
			Account pay=vo.getPayAccount();
			//�����˺���Ϣ
			Account parent = (Account)vo.getAccessionalInfo().get("agentPay");
			processor=getTemplet(tradeData);
			//������ˮ��
			processor.setNodeText(pathRoot+"/ReqSeqNo", ABC.getCorpNo()+vo.getVoucherNo());
			DecimalFormat decf=new DecimalFormat("###0.00");
			//���׽��
			processor.setNodeText(pathRoot+"/Amt",decf.format(vo.getAmount().doubleValue()));
			//�����˺�
			processor.setNodeText(pathCmp+"/DbAccNo",pay.getAccountNumber());
			//�����˺�ʡ�е�����
		    processor.setNodeText(pathCmp+"/DbProv",ABC.getAreaCode(pay));
		    //�տ����ʺų��ȿ��� 
		    String payeeAreaCode = "";
		    if(vo.isDifBank()){
				  processor.setNodeText(pathCmp + "/CrProv","");
				  processor.setNodeText(pathCmp + "/CrAccNo", vo.getPayeeAccount().getAccountNumber());
			}else{
				String payeeNo = vo.getPayeeAccount().getAccountNumber();
				if(payeeNo.length() == 17){
					payeeAreaCode = payeeNo.substring(0, 2);
					payeeNo = payeeNo.substring(2, 17);
				}else{
					payeeAreaCode = ABC.getAreaCode(vo.getPayeeAccount());
					   if (Util.stringIsEmpty(payeeAreaCode)){
						  throw new BISException("�տ���������޷���ȡ��Ϊ��!");
					   }
				}
			  processor.setNodeText(pathCmp + "/CrAccNo", payeeNo);
			  processor.setNodeText(pathCmp+"/CrProv",payeeAreaCode);	
			}
		    //�����˺�
			processor.setNodeText(pathCorp+"/SubAccNo",parent.getAccountNumber());
			//����������
			processor.setNodeText(pathCorp+"/SubAccName",parent.getAccountName());
			//�Ӽ���־
			processor.setNodeText(pathCorp+"/UrgencyFlag",vo.isUrgent()?"Y":"N");
			//���б�־
			processor.setNodeText(pathCorp+"/OthBankFlag",vo.isDifBank()?"1":"0");
			//���ͬ�Ǳ�־
			processor.setNodeText(pathCorp+"/OthCenterFlag",vo.isDifArea()?"0":"1");
			//�տ�˻���
			processor.setNodeText(pathCorp+"/CrAccName",vo.getPayeeAccount().getAccountName());
			String payeeBankName=vo.getPayeeAccount().getOpenBranchName().trim();
			//�տ����������
			processor.setNodeText(pathCorp+"/CrBankName",payeeBankName.length()<=34?payeeBankName:getOpenBankName(payeeBankName,34));
			//����˺�
			processor.setNodeText(pathCorp+"/DbAccName",pay.getAccountName());
			//����˻�����
			processor.setNodeText(pathCorp+"/DbBankName",pay.getOpenBranchName());
			//��ע
			String userFor=vo.getRemark();
			//String postscript = "�����������" + parent.getAccountName() + "-" + ABC.getAreaCode(pay) + "-" + parent.getAccountNumber();
			//�Ĵ��������˾ �����У�ϵͳ�ڲ���˾����+ϵͳ�ڲ���˾�����˺ţ���ʵ�û���д���� 2013-11-15 huangy 
			//String postscript = "�����У�" + vo.getPayAccount().getAccountName() + vo.getPayAccount().getAccountNumber() + "," + vo.getPostScript();
			
			/* �Ĵ��������˾  2013-11-21 huangy 
			 * ��������˾���ƣ��Ĵ����缯�Ų������޹�˾��ʡ�д��룺22����ʵ�����˺ţ�240101040027720   ���ԣ�����
				���ո�ʽ��Ӧ���ǣ� 
				<PostscriptRes>���ʣ��Ĵ����缯�Ų������޹�˾22-240101040027720</PostscriptRes> 
				        ����Ϊ�˸�ʽ��ũ�л������18λҲ����22-240101040027720��ʡ�д���֮��ȫΪ����ַ����ܳ���Ϊ18�ֽڣ� 
				ͬ���ַ��������Ϊ��C68�������ַ��������Ϊ��C58�������˴���߿�ʼ��ȡ��Ҳ���ǴӸ��Կ�ʼ��ȡ��
				*/
			//����
			String postscript1 = new StringBuffer(vo.getPostScript()+"��" + vo.getPayAccount().getAccountName()).reverse().toString() ;
			String postscript2 = ABC.getAreaCode(pay) + "-" + vo.getPayAccount().getAccountNumber();
			//���ת���ɶ�ȫ��
			postscript1 = Util.charChangeHalfToFull(postscript1);
			processor.setNodeText(pathCorp+"/WhyUse",userFor==null?"���":userFor);
			String postscript = "";
			if(vo.isDifBank()){
				//����58λ
				postscript = postscript1.getBytes().length > 40 ? Util.splitIt(postscript1, 40) : postscript1;
				processor.setNodeText(pathCorp+"/PostscriptRes",new StringBuffer(postscript).reverse().toString()+ postscript2);
				String bankNo = (String)vo.getAccessionalInfo().get("reverse2");
				processor.setNodeText(pathCorp+"/CrBankNo",bankNo);
			}else{
				//����68λ
				postscript = postscript1.getBytes().length > 50 ? Util.splitIt(postscript1, 50) : postscript1;
				processor.setNodeText(pathCorp+"/PostscriptRes",new StringBuffer(postscript).reverse().toString() + postscript2);
			}
			tradeData.setSendData(processor.getDocText(false));
		} catch (Exception e) {
			throw new BISException("�������",e);
		}
	}
}

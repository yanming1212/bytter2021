package com.bytter.bis.abc;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.bytter.bis.BISException;
import com.bytter.bis.Configuration;
import com.bytter.bis.Util;
import com.bytter.bis.base.BusinessBase;
import com.bytter.bis.beans.TradeData;
import com.bytter.bis.beans.Voucher;

/**
 * �����ʽ�鼯�����
 * @author fangyb
 *
 */
public class AutoGatherPacker extends ABCPacker {

	@Override
	public void execute(TradeData tradeData, ArrayList businesses)
			throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void execute(TradeData tradeData, BusinessBase business)
			throws Exception {
		try {
			String pathRoot = "/ap";
			String pathCmp = "/ap/Cmp";
			String pathCorp = "/ap/Corp";
			//���ý��ױ�ʶΪAutoGather
			tradeData.setTradeID("AutoGather");
			//��ȡ����ģ��
			processor = getTemplet(tradeData);
			Voucher vo = (Voucher) business;
			processor.setNodeText("/ap/ReqSeqNo",ABC.getCorpNo()+vo.getVoucherNo());
			//���ý��׽��
			DecimalFormat decf=new DecimalFormat("###0.00");
			processor.setNodeText(pathRoot + "/Amt", decf.format(vo.getAmount().doubleValue()));
			//���ñ�ע
			processor.setNodeText(pathCorp + "/Abstract", vo.getRemark());             
			//���ø���
			processor.setNodeText(pathCorp + "/Postscript", vo.getPostScript());         
			//�ϻ��°α�־  0 �ϻ�  1 �²�
			if(vo.getVoucherNo().substring(3,4).equals("3")){
				processor.setNodeText(pathCmp+"/TrFlag", "1");
				String dbProv = ABC.getAreaCode(vo.getPayeeAccount());//�¼��˺�
				String crProv = ABC.getAreaCode(vo.getPayAccount());//�ϼ��˺�
				if(Util.stringIsEmpty(dbProv)){
					throw new BISException("�¼�ʡ��Ϊ��:" + vo.getPayeeAccount().getArea().getProvince() + "--" + vo.getPayeeAccount().getArea().getCity());
				}
				if(Util.stringIsEmpty(crProv)){
					throw new BISException("�ϼ�ʡ��Ϊ��:" + vo.getPayAccount().getArea().getProvince() + "--" + vo.getPayAccount().getArea().getCity());
				}
				//�����¼��ʺ�
				processor.setNodeText(pathCmp + "/DbAccNo", vo.getPayeeAccount().getAccountNumber());
				//�����¼��˻���
				processor.setNodeText(pathCorp+"/DbAccName", vo.getPayeeAccount().getAccountName());
				//���ø���ʡ��
				processor.setNodeText(pathCmp + "/DbProv", dbProv);
				//�����ϼ����ʺ�
				processor.setNodeText(pathCmp + "/CrAccNo", vo.getPayAccount().getAccountNumber());
				//�����շ�ʡ��
				processor.setNodeText(pathCmp + "/CrProv", crProv);
			}else if(vo.getType()==null){
				processor.setNodeText(pathCmp+"/TrFlag", "0");
				String dbProv = ABC.getAreaCode(vo.getPayAccount());//�¼��˺�
				String crProv = ABC.getAreaCode(vo.getPayeeAccount());//�ϼ��˺�
				if(Util.stringIsEmpty(dbProv)){
					throw new BISException("�¼�ʡ��Ϊ��:" + vo.getPayAccount().getArea().getProvince() + "--" + vo.getPayAccount().getArea().getCity());
				}
				if(Util.stringIsEmpty(crProv)){
					throw new BISException("�ϼ�ʡ��Ϊ��:" + vo.getPayeeAccount().getArea().getProvince() + "--" + vo.getPayeeAccount().getArea().getCity());
				}
				//�����¼��ʺ�
				processor.setNodeText(pathCmp + "/DbAccNo", vo.getPayAccount().getAccountNumber());
				//�����¼��˻���
				processor.setNodeText(pathCorp+"/DbAccName", vo.getPayAccount().getAccountName());
				//���ø���ʡ��
				processor.setNodeText(pathCmp + "/DbProv", dbProv);
				//�����ϼ����ʺ�
				processor.setNodeText(pathCmp + "/CrAccNo", vo.getPayeeAccount().getAccountNumber());
				//�����շ�ʡ��
				processor.setNodeText(pathCmp + "/CrProv", crProv);
			}else{
				processor.setNodeText(pathCmp+"/TrFlag", "1");
				String dbProv = ABC.getAreaCode(vo.getPayeeAccount());//�¼��˺�
				String crProv = ABC.getAreaCode(vo.getPayAccount());//�ϼ��˺�
				if(Util.stringIsEmpty(dbProv)){
					throw new BISException("�¼�ʡ��Ϊ��:" + vo.getPayeeAccount().getArea().getProvince() + "--" + vo.getPayeeAccount().getArea().getCity());
				}
				if(Util.stringIsEmpty(crProv)){
					throw new BISException("�ϼ�ʡ��Ϊ��:" + vo.getPayAccount().getArea().getProvince() + "--" + vo.getPayAccount().getArea().getCity());
				}
				//�����¼��ʺ�
				processor.setNodeText(pathCmp + "/DbAccNo", vo.getPayeeAccount().getAccountNumber());
				//�����¼��˻���
				processor.setNodeText(pathCorp+"/DbAccName", vo.getPayeeAccount().getAccountName());
				//���ø���ʡ��
				processor.setNodeText(pathCmp + "/DbProv", dbProv);
				//�����ϼ����ʺ�
				processor.setNodeText(pathCmp + "/CrAccNo", vo.getPayAccount().getAccountNumber());
				//�����շ�ʡ��
				processor.setNodeText(pathCmp + "/CrProv", crProv);
			}
			//���ñ���
			 Parameters parameters = new Parameters(Configuration.getBiConfigPath());
			 //�����˱���
			 String cur = vo.getPayAccount().getCurrencyType();
		      cur = (String)Parameters.getCurs().get(cur);
		      if (Util.stringIsEmpty(cur)) {
		        ABC.getLog().writeLog("ȡ������Ӧ�����бұ𣬰��ظ����ʺűұ�Ϊ��" + cur);
		        cur = "01";
		      }
		      processor.setNodeText(pathCmp + "/DbCur", cur);
		     //�տ��˱��� 
		      String cur1 = "";
		      String payeeCur = vo.getPayeeAccount().getCurrencyType();
		      cur1 = (String)Parameters.getCurs().get(payeeCur);
		      if (Util.stringIsEmpty(cur1)) {
		        ABC.getLog().writeLog("ȡ������Ӧ�����бұ𣬰����տ��ʺűұ�Ϊ��" + payeeCur);
		        cur1 = cur;
		      }
		      processor.setNodeText(pathCmp + "/CrCur", cur1);
		      
			tradeData.setSendData(processor.getDocText(false));
		} catch (Exception e) {
			throw new BISException("ϵͳ�������鼯���ʱ����", e);
		}
	}

}

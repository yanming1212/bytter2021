/**
 * �������
 * @author fangyb
 *
 */
package com.bytter.bis.abc;

import java.io.File;
import java.util.ArrayList;

import com.bytter.bis.BISException;
import com.bytter.bis.XMLProcessor;
import com.bytter.bis.base.BusinessBase;
import com.bytter.bis.base.PackerBase;
import com.bytter.bis.beans.TradeData;

/**
 * �������
 * @author fangyb
 *
 */
public abstract class ABCPacker extends PackerBase {
	public abstract void execute(TradeData tradeData, ArrayList businesses) throws Exception;

	public abstract void execute(TradeData tradeData, BusinessBase business) throws Exception;
	String root = "/ap";
	String reqNo="";
	// ����Ҫ��İ�
	public XMLProcessor getTemplet(TradeData tradeData) throws Exception {
		XMLProcessor templet = null;
		try {
            templet = new XMLProcessor(new File(ABC.getTempletPath().concat(
			tradeData.getTradeID().concat(".xml"))));
		} catch (Exception e) {
			throw new BISException("�����������δ�ҵ���ģ����ģ���д���" + ABC.getTempletPath(), e);
		}
		//����ʱ��
		templet.setNodeText(root + "/ReqTime", ABC.getReqTime());
		//��������
		templet.setNodeText(root + "/ReqDate", ABC.getReqDate());
		//��֤��
		templet.setNodeText(root + "/AuthNo", ABC.getAuthNo());
		//����Ա
		templet.setNodeText(root + "/OpNo", ABC.getOpNo());
		//��ҵ��
		templet.setNodeText(root + "/CorpNo", ABC.getCorpNo());
		reqNo=ABC.getReqSeqNo();
		//������ˮ��
		templet.setNodeText(root + "/ReqSeqNo",reqNo);		
		return templet;
	}
	/**
	 * ��ȡ����������
	 * @param bankName
	 * @param len
	 * @return
	 */
	public String getOpenBankName(String bankName,int len){
		if(bankName.length()<=len){
			return bankName;
		}
		int flag=-1;
		String retBankName="";
		retBankName=bankName;
		retBankName=vailStr(retBankName,"�ɷ����޹�˾","");
		retBankName=vailStr(retBankName,"�ɷݹ�˾","");
		retBankName=vailStr(retBankName,"�ع����޹�˾","");
		retBankName=vailStr(retBankName,"�عɹ�˾","");
		retBankName=vailStr(retBankName,"ʵҵ���޹�˾","");
		retBankName=vailStr(retBankName,"ʵҵ��˾","");
		retBankName=vailStr(retBankName,"Ͷ�����޹�˾","");
		retBankName=vailStr(retBankName,"Ͷ�ʹ�˾","");
		retBankName=vailStr(retBankName,"�й�����","����");
		retBankName=vailStr(retBankName,"�й�","");
		retBankName=vailStr(retBankName,"��","");
		retBankName=vailStr(retBankName,"ũҵ����","ũ��");
		retBankName=vailStr(retBankName,"��������","����");
		retBankName=vailStr(retBankName,"���ڷ�չ����","���");
		retBankName=vailStr(retBankName,"��������","����");
		retBankName=vailStr(retBankName,"��ͨ����","����");
		retBankName=vailStr(retBankName,"��������","����");
		retBankName=vailStr(retBankName,"��ҵ����","����");
		retBankName=vailStr(retBankName,"ʵҵ����","����");
		retBankName=vailStr(retBankName,"Ͷ������","����");
		if(retBankName.length()>len){
		    retBankName=retBankName.substring(0,len);
		}		
		return retBankName;
	}
	/**
	 * �ַ��滻
	 * @param soceStr
	 * @param processStr
	 * @param objStr
	 * @return
	 */
	private String vailStr(String soceStr,String processStr,String objStr){
		soceStr=soceStr.replaceFirst(processStr,objStr);		
		return soceStr;
	}
}

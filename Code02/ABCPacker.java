/**
 * 组包基类
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
 * 组包基类
 * @author fangyb
 *
 */
public abstract class ABCPacker extends PackerBase {
	public abstract void execute(TradeData tradeData, ArrayList businesses) throws Exception;

	public abstract void execute(TradeData tradeData, BusinessBase business) throws Exception;
	String root = "/ap";
	String reqNo="";
	// 返回要组的包
	public XMLProcessor getTemplet(TradeData tradeData) throws Exception {
		XMLProcessor templet = null;
		try {
            templet = new XMLProcessor(new File(ABC.getTempletPath().concat(
			tradeData.getTradeID().concat(".xml"))));
		} catch (Exception e) {
			throw new BISException("请求数据组包未找到包模板或包模板有错误：" + ABC.getTempletPath(), e);
		}
		//请求时间
		templet.setNodeText(root + "/ReqTime", ABC.getReqTime());
		//请求日期
		templet.setNodeText(root + "/ReqDate", ABC.getReqDate());
		//认证码
		templet.setNodeText(root + "/AuthNo", ABC.getAuthNo());
		//操作员
		templet.setNodeText(root + "/OpNo", ABC.getOpNo());
		//企业号
		templet.setNodeText(root + "/CorpNo", ABC.getCorpNo());
		reqNo=ABC.getReqSeqNo();
		//发起流水号
		templet.setNodeText(root + "/ReqSeqNo",reqNo);		
		return templet;
	}
	/**
	 * 获取开户行名称
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
		retBankName=vailStr(retBankName,"股份有限公司","");
		retBankName=vailStr(retBankName,"股份公司","");
		retBankName=vailStr(retBankName,"控股有限公司","");
		retBankName=vailStr(retBankName,"控股公司","");
		retBankName=vailStr(retBankName,"实业有限公司","");
		retBankName=vailStr(retBankName,"实业公司","");
		retBankName=vailStr(retBankName,"投资有限公司","");
		retBankName=vailStr(retBankName,"投资公司","");
		retBankName=vailStr(retBankName,"中国银行","中行");
		retBankName=vailStr(retBankName,"中国","");
		retBankName=vailStr(retBankName,"市","");
		retBankName=vailStr(retBankName,"农业银行","农行");
		retBankName=vailStr(retBankName,"工商银行","工行");
		retBankName=vailStr(retBankName,"深圳发展银行","深发行");
		retBankName=vailStr(retBankName,"招商银行","招行");
		retBankName=vailStr(retBankName,"交通银行","交行");
		retBankName=vailStr(retBankName,"建设银行","建行");
		retBankName=vailStr(retBankName,"商业银行","商行");
		retBankName=vailStr(retBankName,"实业银行","银行");
		retBankName=vailStr(retBankName,"投资银行","银行");
		if(retBankName.length()>len){
		    retBankName=retBankName.substring(0,len);
		}		
		return retBankName;
	}
	/**
	 * 字符替换
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

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
 * 代理支付组包类
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
			//代理账号信息
			Account parent = (Account)vo.getAccessionalInfo().get("agentPay");
			processor=getTemplet(tradeData);
			//交易流水号
			processor.setNodeText(pathRoot+"/ReqSeqNo", ABC.getCorpNo()+vo.getVoucherNo());
			DecimalFormat decf=new DecimalFormat("###0.00");
			//交易金额
			processor.setNodeText(pathRoot+"/Amt",decf.format(vo.getAmount().doubleValue()));
			//付款账号
			processor.setNodeText(pathCmp+"/DbAccNo",pay.getAccountNumber());
			//付款账号省市地区码
		    processor.setNodeText(pathCmp+"/DbProv",ABC.getAreaCode(pay));
		    //收款人帐号长度控制 
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
						  throw new BISException("收款方地区代码无法获取或为空!");
					   }
				}
			  processor.setNodeText(pathCmp + "/CrAccNo", payeeNo);
			  processor.setNodeText(pathCmp+"/CrProv",payeeAreaCode);	
			}
		    //代理账号
			processor.setNodeText(pathCorp+"/SubAccNo",parent.getAccountNumber());
			//代理账名称
			processor.setNodeText(pathCorp+"/SubAccName",parent.getAccountName());
			//加急标志
			processor.setNodeText(pathCorp+"/UrgencyFlag",vo.isUrgent()?"Y":"N");
			//跨行标志
			processor.setNodeText(pathCorp+"/OthBankFlag",vo.isDifBank()?"1":"0");
			//异地同城标志
			processor.setNodeText(pathCorp+"/OthCenterFlag",vo.isDifArea()?"0":"1");
			//收款方账户名
			processor.setNodeText(pathCorp+"/CrAccName",vo.getPayeeAccount().getAccountName());
			String payeeBankName=vo.getPayeeAccount().getOpenBranchName().trim();
			//收款方开户行名称
			processor.setNodeText(pathCorp+"/CrBankName",payeeBankName.length()<=34?payeeBankName:getOpenBankName(payeeBankName,34));
			//付款方账号
			processor.setNodeText(pathCorp+"/DbAccName",pay.getAccountName());
			//付款方账户名称
			processor.setNodeText(pathCorp+"/DbBankName",pay.getOpenBranchName());
			//备注
			String userFor=vo.getRemark();
			//String postscript = "电汇代理机构：" + parent.getAccountName() + "-" + ABC.getAreaCode(pay) + "-" + parent.getAccountNumber();
			//四川长虹财务公司 代理行：系统内财务公司名称+系统内财务公司银行账号，真实用户填写附言 2013-11-15 huangy 
			//String postscript = "代理行：" + vo.getPayAccount().getAccountName() + vo.getPayAccount().getAccountNumber() + "," + vo.getPostScript();
			
			/* 四川长虹财务公司  2013-11-21 huangy 
			 * 例：财务公司名称：四川长虹集团财务有限公司，省市代码：22，真实付款账号：240101040027720   附言：工资
				按照格式，应该是： 
				<PostscriptRes>工资，四川长虹集团财务有限公司22-240101040027720</PostscriptRes> 
				        必须为此格式，农行会检测最后18位也就是22-240101040027720，省市代码之后全为半角字符（总长度为18字节） 
				同行字符长度最大为：C68，跨行字符长度最大为：C58，超过了从左边开始截取，也就是从附言开始截取。
				*/
			//附言
			String postscript1 = new StringBuffer(vo.getPostScript()+"，" + vo.getPayAccount().getAccountName()).reverse().toString() ;
			String postscript2 = ABC.getAreaCode(pay) + "-" + vo.getPayAccount().getAccountNumber();
			//半角转换成都全角
			postscript1 = Util.charChangeHalfToFull(postscript1);
			processor.setNodeText(pathCorp+"/WhyUse",userFor==null?"电汇":userFor);
			String postscript = "";
			if(vo.isDifBank()){
				//附言58位
				postscript = postscript1.getBytes().length > 40 ? Util.splitIt(postscript1, 40) : postscript1;
				processor.setNodeText(pathCorp+"/PostscriptRes",new StringBuffer(postscript).reverse().toString()+ postscript2);
				String bankNo = (String)vo.getAccessionalInfo().get("reverse2");
				processor.setNodeText(pathCorp+"/CrBankNo",bankNo);
			}else{
				//附言68位
				postscript = postscript1.getBytes().length > 50 ? Util.splitIt(postscript1, 50) : postscript1;
				processor.setNodeText(pathCorp+"/PostscriptRes",new StringBuffer(postscript).reverse().toString() + postscript2);
			}
			tradeData.setSendData(processor.getDocText(false));
		} catch (Exception e) {
			throw new BISException("组包错误",e);
		}
	}
}

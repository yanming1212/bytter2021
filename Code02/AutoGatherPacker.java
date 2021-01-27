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
 * 自主资金归集组包类
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
			//设置交易标识为AutoGather
			tradeData.setTradeID("AutoGather");
			//获取交易模版
			processor = getTemplet(tradeData);
			Voucher vo = (Voucher) business;
			processor.setNodeText("/ap/ReqSeqNo",ABC.getCorpNo()+vo.getVoucherNo());
			//设置交易金额
			DecimalFormat decf=new DecimalFormat("###0.00");
			processor.setNodeText(pathRoot + "/Amt", decf.format(vo.getAmount().doubleValue()));
			//设置备注
			processor.setNodeText(pathCorp + "/Abstract", vo.getRemark());             
			//设置附言
			processor.setNodeText(pathCorp + "/Postscript", vo.getPostScript());         
			//上划下拔标志  0 上划  1 下拨
			if(vo.getVoucherNo().substring(3,4).equals("3")){
				processor.setNodeText(pathCmp+"/TrFlag", "1");
				String dbProv = ABC.getAreaCode(vo.getPayeeAccount());//下级账号
				String crProv = ABC.getAreaCode(vo.getPayAccount());//上级账号
				if(Util.stringIsEmpty(dbProv)){
					throw new BISException("下级省市为空:" + vo.getPayeeAccount().getArea().getProvince() + "--" + vo.getPayeeAccount().getArea().getCity());
				}
				if(Util.stringIsEmpty(crProv)){
					throw new BISException("上级省市为空:" + vo.getPayAccount().getArea().getProvince() + "--" + vo.getPayAccount().getArea().getCity());
				}
				//设置下级帐号
				processor.setNodeText(pathCmp + "/DbAccNo", vo.getPayeeAccount().getAccountNumber());
				//设置下级账户名
				processor.setNodeText(pathCorp+"/DbAccName", vo.getPayeeAccount().getAccountName());
				//设置付方省市
				processor.setNodeText(pathCmp + "/DbProv", dbProv);
				//设置上级方帐号
				processor.setNodeText(pathCmp + "/CrAccNo", vo.getPayAccount().getAccountNumber());
				//设置收方省市
				processor.setNodeText(pathCmp + "/CrProv", crProv);
			}else if(vo.getType()==null){
				processor.setNodeText(pathCmp+"/TrFlag", "0");
				String dbProv = ABC.getAreaCode(vo.getPayAccount());//下级账号
				String crProv = ABC.getAreaCode(vo.getPayeeAccount());//上级账号
				if(Util.stringIsEmpty(dbProv)){
					throw new BISException("下级省市为空:" + vo.getPayAccount().getArea().getProvince() + "--" + vo.getPayAccount().getArea().getCity());
				}
				if(Util.stringIsEmpty(crProv)){
					throw new BISException("上级省市为空:" + vo.getPayeeAccount().getArea().getProvince() + "--" + vo.getPayeeAccount().getArea().getCity());
				}
				//设置下级帐号
				processor.setNodeText(pathCmp + "/DbAccNo", vo.getPayAccount().getAccountNumber());
				//设置下级账户名
				processor.setNodeText(pathCorp+"/DbAccName", vo.getPayAccount().getAccountName());
				//设置付方省市
				processor.setNodeText(pathCmp + "/DbProv", dbProv);
				//设置上级方帐号
				processor.setNodeText(pathCmp + "/CrAccNo", vo.getPayeeAccount().getAccountNumber());
				//设置收方省市
				processor.setNodeText(pathCmp + "/CrProv", crProv);
			}else{
				processor.setNodeText(pathCmp+"/TrFlag", "1");
				String dbProv = ABC.getAreaCode(vo.getPayeeAccount());//下级账号
				String crProv = ABC.getAreaCode(vo.getPayAccount());//上级账号
				if(Util.stringIsEmpty(dbProv)){
					throw new BISException("下级省市为空:" + vo.getPayeeAccount().getArea().getProvince() + "--" + vo.getPayeeAccount().getArea().getCity());
				}
				if(Util.stringIsEmpty(crProv)){
					throw new BISException("上级省市为空:" + vo.getPayAccount().getArea().getProvince() + "--" + vo.getPayAccount().getArea().getCity());
				}
				//设置下级帐号
				processor.setNodeText(pathCmp + "/DbAccNo", vo.getPayeeAccount().getAccountNumber());
				//设置下级账户名
				processor.setNodeText(pathCorp+"/DbAccName", vo.getPayeeAccount().getAccountName());
				//设置付方省市
				processor.setNodeText(pathCmp + "/DbProv", dbProv);
				//设置上级方帐号
				processor.setNodeText(pathCmp + "/CrAccNo", vo.getPayAccount().getAccountNumber());
				//设置收方省市
				processor.setNodeText(pathCmp + "/CrProv", crProv);
			}
			//设置币种
			 Parameters parameters = new Parameters(Configuration.getBiConfigPath());
			 //付款人币种
			 String cur = vo.getPayAccount().getCurrencyType();
		      cur = (String)Parameters.getCurs().get(cur);
		      if (Util.stringIsEmpty(cur)) {
		        ABC.getLog().writeLog("取不到对应的银行币别，拜特付款帐号币别为：" + cur);
		        cur = "01";
		      }
		      processor.setNodeText(pathCmp + "/DbCur", cur);
		     //收款人币种 
		      String cur1 = "";
		      String payeeCur = vo.getPayeeAccount().getCurrencyType();
		      cur1 = (String)Parameters.getCurs().get(payeeCur);
		      if (Util.stringIsEmpty(cur1)) {
		        ABC.getLog().writeLog("取不到对应的银行币别，拜特收款帐号币别为：" + payeeCur);
		        cur1 = cur;
		      }
		      processor.setNodeText(pathCmp + "/CrCur", cur1);
		      
			tradeData.setSendData(processor.getDocText(false));
		} catch (Exception e) {
			throw new BISException("系统在自主归集组包时出错！", e);
		}
	}

}

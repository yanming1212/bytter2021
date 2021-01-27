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
 * 批量对私交易或代发工资组包类
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
			//交易流水号
			String reqNo=ABC.getCorpNo()+sumVo.getAccessionalInfo().get(Constants.DB_REVERSE1).toString().trim();
			//设置请求序号
			processor.setNodeText("/ap/ReqSeqNo",reqNo);
			//付款方省市地区码
			this.processor.setNodeText(this.root + "/Cmp/DbProv",ABC.getAreaCode(sumVo.getPayAccount()));
			//付款方账号
			this.processor.setNodeText(this.root + "/Cmp/DbAccNo",((Voucher) vouchers.get(0)).getPayAccount().getAccountNumber());
			//币别码
			this.processor.setNodeText(this.root + "/Cmp/DbCur","01");
			//上传文件名
			this.processor.setNodeText(this.root + "/Cmp/BatchFileName",((Voucher) vouchers.get(0)).getVoucherNo() + ".txt");
			//付款方账户名称
			this.processor.setNodeText(this.root + "/Corp/DbAccName",((Voucher) vouchers.get(0)).getPayAccount().getAccountName());
			//附言,备注
			this.processor.setNodeText(this.root + "/Corp/Postscript",((Voucher) vouchers.get(0)).getRemark());
			StringBuffer sb = new StringBuffer();
			double totalValue = 0;
			//指定长度数据域
			for (int i = 0; i < vouchers.size(); ++i) {
				Voucher voucher = (Voucher) vouchers.get(i);
				totalValue =  totalValue + voucher.getAmount().doubleValue();
				//序号
				sb.append(Util.padString(ABC.getCorpNo(), 20, " "));
				//收款方账户名称
				sb.append(Util.padString(voucher.getPayeeAccount().getAccountName(), 32, " "));
				//收款方账号
				sb.append(Util.padString(voucher.getPayeeAccount().getAccountNumber(),30," "));
				//交易金额
				sb.append(Util.padString(voucher.getAmount().toString(),18," "));
				//备注
				sb.append(Util.padString(voucher.getRemark(),30," "));
				sb.append("\r\n");
			}
			//交易总金额
			this.processor.setNodeText(this.root + "/Amt", totalValue + "");
			//交易总笔数
			this.processor.setNodeText(this.root + "/Corp/TotalNum",vouchers.size() + "");
			//上传文件路径
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
			throw new BISException("系统在个人报帐组包时出错", e);
		}
	}

	public void execute(TradeData tradeData, BusinessBase business)
			throws Exception {
	}
	
	
}
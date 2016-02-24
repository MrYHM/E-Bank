package com.bank.service.trade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.bank.base.BaseService;
import com.bank.dao.trade.TradeDAO;
import com.bank.model.msg.MsgModel;
import com.bank.model.trade.TradeModel;
import com.bank.model.user.UserModel;
import com.bank.service.card.UserCardService;
import com.bank.service.msg.MsgService;
import com.bank.service.user.UserService;
import com.bank.utils.RegularUtil;

@Service
public class TradeService implements BaseService<TradeModel>{

	@Autowired
	private TradeDAO td;
	@Autowired
	private UserService us;
	@Autowired
	private MsgService ms;
	@Autowired
	private UserCardService ucs;
	
	/**
	 * 余额转余额
	 * @param user
	 * @param touser
	 * @param tradeinfo
	 * @param trademoney
	 * @return
	 */
	@Transactional
	public JSONObject trade(UserModel user,String touser,String tradeinfo,double trademoney){
		JSONObject jo = new JSONObject();
		try {
			if (user.getUser_account_money()-trademoney<0) {
				jo.put("error", 203);
				jo.put("msg", "您的账户余额不足");
				return jo;
			}
			UserModel um =  us.findUserByAccoutn(touser);
			us.alertUserMoneyById(user.getUser_account_money()-trademoney, Integer.valueOf(user.getUser_id()));//本方减钱
			us.alertUserMoneyById(um.getUser_account_money()+trademoney, Integer.valueOf(um.getUser_id()));//对方加钱
			TradeModel tm = new TradeModel();
			tm.setTradeInfo(tradeinfo);
			tm.setTradeNumber(""+System.currentTimeMillis());
			tm.setTradeUserId(Integer.valueOf(user.getUser_id()));
			tm.setTradeOtherUserId(Integer.valueOf(um.getUser_id()));
			tm.setTradeState(1);//1正常2失败
			tm.setTradeType(RegularUtil.USERTRADE);//
			tm.setTradeExpendMoney(trademoney);
			tm.setTradeIncomeMoney(0);
			add(tm);//转账方添加交易记录
			tm.setTradeNumber(""+System.currentTimeMillis());
			tm.setTradeUserId(Integer.valueOf(um.getUser_id()));
			tm.setTradeOtherUserId(Integer.valueOf(user.getUser_id()));
			tm.setTradeExpendMoney(0);
			tm.setTradeIncomeMoney(trademoney);
			add(tm);//被转帐方添加交易记录
			MsgModel mm = new MsgModel();
			mm.setMsgState(false);
			mm.setMsgUserId(Integer.valueOf(um.getUser_id()));
			mm.setMsgType(3);
			mm.setMsgTitle("您收到一笔来自"+user.getUser_name()+"的款项！");
			String content = "金额共"+trademoney+"元人民币,详情请查看您的交易记录";
			if (tradeinfo!=null) {
				content +=",并且<div class='gray6'>"+user.getUser_name()+"<div>还对您说:<div class='gray6'>"+tradeinfo+"<div>";
			}
			mm.setMsgContent(content);
			ms.add(mm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jo.put("error", 200);
			jo.put("msg", "转账失败！");
			return jo;
		}
		jo.put("error", 200);
		jo.put("msg", "转账成功！");
		return jo;
	}
	
	/**
	 * 卡转余额
	 * @param user
	 * @param cardnum
	 * @param touser
	 * @param tradeinfo
	 * @param trademoney
	 * @return
	 */
	@Transactional
	public JSONObject trade(UserModel user,String cardnum,String touser,String tradeinfo,double trademoney){
		JSONObject jo = new JSONObject();
		try {
			double oldmoney  = ucs.selectCardBalanceById(cardnum);//查询该卡余额
			if (oldmoney-trademoney<0) {
				jo.put("error", 203);
				jo.put("msg", "您的账户余额不足");
				return jo;
			}
			UserModel um =  us.findUserByAccoutn(touser);
			ucs.alertCardBalanceById(oldmoney-trademoney, cardnum);//本方卡减钱
			us.alertUserMoneyById(um.getUser_account_money()+trademoney, Integer.valueOf(um.getUser_id()));//对方加钱
			TradeModel tm = new TradeModel();
			tm.setTradeInfo(tradeinfo);
			tm.setTradeNumber(""+System.currentTimeMillis());
			tm.setTradeUserId(Integer.valueOf(user.getUser_id()));
			tm.setTradeOtherUserId(Integer.valueOf(um.getUser_id()));
			tm.setTradeState(1);//1正常2失败
			tm.setTradeType(RegularUtil.USERTRADE);//
			tm.setTradeExpendMoney(trademoney);
			tm.setTradeIncomeMoney(0);
			tm.setTradeUserCard(cardnum);
			add(tm);//转账方添加交易记录
			tm.setTradeNumber(""+System.currentTimeMillis());
			tm.setTradeUserId(Integer.valueOf(um.getUser_id()));
			tm.setTradeOtherUserId(Integer.valueOf(user.getUser_id()));
			tm.setTradeExpendMoney(0);
			tm.setTradeIncomeMoney(trademoney);
			add(tm);//被转帐方添加交易记录
			MsgModel mm = new MsgModel();
			mm.setMsgState(false);
			mm.setMsgUserId(Integer.valueOf(um.getUser_id()));
			mm.setMsgType(3);
			mm.setMsgTitle("您收到一笔来自"+user.getUser_name()+"的款项！");
			String content = "金额共"+trademoney+"元人民币,详情请查看您的交易记录";
			if (tradeinfo!=null) {
				content +=",并且<div class='gray6'>"+user.getUser_name()+"<div>还对您说:<div class='gray6'>"+tradeinfo+"<div>";
			}
			mm.setMsgContent(content);
			ms.add(mm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jo.put("error", 200);
			jo.put("msg", "转账失败！");
			return jo;
		}
		jo.put("error", 200);
		jo.put("msg", "转账成功！");
		return jo;
	}
	
	/**
	 * 转账到银行卡(卡转卡)
	 * @param user
	 * @param usercardnum
	 * @param tocardnum
	 * @param tradeinfo
	 * @param trademoney
	 * @return
	 */
	@Transactional
	public JSONObject tradeCard(UserModel user,String usercardnum,String tocardnum,String tradeinfo,double trademoney){
		JSONObject jo = new JSONObject();
		try {
			double usercardmoney  = ucs.selectCardBalanceById(usercardnum);//查询转账卡余额
			if (usercardmoney-trademoney<0) {
				jo.put("error", 203);
				jo.put("msg", "您的账户余额不足");
				return jo;
			}
			double tocardmoney  = ucs.selectCardBalanceById(tocardnum);//查询被转账卡余额
			ucs.alertCardBalanceById(tocardmoney+trademoney, tocardnum);//被转帐方加钱
			ucs.alertCardBalanceById(usercardmoney-trademoney, usercardnum);//本方卡减钱
			UserModel um =  us.findUserByCardNum(tocardnum);//根据卡号找到被转账方
			TradeModel tm = new TradeModel();
			tm.setTradeInfo(tradeinfo);
			tm.setTradeNumber(""+System.currentTimeMillis());
			tm.setTradeUserId(Integer.valueOf(user.getUser_id()));
			tm.setTradeOtherUserId(Integer.valueOf(um.getUser_id()));
			tm.setTradeState(1);//1正常2失败
			tm.setTradeType(RegularUtil.USERTRADE);//
			tm.setTradeExpendMoney(trademoney);
			tm.setTradeIncomeMoney(0);
			tm.setTradeUserCard(usercardnum);
			tm.setTradeOtherCard(tocardnum);
			add(tm);//转账方添加交易记录
			tm.setTradeNumber(""+System.currentTimeMillis());
			tm.setTradeUserId(Integer.valueOf(um.getUser_id()));
			tm.setTradeOtherUserId(Integer.valueOf(user.getUser_id()));
			tm.setTradeExpendMoney(0);
			tm.setTradeIncomeMoney(trademoney);
			tm.setTradeUserCard(tocardnum);
			tm.setTradeOtherCard(usercardnum);
			add(tm);//被转帐方添加交易记录
			MsgModel mm = new MsgModel();
			mm.setMsgState(false);
			mm.setMsgUserId(Integer.valueOf(um.getUser_id()));
			mm.setMsgType(3);
			mm.setMsgTitle("您收到一笔来自"+user.getUser_name()+"的款项！");
			String content = "金额共"+trademoney+"元人民币,详情请查看您的交易记录";
			if (tradeinfo!=null) {
				content +=",并且<div class='gray6'>"+user.getUser_name()+"<div>还对您说:<div class='gray6'>"+tradeinfo+"<div>";
			}
			mm.setMsgContent(content);
			ms.add(mm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jo.put("error", 200);
			jo.put("msg", "转账失败！");
			return jo;
		}
		jo.put("error", 200);
		jo.put("msg", "转账成功！");
		return jo;
	}
	
	/**
	 * 转账到银行卡(余额转卡)
	 * @param user
	 * @param usercardnum
	 * @param tocardnum
	 * @param tradeinfo
	 * @param trademoney
	 * @return
	 */
	@Transactional
	public JSONObject tradeCard(UserModel user,String tocardnum,String tradeinfo,double trademoney){
		JSONObject jo = new JSONObject();
		try {
			double usercardmoney  = user.getUser_account_money();//查询用户
			if (usercardmoney-trademoney<0) {
				jo.put("error", 203);
				jo.put("msg", "您的账户余额不足");
				return jo;
			}
			double tocardmoney  = ucs.selectCardBalanceById(tocardnum);//查询被转账卡余额
			ucs.alertCardBalanceById(tocardmoney+trademoney, tocardnum);//被转帐方加钱
			us.alertUserMoneyById(usercardmoney-trademoney, Integer.valueOf(user.getUser_id()));//本方卡减钱
			UserModel um =  us.findUserByCardNum(tocardnum);//根据卡号找到被转账方
			TradeModel tm = new TradeModel();
			tm.setTradeInfo(tradeinfo);
			tm.setTradeNumber(""+System.currentTimeMillis());
			tm.setTradeUserId(Integer.valueOf(user.getUser_id()));
			tm.setTradeOtherUserId(Integer.valueOf(um.getUser_id()));
			tm.setTradeState(1);//1正常2失败
			tm.setTradeType(RegularUtil.USERTRADE);//
			tm.setTradeExpendMoney(trademoney);
			tm.setTradeIncomeMoney(0);
			tm.setTradeOtherCard(tocardnum);
			add(tm);//转账方添加交易记录
			tm.setTradeNumber(""+System.currentTimeMillis());
			tm.setTradeUserId(Integer.valueOf(um.getUser_id()));
			tm.setTradeOtherUserId(Integer.valueOf(user.getUser_id()));
			tm.setTradeExpendMoney(0);
			tm.setTradeIncomeMoney(trademoney);
			tm.setTradeUserCard(tocardnum);
			add(tm);//被转帐方添加交易记录
			MsgModel mm = new MsgModel();
			mm.setMsgState(false);
			mm.setMsgUserId(Integer.valueOf(um.getUser_id()));
			mm.setMsgType(3);
			mm.setMsgTitle("您收到一笔来自"+user.getUser_name()+"的款项！");
			String content = "金额共"+trademoney+"元人民币,详情请查看您的交易记录";
			if (tradeinfo!=null) {
				content +=",并且<div class='gray6'>"+user.getUser_name()+"<div>还对您说:<div class='gray6'>"+tradeinfo+"<div>";
			}
			mm.setMsgContent(content);
			ms.add(mm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jo.put("error", 200);
			jo.put("msg", "转账失败！");
			return jo;
		}
		jo.put("error", 200);
		jo.put("msg", "转账成功！");
		return jo;
	}
	
	/**
	 * 转出或转入
	 * @param ebaooryuebao 转 出/入 到 余额宝(false)/余额(true)
	 * @param corborm 金额来源 银行卡/余额/余额宝
	 * @param trademoney 金额
	 * @param tradeinfo 备注
	 * @return
	 */
	@Transactional
	public JSONObject tradeIn(UserModel user,boolean ebaooryuebao,boolean corborm,String cardnum,double trademoney,String tradeinfo){
		int uid = Integer.valueOf(user.getUser_id());
		JSONObject jo = new JSONObject();
			//转出
			if (ebaooryuebao) {
				//转出到余额
				if (corborm) {
					//使用余额宝
					if (user.getUser_account_balance()-trademoney<0) {
						jo.put("error", 203);
						jo.put("msg", "您的余额宝余额不足");
						return jo;
					}
					us.alertUserBalanceById(user.getUser_account_balance()-trademoney, uid);
					us.alertUserMoneyById(user.getUser_account_money()+trademoney, uid);
					TradeModel tm = new TradeModel();
					tm.setTradeExpendMoney(0);
					tm.setTradeIncomeMoney(0);
					tm.setTradeInfo(tradeinfo);
					tm.setTradeNumber(System.currentTimeMillis()+"");
					
				}else{
					//使用银行卡
					double cardmoney = ucs.selectCardBalanceById(cardnum);
					if (cardmoney-trademoney<0) {
						jo.put("error", 203);
						jo.put("msg", "该银行卡余额不足");
						return jo;
					}
					ucs.alertCardBalanceById(cardmoney-trademoney, cardnum);
					us.alertUserMoneyById(user.getUser_account_money()+trademoney, uid);
				}
				
			} else {
				//转出到余额宝
				if (corborm) {
					//使用余额
					if (user.getUser_account_money()-trademoney<0) {
						jo.put("error", 203);
						jo.put("msg", "您的E宝余额不足");
						return jo;
					}
					us.alertUserBalanceById(user.getUser_account_balance()+trademoney, uid);
					us.alertUserMoneyById(user.getUser_account_money()-trademoney, uid);
				}else{
					
					//使用银行卡
					double cardmoney = ucs.selectCardBalanceById(cardnum);
					if (cardmoney-trademoney<0) {
						jo.put("error", 203);
						jo.put("msg", "该银行卡余额不足");
						return jo;
					}
					us.alertUserBalanceById(user.getUser_account_balance()+trademoney, uid);
					ucs.alertCardBalanceById(cardmoney-trademoney, cardnum);
				}
			}
		jo.put("error", 200);
		jo.put("msg", "转账成功");
		return jo;
	}
	
	/**
	 * 转出
	 * @param user
	 * @param ebaooryuebao 余额宝(false)/余额(true)
	 * @param cardnum
	 * @param trademoney
	 * @param tradeinfo
	 * @return
	 */
	public JSONObject tradeOut(UserModel user, boolean ebaooryuebao, String cardnum, double trademoney,
			String tradeinfo) {
		int uid = Integer.valueOf(user.getUser_id());
		JSONObject jo = new JSONObject();
		if (ebaooryuebao) {
			//从余额宝转到卡
			if (user.getUser_account_balance()-trademoney<0) {
				jo.put("error", 203);
				jo.put("msg", "该余额宝余额不足");
				return jo;
			}
			us.alertUserBalanceById(user.getUser_account_balance()-trademoney, uid);
			ucs.alertCardBalanceById(ucs.selectCardBalanceById(cardnum)+trademoney, cardnum);
		}else{
			//从余额转到卡
			if (user.getUser_account_money()-trademoney<0) {
				jo.put("error", 203);
				jo.put("msg", "您的E宝余额不足");
				return jo;
			}
			us.alertUserMoneyById(user.getUser_account_money()-trademoney, uid);
			ucs.alertCardBalanceById(ucs.selectCardBalanceById(cardnum)+trademoney, cardnum);
			
			
		}
		jo.put("error", 200);
		jo.put("msg", "转账成功");
		return jo;
	}
	@Override
	public int add(TradeModel model) {
		// TODO Auto-generated method stub
		return td.insert(model);
	}

	@Override
	public int RemoveById(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int alterById(TradeModel model) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TradeModel findById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TradeModel> findAll() {
		// TODO Auto-generated method stub
		return null;
	}


}
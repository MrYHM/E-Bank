package com.bank.dao.user;

import com.bank.base.BaseDAO;
import com.bank.model.user.UserModel;

public interface UserDAO extends BaseDAO<UserModel>{

	public int insert_first_step(UserModel user);
	public UserModel selectUserByIdCard(String idcard);
}
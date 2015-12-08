<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	// 获得项目完全路径（假设你的项目叫MyApp，那么获得到的地址就是 http://localhost:8080/MyApp/）:
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
		<title>我的银行</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link href="page/assets/css/dpl-min.css" rel="stylesheet" type="text/css" />
		<link href="page/assets/css/bui-min.css" rel="stylesheet" type="text/css" />
		<link href="page/assets/css/main-min.css" rel="stylesheet" type="text/css" />
<base href=" <%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
<div class="header">
			<div class="dl-title">
				<!--<img src="/chinapost/Public/assets/img/top.png">-->
			</div>

			<div class="dl-log">欢迎您，<span class="dl-log-user">root</span><a href="/chinapost/index.php?m=Public&a=logout" title="退出系统" class="dl-log-quit">[退出]</a>
			</div>
		</div>
		<div class="content">
			<div class="dl-main-nav">
				<div class="dl-inform">
					<div class="dl-inform-title"><s class="dl-inform-icon dl-up"></s></div>
				</div>
				<ul id="J_Nav" class="nav-list ks-clear">
					<li class="nav-item dl-selected">
						<div class="nav-item-inner nav-home">我的账户</div>
					</li>
					<li class="nav-item dl-selected">
					<div class="nav-item-inner nav-order">转账汇款</div>
					<li class="nav-item dl-selected">
					<div class="nav-item-inner nav-order">存款贷款</div>
					<li class="nav-item dl-selected">
					<div class="nav-item-inner nav-order">个人信息</div>
					<li class="nav-item dl-selected">
					<div class="nav-item-inner nav-order">关于本行</div>
				</ul>
			</div>
			<ul id="J_NavContent" class="dl-tab-conten">

			</ul>
		</div>
		<script type="text/javascript" src="page/assets/js/jquery-1.8.1.min.js"></script>
		<script type="text/javascript" src="page/assets/js/bui-min.js"></script>
		<script type="text/javascript" src="page/assets/js/common/main-min.js"></script>
		<script type="text/javascript" src="page/assets/js/config-min.js"></script>
		<script>
			BUI.use('common/main', function() {
				var config = [{
					id: '1',
					menu: [{
						text: '我的账户',
						items: [{
							id: '12',
							text: '账户列表',
							href: 'dynamic/activityMng.html'
						}, {
							id: '3',
							text: '账户查询',
							href: 'message/sysInfo.html'
						}, {
							id: '4',
							text: '申请账户',
							href: 'dynamic/jobMng.html'
						}, {
							id: '6',
							text: '挂失账户',
							href: 'dynamic/commentMng.html'
						}, {
							id: '8',
							text: '注销账户',
							href: 'dynamic/dynamicMng.html'
						}]
					}]
				}, {
					id: '7',
					homePage: '9',
					menu: [{
						text: '转账汇款',
						items: [{
							id: '9',
							text: '注册账户转账',
							href: 'enterprise/index.html'
						},{
							id: '9',
							text: '境内跨行转账',
							href: 'enterprise/index.html'
						},{
							id: '9',
							text: '转账汇款查询',
							href: 'enterprise/index.html'
						},{
							id: '9',
							text: '缴费',
							href: 'enterprise/index.html'
						}]
					}]
				}, {
					id: '15',
					homePage: '16',
					menu: [{
						text: '存款贷款',
						items: [{
							id: '16',
							text: '定期存款',
							href: 'student/index.html'
						},{
							id: '16',
							text: '贷款',
							href: 'student/index.html'
						}]
					}]
				}, {
					id: '17',
					homePage: '18',
					menu: [{
						text: '个人信息',
						items: [{
							id: '18',
							text: '查看个人信息',
							href: 'course/index.html'
						}, {
							id: '19',
							text: '修改个人信息',
							href: 'course/addCourse.html'
						}]
					}]
				}, {
					id: '20',
					homePage: '21',
					menu: [{
						text: '虚拟学生管理',
						items: [{
							id: '21',
							text: '学生管理',
							href: 'virtualstudent/index.html'
						}, {
							id: '23',
							text: '学生注册',
							href: 'virtualstudent/studentRegister.html'
						}]
					}]
				}, {
					id: '29',
					homePage: '31',
					menu: [{
						text: '虚拟企业管理',
						items: [{
							id: '31',
							text: '职位列表',
							href: 'virtualenterprise/jobList.html'
						}]
					}]
				}];
				new PageUtil.MainPage({
					modulesConfig: config
				});
			});
		</script>
		<div style="text-align:center;">
			<p></p>
		</div>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	// 获得项目完全路径（假设你的项目叫MyApp，那么获得到的地址就是 http://localhost:8080/MyApp/）:
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML>
<html>
<head>
<base href=" <%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="shortcut icon" type="image/x-icon" href="page/assets//img/tubiao.ico" /> 
<link href="page/assets/css/bootstrap.css" rel="stylesheet">
<link rel="stylesheet" href="page/assets/css/bootstrapValidator.min.css"/>
<title>注册</title>
<style type="text/css">
label{
	letter-spacing: 3px;
}
.btncolor{
	color:#CCC;
	background-color: #3f316d;
}
#go{
	opacity: 0.0;
}
</style>
</head>
<body style="background-image: url('page/assets/img/bg_grid.png');">
	<!-- 头部导航 -->
	<jsp:include page="../head_foot/head.jsp"></jsp:include>
	
	<div class="container">
		<ol class="breadcrumb">
			<li style="color: #3f316d">填写信息</li>
			<li class="active">完善信息</li>
			<li class="active">确认信息</li>
		</ol>
		<div id="regdiv" class="row">
			<div class="col-sm-4 col-sm-offset-4">
				<form id="regform" action="user/regfirst.action" method="post">
					<div id="user_name" class="form-group">
						<label for="user_name">姓名</label> 
						<input type="text" class="form-control" id="exampleInputName2"
							name = "user_name" placeholder="请输入本人姓名">
					</div>
					<div id="user_card_type" class="form-group">
						<label for="exampleInputName2">证件类型</label> 
						<select class="form-control">
							<option>身份证</option>
						</select>
					</div>
					<div id="user_idcard" class="form-group">
						<label for="user_idcard">证件号码</label> 
						<input type="text" class="form-control" id="exampleInputName2"
							name = "user_idcard" placeholder="请输入证件号码">
					</div>
					<div id="user_phone" class="form-group">
						<label for="exampleInputName2">手机号码</label> 
							<input type="text" class="form-control" id="exampleInputName2"
							name = "user_phone" placeholder="请输入手机号码">
					</div>
					<div id="go" class="form-group" align="center">
						<div class="row">
							<div class="col-sm-3"></div>
							<div class="col-sm-2">
								<button type="submit" class="btn btn-default btncolor">下一步</button>
							</div>
							<div class="col-sm-1"></div>
							<div class="col-sm-1">
								<button type="reset" class="btn btn-default btncolor">重置</button>
							</div>
							<div class="col-sm-5"></div>
						</div>
					</div>
				</form>
			</div>		
		</div>
	</div>


	<div id="isSuc" class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog"
		aria-labelledby="mySmallModalLabel">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
						<h4 class="modal-title">提示</h4>
				</div>
				<div class="modal-body">
						<p>注册失败！</p>
				</div>
			</div>
		</div>
	</div>
	<script src="page/assets/js/jquery-1.8.1.min.js"></script>
	<!-- foot -->
	<jsp:include page="../head_foot/foot.jsp"></jsp:include>
	<script src="page/assets/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="page/assets/js/bootstrapValidator.min.js"></script> 
	<script src="page/assets/js/velocity.min.js"></script>
	<script src="page/assets/js/velocity.ui.min.js"></script>
	<script type="text/javascript">
	$(document).ready(function() {
	    $('#regform').bootstrapValidator({
	        message: 'This value is not valid',
	        submitButtons: 'button[type="submit"]',
	        feedbackIcons: {
	            valid: 'glyphicon glyphicon-ok',
	            invalid: 'glyphicon glyphicon-remove',
	            validating: 'glyphicon glyphicon-refresh'
	        },
	        fields: {
	            user_name: {
	                validators: {
	                    notEmpty: {
	                        message: '用户名不能为空'
	                    },
	                }
	            },
	            user_idcard: {
	            	trigger:"blur",
	                validators: {
	                    notEmpty: {
	                        message: '身份证信息不能为空'
	                    },
	                    remote: {
                            url: 'user/verifyAccount.action',
                            type: "post",
                            async: true,
                            data:
                            {
                            	user_account: function(validator)
                                {
                                    return $('#regform :input[name="user_idcard"]').val();
                                },
                                accountType : 0
                            },
                        },
	                }
	            },
	            user_phone: {
	            	threshold:10,
	                validators: {
	                    notEmpty: {
	                        message: '手机号码不能为空'
	                    },
	                    remote: {
                            url: 'user/verifyAccount.action',
                            type: "post",
                            async: true,
                            data:
                            {
                            	user_account: function(validator)
                                {
                                    return $('#regform :input[name="user_phone"]').val();
                                },
                                accountType : 2
                            },
                        },
	                }
	            },

	        }
	    }).on('success.form.bv', function(e) {
            // Prevent form submission
            e.preventDefault();

            // Get the form instance
            var $form = $(e.target);

            // Get the BootstrapValidator instance
            var bv = $form.data('bootstrapValidator');

            // Use Ajax to submit form data
            $.post($form.attr('action'), $form.serialize(), function(result) {
                console.log(result);
				if (result.error==202) {
	                $("#isSuc").modal(); 
				}else{
					location.href = "page/regist/regist_2.jsp";
				}
					
            }, 'json');
        });
	});
	//出场动画
	$(function(){
		var seqInit=[{
	        elements:$("#regdiv"),
	        properties:'transition.bounceLeftIn',
	        options:{
	        	delay:10
	        }
	    },{
	        elements:$("#go"),
	        properties:'transition.bounceLeftIn',
	        options:{
	        	delay:10
	        }
	    },];
		 $.Velocity.RunSequence(seqInit);
		})
	
	//模态框居中
		function centerModals() {
			$('.modal').each(
					function(i) {
						var $clone = $(this).clone().css('display', 'block')
								.appendTo('body');
						var top = Math.round(($clone.height() - $clone.find(
								'.modal-content').height()) / 3);
						top = top > 0 ? top : 0;
						$clone.remove();
						$(this).find('.modal-content').css("margin-top", top);
					});
		}
		$('.modal').on('show.bs.modal', centerModals);
		$(window).on('resize', centerModals);
	</script>
</body>
</html>
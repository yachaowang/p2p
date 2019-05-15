
//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}

//注册协议确认
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});
});

//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}



function checkPhone() {
	//验证手机号
	var phone = $.trim($("#phone").val());
	var flag = true;
	if(""==phone){
		showError("phone","请输入手机号");
	}else if(!/^1[1-9]\d{9}$/.test(phone)){
		showError("phone","请输入正确的手机号");
		return false;
	}else{
		$.ajax({
			url:"loan/checkPhone",
			type:"post",
			async:false,
			data:"phone="+phone,
			success:function (jsonObject) {
				if(jsonObject.errorMessage == "OK"){
					showSuccess("phone");
					flag = true;
				}else{
					showError("phone",jsonObject.errorMessage)
				}
			},
			error:function () {
				showError("phone","系统繁忙...");
				flag = false;
			}
		});
	}
	return flag;
}
//验证登录密码
function checkLoginPassword() {
	var loginPassword =  $.trim($("#loginPassword").val());
	var replayLoginPassword = $.trim($("#replayLoginPassword").val());

	if(""==loginPassword){
		showError("loginPassword","请输入密码");
		return false;
	}else if(!/^[0-9a-zA-Z]+$/.test(loginPassword)){
		showError("loginPassword","密码字符只可使用数字和大小写英文字母");
		return false;

	}else if(!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)){
		showError("loginPassword", "密码应同时包含英文和数字");
		return false;
	}else if(loginPassword.length<6 || loginPassword.length>20){
		showError("loginPassword","密码长度应为6-20");
		return false;
	}else {
		showSuccess("loginPassword");
	}
	if (replayLoginPassword != "" && loginPassword=="") {
		showError("replayLoginPassword","请先输入密码");
	}
	return true;
}
//验证确认密码
function checkLoginPasswordEqu() {
	var loginPassword =  $.trim($("#loginPassword").val());
	var replayLoginPassword = $.trim($("#replayLoginPassword").val());

	if(""==loginPassword){
		showError("loginPassword","请先输入密码");
		return false;
	}else if(replayLoginPassword!=loginPassword){
		showError("replayLoginPassword","两次密码输入不一致")
		return false;
	}else{
		showSuccess("replayLoginPassword")
	}
	return true;
}
//验证图片验证码
function checkCaptcha() {
	var captcha = $.trim($("#captcha").val());
	var flag = true;
	if(""==captcha){
		showError("captcha","请输入验证码");
		return false;
	}else{
		$.ajax({
			url:"loan/checkCaptcha",
			type:"get",
			data:{"captcha":captcha},
			async:false,
			success:function (jsonObject) {
				if(jsonObject.errorMessage=="OK"){
					showSuccess("captcha");
					flag = true;
				}else{
					showError("captcha",jsonObject.errorMessage);
					flag = false;
				}
			},
			error:function () {
				showError("captcha","系统繁忙...")
				flag = false;
			}
		});
	}
	return flag;
}
//注册
function register() {
	var phone = $.trim($("#phone").val());
	var loginPassword = $.trim($("#loginPassword").val());
	var replayLoginPassword = $.trim($("#replayLoginPassword").val());

	if(checkPhone() && checkLoginPassword() && checkLoginPasswordEqu() && checkCaptcha()){
		$("#loginPassword").val($.md5(loginPassword));
		$("#replayLoginPassword").val($.md5(replayLoginPassword));

		$.ajax({
			url:"loan/register",
			type:"post",
			data:"phone="+phone+"&loginPassword="+$.md5(loginPassword),
			success:function (jsonObject) {
				if (jsonObject.errorMessage == "OK") {
					window.location.href = "realName.jsp";
				} else {
					showError("captcha","注册失败,请稍后重试...");
				}
			},
			error:function () {
				showError("captcha","系统繁忙,请稍后重试...");

			}
		});
	}
}
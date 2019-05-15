
//同意实名认证协议
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

//验证姓名
function userRealName() {
	var realName = $.trim($("#realName").val());
	if(""==realName){
		showError("realName","请填写真实姓名");
		return false;
	}else if(!/^[\u4e00-\u9fa5]{0,}$/.test(realName)){
		showError("realName","真实姓名只支持中文");
		return false;
	}else{
		showSuccess("realName");
		return true;
	}
}
//验证身份证号
function idCardCheck() {
	var idCard = $.trim($("#idCard").val());

	if(""==idCard){
		showError("idCard","请输入身份证号");
		return false;
	}else if(!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)){
		showError("idCard","请输入正确的身份证号码");
		return false;
	}else{
		showSuccess("idCard");

	}
	return true;

}
//确认身份证号码验证
function idCardEequ() {
	var idCard = $.trim($("#idCard").val());
	var replayIdCard = $.trim($("#replayIdCard").val());
	if(""==replayIdCard){
		showError("replayIdCard","请输入确认身份证号码");
		return false;

	}else if(idCard=="" && replayIdCard !=""){

		showError("replayIdCard","请先输入身份证号码");
		return false;
	}else if(replayIdCard != idCard) {
		showError("replayIdCard", "确认身份证号码与身份证号码不一致");
		return false;

	}else {
		showSuccess("replayIdCard");

	}
	return true;

}
//验证图片验证码
function checkCaptcha() {
	var captcha = $.trim($("#captcha").val());
	var flag = true;

	if ("" == captcha) {
		showError("captcha", "请输入图形验证码");
		return false;
	} else {
		$.ajax({
			url:"loan/checkCaptcha",
			type:"get",
			async:false,
			data:{
				"captcha":captcha
			},
			success:function (jsonObject) {
				if (jsonObject.errorMessage == "OK") {
					showSuccess("captcha");
					flag = true;
				} else {
					showError("captcha",jsonObject.errorMessage);
					flag = false;
				}
			},
			error:function () {
				showError("captcha","系统繁忙,请稍后重试...");
				flag = false;
			}
		});
	}

	return flag;
}
//验证认证
function verifyRealName() {
	var realName = $.trim($("#realName").val());
	var idCard = $.trim($("#idCard").val());
	if(userRealName() && idCardCheck() && idCardEequ() && checkCaptcha()){
		$.ajax({
			url:"loan/verifyRealName",
			type:"post",
			data:"idCard="+idCard+"&realName="+realName,
			success:function (jsonObject) {
				if(jsonObject.errorMessage=="OK"){
					window.location.href="index";

				}else{
					showError("captcha","您输入的信息不匹配,请重新输");
				}
			},
			error:function () {
				showError("captcha","系统繁忙,请稍后重试...");
			}
		});
	}
}

//验证认证
function back() {
	var name = $.trim($("#name").val());
	var phone = $.trim($("#phone").val());
	var idCard = $.trim($("#idCard").val());
	var backId = $.trim($("#backId").val());
	if((name && phone && idCard && backId)!=""){
		$.ajax({
			url:"loan/back",
			type:"post",
			data:"name="+name+"&phone="+phone+"&idCard="+idCard+"&backId="+backId,
			success:function (jsonObject) {
				if(jsonObject.errorMessage=="OK"){

					window.location.href="show.jsp";

				}else if(jsonObject.errorMessage=="false"){
					window.location.href="show2.jsp";
				}

			},
			error:function () {
				showError("captcha","系统繁忙,请稍后重试...");
			}
		});
	}
}
var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}

//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode == 13){
		login();
	}
});

$(function () {
	loadStat();
	$("#dateBtn1").on("click",function () {
        var _this=$(this);
        var phone = $.trim($("#phone").val());
        if($("#dateBtn1").html()!="获取验证码"){
            return false;
        }
        if(""==phone){
            $("#showId").html("请输入手机号");
            return false;
        } else{
            $.ajax({
                url:"loan/messageCode",
                type:"post",
                data:"phone="+phone,
                success:function (jsonObject) {
                    if(jsonObject.errorMessage=="OK"){
                        alert("您的短信验证码是:" + jsonObject.randomNumber);
                        if(!$(this).hasClass("on")){
                            $.leftTime(60,function(d){
                                if(d.status){
                                    _this.addClass("on");
                                    _this.html((d.s=="00"?"60":d.s)+"秒后重新获取");
                                }else{
                                    _this.removeClass("on");
                                    _this.html("获取验证码");
                                }
                            });
                        }else{
                            $("#showId").html("短信验证码获取失败");
                        }
                    }
                },
                error:function () {
                    $("#showId").html("系统繁忙，请稍后重试...");
                }
            });

        }
    });
});

function loadStat() {
	$.ajax({
		url:"loan/loadStat",
		type:"get",
		success:function (jsonObject) {
			$(".historyAverageRate").html(jsonObject.historyAverageRate);
			$("#allUserCount").html(jsonObject.allUserCount);
			$("#allBidMoney").html(jsonObject.allBidMoney);
		}
	});
}

//验证账号（手机号）
function checkPhone() {
	var phone = $.trim($("#phone").val());
	if(""==phone){
		$("#showId").html("请输入手机号");
		return false;
	}else if(!/^1[1-9]\d{9}$/.test(phone)){
		$("#showId").html("手机号输入错误");
		return false;
	}else{
		$("#showId").html("");
		return true;
	}
	
}
//验证登录密码
function checkLoginPassword() {
	var loginPassword = $.trim($("#loginPassword").val());
	if(""==loginPassword){
		$("#showId").html("请输入密码");
		return false;
	}else{
		$("#showId").html("");
		return true;
	}
}

function checkMessageCode() {
    var messageCode = $.trim($("#messageCode").val());

    if ("" == messageCode) {
        $("#showId").html("请输入短信验证码");
        return false;
    } else {
        $("#showId").html("");
    }
    return true;
}

//验证登录
function login() {
    var phone = $.trim($("#phone").val());
    var loginPassword = $.trim($("#loginPassword").val());
    var messageCode = $.trim($("#messageCode").val());

    if (checkPhone() && checkLoginPassword() && checkMessageCode()) {
        $("#loginPassword").val($.md5(loginPassword));
        $.ajax({
            url:"loan/login",
            type:"post",
            data:"phone="+phone+"&loginPassword="+$.md5(loginPassword)+"&messageCode="+messageCode,
            success:function (jsonObject) {
                if (jsonObject.errorMessage == "OK") {
                    window.location.href = referrer;
                } else {
                    $("#showId").html(jsonObject.errorMessage);
                }
            },
            error:function () {
                $("#showId").html("系统繁忙,请稍后重试...");
            }
        });

    }

}














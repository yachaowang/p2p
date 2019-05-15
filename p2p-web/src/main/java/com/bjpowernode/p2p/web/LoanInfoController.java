package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import com.bjpowernode.p2p.service.vo.BidUserTop;
import com.bjpowernode.p2p.service.vo.PaginationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName LoanInfoController
 * @Description
 * @Version 1.0
 * @Date 2019/3/26 19:55
 * @Author wyc
 **/
@Controller
public class LoanInfoController {

    @Autowired
    private LoanInfoService loanInfoService;

    @Autowired
    private BidInfoService bidInfoService;

    @Autowired
    private FinanceAccountService financeAccountService;

    @RequestMapping(value="/loan/loan")
    public String loan(HttpServletResponse request, Model model,
                       @RequestParam (value = "ptype",required = false)Integer ptype,
                       @RequestParam (value = "currentPage",required = false)Integer currentPage){

        //判断是否为首页
        if(null==currentPage){
            currentPage=1;
        }
        //准备分页参数
        Map<String,Object> paramMap = new HashMap<>();
        //判断产品类型是否有值
        if(null != ptype){
            paramMap.put("productType",ptype);
        }
        int pageSize = 9;

        paramMap.put("currentPage",(currentPage - 1)*pageSize);

        paramMap.put("pageSize",pageSize);

        //分页查询产品信息列表(产品类型,页码,每页显示条数) -> 返回:每页展示的产品,总条数,创建一个分页模型对象
        PaginationVO<LoanInfo> paginationVO = loanInfoService.queryLoanInfoByPage(paramMap);

        //计算总页数
        int totalPage = paginationVO.getTotal().intValue() / pageSize;
        //取余
        int mod = paginationVO.getTotal().intValue() % pageSize;
        if (mod > 0) {
            totalPage = totalPage + 1;
        }
        //获取用户投资排行榜
        List<BidUserTop> bidUserTopList = bidInfoService.queryBidUserTop();

        model.addAttribute("bidUserTopList",bidUserTopList);
        model.addAttribute("loanInfoList",paginationVO.getDataList());
        model.addAttribute("totalRows",paginationVO.getTotal());
        model.addAttribute("totalPage",totalPage);
        model.addAttribute("currentPage",currentPage);
        if (null != ptype) {
            model.addAttribute("ptype",ptype);
        }
        return "loan";
    }

    @RequestMapping(value = "/loan/loanInfo")
    public String loanInfo(HttpServletRequest request,Model model,
                           @RequestParam(value = "id",required = true)Integer id){

        //根据产品标识获取产品详情
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(id);
        //根据产品标识查询产品的所有投资记录
        List<BidInfo> bidInfoList = bidInfoService.queryBidInfoListByLoanId(id);

        //从session中获取用户信息
        User sesssionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        //判断用户是否登录
        if(null != sesssionUser){
            //根据用户标识获取用户的帐户资金信息
            FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sesssionUser.getId());
            model.addAttribute("financeAccount",financeAccount);
        }

        //根据用户标识获取用户的帐户资金信息
        model.addAttribute("loanInfo",loanInfo);
        model.addAttribute("bidInfoList",bidInfoList);

        return "loanInfo";

    }

}
package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    private LoanInfoService loanInfoService;

    @Autowired
    private UserService userService;


    @Autowired
    private BidInfoService bidInfoService;


    @RequestMapping(value = "/index")
    public String index(HttpServletResponse request, Model model) {
        //历史平均年化利率
        Double historyAverageRate = loanInfoService.queryHistoryAverageRate();
        model.addAttribute(Constants.HISTORY_AVERAGE_RATE, historyAverageRate);

        //平台总人数
        Long allUserCount = userService.queryAllUserCount();
        model.addAttribute(Constants.ALL_USER_COUNT,allUserCount);

        //平台累计投资金额
        Double allBidMoney = bidInfoService.queryAllBidMoney();
        model.addAttribute(Constants.ALL_BID_MONEY,allBidMoney);

        //准备查询参数
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("currentPage",0);
        //新手宝:产品类型0,显示第1页,每页显示1条
        paramMap.put("productType",Constants.PRODUCT_TYPE_X);
        paramMap.put("pageSize",1);
        List<LoanInfo> xLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("xLoanInfoList",xLoanInfoList);

        // //优选产品:产品类型1,显示第1页,每页显示4条
        paramMap.put("productType",Constants.PRODUCT_TYPE_U);
        paramMap.put("pageSize",4);
        List<LoanInfo> uLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("uLoanInfoList",uLoanInfoList);

        //散标产品:产品类型2,显示第1页,每页显示8条
        paramMap.put("productType",Constants.PRODUCT_TYPE_S);
        paramMap.put("pageSize",4);
        List<LoanInfo> sLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("sLoanInfoList",sLoanInfoList);
        return "index";
    }
}

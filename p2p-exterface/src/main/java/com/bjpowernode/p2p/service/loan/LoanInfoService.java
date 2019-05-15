package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.vo.PaginationVO;

import java.util.List;
import java.util.Map;

public interface LoanInfoService {
    /**
     * 获取平均年华利率
     **/
    Double queryHistoryAverageRate();

    /**
     * 根据产品类型查产品
     * @param paramMap
     * @return
     */
    List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap);

    /**
     *
     * 分页查询产品信息列表
     * @param paramMap
     * @return
     */
    PaginationVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> paramMap);

    /**
     * 根据产品标识获取产品详情
     * @param id
     * @return
     */
    LoanInfo queryLoanInfoById(Integer id);


}

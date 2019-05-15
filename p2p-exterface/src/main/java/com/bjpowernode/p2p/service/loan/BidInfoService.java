package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.service.vo.BidUserTop;
import com.bjpowernode.p2p.service.vo.PaginationVO;
import com.bjpowernode.p2p.service.vo.ResultObject;

import java.util.List;
import java.util.Map;

public interface BidInfoService {
    /**
     * 平台累计投资金额
     * @return
     */
    Double queryAllBidMoney();

    /**
     *根据产品标识产品的所有投资记录
     * @param id
     * @return
     */
    List<BidInfo> queryBidInfoListByLoanId(Integer id);

    /**
     * 投资
     * @param paramMap
     * @return
     */
    ResultObject invest(Map<String, Object> paramMap);

    /**
     * 根据用户标识获取最近投资记录
     * @param paramMap
     * @return
     */
    List<BidInfo> queryBidIcnfoListByUid(Map<String, Object> paramMap);

    /**
     * //根据用户标识分页查询投资列表
     * @param paramMap
     * @return
     */
    PaginationVO<BidInfo> queryBidInfoByPage(Map<String, Object> paramMap);

    /**
     * 获取用户投资排行榜
     * @return
     */
    List<BidUserTop> queryBidUserTop();
}

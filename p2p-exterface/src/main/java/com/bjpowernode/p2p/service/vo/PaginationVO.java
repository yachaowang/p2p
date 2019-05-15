package com.bjpowernode.p2p.service.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PaginationVO
 * @Description
 * @Version 1.0
 * @Date 2019/3/26 20:23
 * @Author wyc
 **/
@Data
public class PaginationVO<T> implements Serializable {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 数据
     */
    private List<T> dataList;

}

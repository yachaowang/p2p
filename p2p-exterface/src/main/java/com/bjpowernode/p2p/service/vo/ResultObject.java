package com.bjpowernode.p2p.service.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ResultObject
 * @Description
 * @Version 1.0
 * @Date 2019/3/28 21:37
 * @Author wyc
 **/
@Data
public class ResultObject implements Serializable {

    /**
     * SUCCESS|FAIL
     */
    private String errorCode;

}

package com.bjpowernode.p2p.service.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName BidUserTop
 * @Description
 * @Version 1.0
 * @Date 2019/4/1 17:49
 * @Author wyc
 **/
@Data
public class BidUserTop implements Serializable {

    private String phone;

    private Double score;
}

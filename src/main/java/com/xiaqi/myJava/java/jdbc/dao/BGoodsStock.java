package com.xiaqi.myJava.java.jdbc.dao;

import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BGoodsStock {
    private Long pkno;

    private String cuser;

    private Date ctime;

    private String muser;

    private Date mtime;

    private String goodsId;

    private Integer goodsCount;
}

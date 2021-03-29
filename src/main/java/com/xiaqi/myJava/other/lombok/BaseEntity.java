package com.xiaqi.myJava.other.lombok;

import java.util.Date;

import lombok.Data;

@Data
public class BaseEntity {
	private Integer id;
    private Date createdDate;
    private Date updateedDate;
}

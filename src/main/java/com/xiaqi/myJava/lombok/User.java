package com.xiaqi.myJava.lombok;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data // 仅对一个继承类加上@Data，会有告警提示
@EqualsAndHashCode(callSuper = false)//“callSuper=false”表示，equals()和hashcode()方法不考虑父类中的属性
public class User extends BaseEntity {
	private String name;
	private int age;
}

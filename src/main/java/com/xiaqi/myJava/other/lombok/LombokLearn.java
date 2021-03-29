package com.xiaqi.myJava.other.lombok;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Lombok让开发人员少些代码<br/>
 * 
 * @Data：作用于类上，是以下注解的集合：@ToString @EqualsAndHashCode @Getter @Setter @RequiredArgsConstructor <br/>
 * @Slf4j 为类提供一个 属性名为log的 org.slf4j.Logger对像<br/>
 */
@Getter // 生成所有成员变量的getter/setter方法
@Setter
@ToString // 覆盖默认的toString()方法
@EqualsAndHashCode // 覆盖默认的equals和hashCode
@Accessors(chain = true) //用于配置getter和setter方法的生成结果.chain的中文含义是链式的，设置为true，则setter方法返回当前对象
public class LombokLearn{
	private String name;
	private int age;

	public static void main(String[] args) {
		LombokLearn user = new LombokLearn();
		//LombokLearn user2 = user.setName("hahaha");
		System.out.print(user);
	}
}

package jp.ats.liverwort.support.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * DTO の setter 情報を表すアノテーションです。
 *
 * @author 千葉 哲嗣
 */
@Target({ METHOD })
@Retention(RUNTIME)
public @interface DTOSetter {

	/**
	 * カラム名
	 *
	 * @return カラム名
	 */
	String column();

	/**
	 * 型
	 *
	 * @return 型
	 */
	Class<?> type();
}

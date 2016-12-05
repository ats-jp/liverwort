package jp.ats.liverwort.jdbc;

import java.sql.SQLException;

/**
 * {@link SQLException} を {@link LiException} 及びそのサブクラスに変換します。
 * <br>
 * {@link SQLException} は、 {@link Exception} のサブクラスなので、 {@link RuntimeException} のサブクラスである {@link LiException} に変換します。
 *
 * @author 千葉 哲嗣
 * @see Initializer#setErrorConverterClass(Class)
 */
@FunctionalInterface
public interface ErrorConverter {

	/**
	 * {@link SQLException} を {@link LiException} 及びそのサブクラスに変換します。
	 *
	 * @param e Liverwort 内で発生した {@link SQLException}
	 * @return 変換後の例外
	 */
	LiException convert(SQLException e);
}

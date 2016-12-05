package jp.ats.liverwort.jdbc;

import java.sql.Connection;

/**
 * {@link Connection} に似せ、機能を制限したインターフェイスです。
 *
 * @author 千葉 哲嗣
 * @see LiManager#getConnection()
 */
public interface LiConnection extends Metadata {

	/**
	 * SQL 文から {@link LiStatement} のインスタンスを生成し、返します。
	 * <br>
	 * パラメータで指定される SQL 文にはプレースホルダ '?' を使用することはできません。
	 *
	 * @param sql プレースホルダを持たない SQL
	 * @return {@link LiStatement} のインスタンス
	 */
	LiStatement getStatement(String sql);

	/**
	 * SQL 文から {@link LiStatement} のインスタンスを生成し、返します。
	 * <br>
	 * パラメータで指定される SQL 文にはプレースホルダ '?' を含めることが可能です。
	 *
	 * @param sql プレースホルダを持つ SQL
	 * @param complementer プレースホルダに結びつける値を持つ
	 * @return {@link LiStatement} のインスタンス
	 */
	LiStatement getStatement(String sql, PreparedStatementComplementer complementer);

	/**
	 * SQL 文から {@link LiPreparedStatement} のインスタンスを生成し、返します。
	 * <br>
	 * パラメータで指定される SQL 文にはプレースホルダ '?' を含めることが可能です。
	 *
	 * @param sql プレースホルダを持つ SQL
	 * @return {@link LiPreparedStatement} のインスタンス
	 */
	LiPreparedStatement prepareStatement(String sql);

	/**
	 * {@link BatchStatement} のインスタンスを生成し、返します。
	 *
	 * @return {@link BatchStatement} のインスタンス
	 */
	BatchStatement getBatchStatement();

	/**
	 * パラメータで指定された名称を、データベースで使用される標準的な名前に変換します。
	 *
	 * @param name 標準化前の名称
	 * @return 標準化された名称
	 */
	String regularize(String name);

	/**
	 * {@link PreparedStatementWrapper} を設定し、この接続が生成する {@link LiPreparedStatement} をラップさせます。
	 *
	 * @param wrapper この接続が生成する {@link LiPreparedStatement} のラッパー
	 */
	void setPreparedStatementWrapper(PreparedStatementWrapper wrapper);

	/**
	 * {@link BatchStatementWrapper} を設定し、この接続が生成する {@link BatchStatement} をラップさせます。
	 *
	 * @param wrapper この接続が生成する {@link BatchStatement} のラッパー
	 */
	void setBatchStatementWrapper(BatchStatementWrapper wrapper);
}

package jp.ats.liverwort.jdbc;

import java.sql.Statement;

/**
 * {@link Statement} に似せ、機能を制限したインターフェイスです。
 *
 * @author 千葉 哲嗣
 * @see LiConnection#getStatement(String)
 * @see LiConnection#getStatement(String, PreparedStatementComplementer)
 */
public interface LiStatement extends AutoCloseable {

	/**
	 * 検索を行います。
	 *
	 * @return 検索結果
	 */
	LiResultSet executeQuery();

	/**
	 * 更新を行います。
	 *
	 * @return 更新件数
	 */
	int executeUpdate();

	/**
	 * SQL文を実行します。
	 *
	 * @return 最初の結果が {@link LiResultSet} オブジェクトの場合は true 
	 */
	boolean execute();

	/**
	 * {@link #execute()} の結果の {@link LiResultSet} を取得します。
	 *
	 * @return 更新カウントであるか、または結果がない場合は null
	 */
	LiResultSet getResultSet();

	/**
	 * {@link #execute()} の結果の、更新カウントを取得します。
	 *
	 * @return 現在の結果が {@link LiResultSet} オブジェクトであるか、または結果がない場合は -1 
	 */
	int getUpdateCount();

	/**
	 * Statement オブジェクトの次の結果に移動します。
	 *
	 * @return 次の結果が ResultSet オブジェクトの場合は true
	 */
	boolean getMoreResults();

	/**
	 * このステートメントを閉じます。
	 * <br>
	 * このステートメントが生成した {@link LiResultSet} も同時に閉じます。
	 */
	@Override
	void close();
}

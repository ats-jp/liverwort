package jp.ats.liverwort.jdbc;

/**
 * {@link LiPreparedStatement} をラップし、機能追加するための仕組みを定義したインターフェイスです。
 *
 * @author 千葉 哲嗣
 * @see LiConnection#setPreparedStatementWrapper(PreparedStatementWrapper)
 */
@FunctionalInterface
public interface PreparedStatementWrapper {

	/**
	 * {@link LiPreparedStatement} が生成されたときに呼び出されます。
	 *
	 * @param statement 元の {@link LiPreparedStatement}
	 * @return ラップされた {@link LiPreparedStatement}
	 */
	LiPreparedStatement wrap(LiPreparedStatement statement);
}

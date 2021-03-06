package jp.ats.liverwort.jdbc;

import java.sql.DataTruncation;
import java.sql.SQLException;

import jp.ats.liverwort.internal.U;

/**
 * 独自の {@link ErrorConverter} を設定しなかった場合に使用されるデフォルトの {@link ErrorConverter} です。
 *
 * @author 千葉 哲嗣
 */
public class DefaultErrorConverter implements ErrorConverter {

	/**
	 * {@link Throwable#printStackTrace()} を実行し、パラメータで渡された {@link SQLException} を {@link LiException} でラップして返します。
	 *
	 * @throws LiException {@link SQLException} をラップした例外
	 */
	@Override
	public LiException convert(SQLException e) {
		if (e instanceof DataTruncation) {
			DataTruncation warning = (DataTruncation) e;
			String prefix = "data truncation: ";
			System.err.println(prefix + "index: " + warning.getIndex());
			System.err.println(prefix + "parameter: " + warning.getParameter());
			System.err.println(prefix + "read: " + warning.getRead());
			System.err.println(prefix + "data size: " + warning.getDataSize());
			System.err.println(prefix + "transfer size: " + warning.getTransferSize());
		}

		e.printStackTrace();

		throw new LiException(e);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}

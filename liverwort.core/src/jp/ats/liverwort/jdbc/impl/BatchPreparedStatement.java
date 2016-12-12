package jp.ats.liverwort.jdbc.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.ats.liverwort.internal.U;
import jp.ats.liverwort.jdbc.Configure;
import jp.ats.liverwort.jdbc.LiPreparedStatement;
import jp.ats.liverwort.jdbc.LiResultSet;

/**
 * {@link ConcreteBatchStatement} で使用する {@link LiPreparedStatement} の実装クラスです。
 *
 * @author 千葉 哲嗣
 */
class BatchPreparedStatement extends ConcretePreparedStatement {

	private final Configure config;

	private PreparedStatement statement;

	BatchPreparedStatement(Configure config, PreparedStatement statement) {
		super(config, statement);
		this.config = config;
		this.statement = statement;
	}

	void addBatch() {
		try {
			statement.addBatch();
		} catch (SQLException e) {
			close();
			throw config.getErrorConverter().convert(e);
		}
	}

	int[] executeBatch() {
		try {
			return statement.executeBatch();
		} catch (SQLException e) {
			close();
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public void close() {
		U.close(statement);
	}

	@Override
	public LiResultSet executeQuery() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int executeUpdate() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void finalize() {
		close();
	}
}

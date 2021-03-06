package jp.ats.liverwort.jdbc;

import jp.ats.liverwort.jdbc.wrapperbase.ConnectionBase;

/**
 * @author 千葉 哲嗣
 */
class LoggingConnection extends ConnectionBase
	implements PreparedStatementWrapper, BatchStatementWrapper {

	private final Logger logger;

	LoggingConnection(LiConnection conn, Logger logger) {
		super(conn);
		conn.setPreparedStatementWrapper(this);
		conn.setBatchStatementWrapper(this);
		this.logger = logger;
	}

	@Override
	public LiStatement getStatement(String sql) {
		logger.setSql(sql);
		return super.getStatement(sql);
	}

	@Override
	public LiStatement getStatement(
		String sql,
		PreparedStatementComplementer complementer) {
		logger.setSql(sql);
		return super.getStatement(sql, complementer);
	}

	@Override
	public LiPreparedStatement prepareStatement(String sql) {
		logger.setSql(sql);
		return super.prepareStatement(sql);
	}

	@Override
	public LiPreparedStatement wrap(LiPreparedStatement statement) {
		return new LoggingPreparedStatement(statement, logger);
	}

	@Override
	public BatchStatement wrap(BatchStatement statement) {
		return new LoggingBatchStatement(statement, logger);
	}
}

package jp.ats.liverwort.jdbc;

import jp.ats.liverwort.jdbc.wrapperbase.BatchStatementBase;

/**
 * @author 千葉 哲嗣
 */
class LoggingBatchStatement extends BatchStatementBase {

	private final Logger logger;

	LoggingBatchStatement(BatchStatement statement, Logger manager) {
		super(statement);
		this.logger = manager;
	}

	@Override
	public void addBatch(String sql) {
		logger.setSql(sql);
		super.addBatch(sql);
		logger.flush();
	}

	@Override
	public void addBatch(String sql, PreparedStatementComplementer complementer) {
		super.addBatch(sql, new LoggingComplementer(sql, logger, complementer));
	}

	private static class LoggingComplementer implements PreparedStatementComplementer {

		private final String sql;

		private final Logger logger;

		private final PreparedStatementComplementer base;

		private LoggingComplementer(
			String sql,
			Logger logger,
			PreparedStatementComplementer base) {
			this.sql = sql;
			this.logger = logger;
			this.base = base;
		}

		@Override
		public int complement(LiPreparedStatement statement) {
			logger.setSql(sql);
			int result = base.complement(statement);
			logger.flush();
			return result;
		}
	}
}

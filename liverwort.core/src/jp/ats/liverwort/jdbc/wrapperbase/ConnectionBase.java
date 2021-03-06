package jp.ats.liverwort.jdbc.wrapperbase;

import jp.ats.liverwort.jdbc.BatchStatement;
import jp.ats.liverwort.jdbc.BatchStatementWrapper;
import jp.ats.liverwort.jdbc.LiConnection;
import jp.ats.liverwort.jdbc.LiPreparedStatement;
import jp.ats.liverwort.jdbc.LiStatement;
import jp.ats.liverwort.jdbc.PreparedStatementComplementer;
import jp.ats.liverwort.jdbc.PreparedStatementWrapper;

/**
 * {@link LiConnection} のラッパーを実装するベースとなる、抽象基底クラスです。
 *
 * @author 千葉 哲嗣
 */
public abstract class ConnectionBase extends MetadataBase implements LiConnection {

	private final LiConnection base;

	/**
	 * ラップするインスタンスを受け取るコンストラクタです。
	 *
	 * @param base ベースとなるインスタンス
	 */
	protected ConnectionBase(LiConnection base) {
		super(base);
		this.base = base;
	}

	@Override
	public LiStatement getStatement(String sql) {
		return base.getStatement(sql);
	}

	@Override
	public LiStatement getStatement(String sql, PreparedStatementComplementer complementer) {
		return base.getStatement(sql, complementer);
	}

	@Override
	public LiPreparedStatement prepareStatement(String sql) {
		return base.prepareStatement(sql);
	}

	@Override
	public BatchStatement getBatchStatement() {
		return base.getBatchStatement();
	}

	@Override
	public String regularize(String name) {
		return base.regularize(name);
	}

	@Override
	public void setPreparedStatementWrapper(PreparedStatementWrapper wrapper) {
		base.setPreparedStatementWrapper(wrapper);
	}

	@Override
	public void setBatchStatementWrapper(BatchStatementWrapper wrapper) {
		base.setBatchStatementWrapper(wrapper);
	}
}

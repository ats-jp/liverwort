package jp.ats.liverwort.jdbc.wrapperbase;

import jp.ats.liverwort.jdbc.LiResultSet;

/**
 * {@link LiResultSet} のラッパーを実装するベースとなる、抽象基底クラスです。
 *
 * @author 千葉 哲嗣
 */
public abstract class ResultSetBase extends ResultBase implements LiResultSet {

	private final LiResultSet base;

	/**
	 * ラップするインスタンスを受け取るコンストラクタです。
	 *
	 * @param base ベースとなるインスタンス
	 */
	protected ResultSetBase(LiResultSet base) {
		super(base);
		this.base = base;
	}

	@Override
	public boolean next() {
		return base.next();
	}

	@Override
	public void close() {
		base.close();
	}
}

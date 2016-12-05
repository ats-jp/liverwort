package jp.ats.liverwort.orm;

import java.util.Objects;

import jp.ats.liverwort.selector.Selector;
import jp.ats.liverwort.sql.SQLAdjuster;

/**
 * {@link SQLAdjuster} を使用するためのラッパークラス です。
 *
 * @author 千葉 哲嗣
 */
public class SQLAdjusterOption implements QueryOption {

	private final SQLAdjuster adjuster;

	/**
	 * インスタンスを生成します。
	 *
	 * @param adjuster {@link SQLAdjuster}
	 */
	public SQLAdjusterOption(SQLAdjuster adjuster) {
		this.adjuster = Objects.requireNonNull(adjuster);
	}

	@Override
	public void process(Selector selector) {
		selector.setSQLAdjuster(adjuster);
	}
}

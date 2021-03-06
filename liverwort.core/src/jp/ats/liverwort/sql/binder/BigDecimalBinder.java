package jp.ats.liverwort.sql.binder;

import java.math.BigDecimal;

import jp.ats.liverwort.jdbc.LiPreparedStatement;
import jp.ats.liverwort.sql.Binder;

/**
 * {@link LiPreparedStatement} に {@link BigDecimal} の値を設定するための {@link Binder} です。
 *
 * @author 千葉 哲嗣
 */
public final class BigDecimalBinder extends Binder {

	private final BigDecimal value;

	/**
	 * パラメータの値を持つインスタンスを生成します。
	 *
	 * @param value このインスタンスの値
	 */
	public BigDecimalBinder(BigDecimal value) {
		this.value = value;
	}

	@Override
	public void bind(int index, LiPreparedStatement statement) {
		statement.setBigDecimal(index, value);
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public Binder replicate() {
		return this;
	}

	@Override
	public boolean canEvalValue() {
		return true;
	}

	@Override
	public Object getValue() {
		return value;
	}

	/**
	 * このインスタンスの持つ値を返します。
	 *
	 * @return 値
	 */
	public BigDecimal getBigDecimalValue() {
		return value;
	}

	@Override
	protected Object getSpecificallyValue() {
		return value;
	}
}

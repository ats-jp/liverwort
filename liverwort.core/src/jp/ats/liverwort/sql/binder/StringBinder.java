package jp.ats.liverwort.sql.binder;

import jp.ats.liverwort.jdbc.LiPreparedStatement;
import jp.ats.liverwort.sql.Binder;

/**
 * {@link LiPreparedStatement} に {@link String} の値を設定するための {@link Binder} です。
 *
 * @author 千葉 哲嗣
 */
public final class StringBinder extends Binder {

	private final String value;

	/**
	 * パラメータの値を持つインスタンスを生成します。
	 *
	 * @param value このインスタンスの値
	 */
	public StringBinder(String value) {
		this.value = value;
	}

	@Override
	public void bind(int index, LiPreparedStatement statement) {
		statement.setString(index, value);
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
	public String getStringValue() {
		return value;
	}

	@Override
	protected Object getSpecificallyValue() {
		return value;
	}
}

package jp.ats.liverwort.sql.binder;

import java.util.UUID;

import jp.ats.liverwort.jdbc.LiPreparedStatement;
import jp.ats.liverwort.sql.Binder;

/**
 * {@link LiPreparedStatement} に {@link UUID} の値を設定するための {@link Binder} です。
 *
 * @author 千葉 哲嗣
 */
public class UUIDBinder extends Binder {

	private final UUID value;

	/**
	 * パラメータの値を持つインスタンスを生成します。
	 *
	 * @param value このインスタンスの値
	 */
	public UUIDBinder(UUID value) {
		this.value = value;
	}

	@Override
	public void bind(int index, LiPreparedStatement statement) {
		statement.setObject(index, value);
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
	public UUID getUUIDValue() {
		return value;
	}

	@Override
	protected Object getSpecificallyValue() {
		return value;
	}

}

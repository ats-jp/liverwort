package jp.ats.liverwort.sql.binder;

import java.io.InputStream;

import jp.ats.liverwort.jdbc.LiPreparedStatement;
import jp.ats.liverwort.sql.Binder;

/**
 * {@link LiPreparedStatement} に {@link InputStream} から読み込んだ値を設定するための {@link Binder} です。
 *
 * @author 千葉 哲嗣
 */
public final class BinaryStreamBinder extends Binder {

	private final InputStream value;

	private final int length;

	/**
	 * 値を読み込む {@link InputStream} を持つインスタンスを生成します。
	 *
	 * @param value 値を読み込む {@link InputStream}
	 * @param length 値のサイズ
	 */
	public BinaryStreamBinder(InputStream value, int length) {
		this.value = value;
		this.length = length;
	}

	@Override
	public String toString() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void bind(int index, LiPreparedStatement statement) {
		statement.setBinaryStream(index, value, length);
	}

	@Override
	public Binder replicate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean canEvalValue() {
		return false;
	}

	@Override
	public Object getValue() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Object getSpecificallyValue() {
		throw new UnsupportedOperationException();
	}
}

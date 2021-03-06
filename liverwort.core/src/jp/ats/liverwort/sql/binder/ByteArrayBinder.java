package jp.ats.liverwort.sql.binder;

import java.util.Arrays;

import jp.ats.liverwort.jdbc.LiPreparedStatement;
import jp.ats.liverwort.sql.Binder;

/**
 * {@link LiPreparedStatement} に byte 配列を設定するための {@link Binder} です。
 *
 * @author 千葉 哲嗣
 */
public final class ByteArrayBinder extends Binder {

	private final ByteArray byteArray;

	/**
	 * パラメータの値を持つインスタンスを生成します。
	 *
	 * @param value このインスタンスの値
	 */
	public ByteArrayBinder(byte[] value) {
		this.byteArray = new ByteArray(value);
	}

	@Override
	public void bind(int index, LiPreparedStatement statement) {
		statement.setBytes(index, byteArray.value);
	}

	@Override
	public String toString() {
		return String.valueOf(byteArray);
	}

	@Override
	public Binder replicate() {
		return new ByteArrayBinder(byteArray.getClone());
	}

	@Override
	public boolean canEvalValue() {
		return true;
	}

	@Override
	public Object getValue() {
		return byteArray.getClone();
	}

	/**
	 * このインスタンスの持つ値を返します。
	 *
	 * @return 値
	 */
	public byte[] getByteArrayValue() {
		return byteArray.getClone();
	}

	@Override
	protected Object getSpecificallyValue() {
		return byteArray;
	}

	private static class ByteArray {

		private final byte[] value;

		private ByteArray(byte[] value) {
			this.value = clone(value);
		}

		@Override
		public int hashCode() {
			if (value == null) return 0;
			int[] hashCodes = new int[value.length];
			for (int i = 0; i < hashCodes.length; i++) {
				hashCodes[i] = value[i];
			}

			return Arrays.hashCode(hashCodes);
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof ByteArray)) return false;
			byte[] targetValue = ((ByteArray) o).value;
			if (value == null && targetValue == null) return true;
			return Arrays.equals(value, targetValue);
		}

		@Override
		public String toString() {
			return value == null ? null : new String(value);
		}

		private byte[] getClone() {
			return clone(value);
		}

		private static byte[] clone(byte[] value) {
			return value == null ? null : value.clone();
		}
	}
}

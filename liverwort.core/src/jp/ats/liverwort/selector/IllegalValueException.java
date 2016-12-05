package jp.ats.liverwort.selector;

import jp.ats.liverwort.jdbc.LiException;
import jp.ats.liverwort.sql.Column;

/**
 * 検索時に SELECT 句に使用されていなかったカラムの値を取得しようとしたときにスローされる例外です。
 *
 * @author 千葉 哲嗣
 * @see SelectedValues
 */
public class IllegalValueException extends LiException {

	private static final long serialVersionUID = -1712052451101320409L;

	IllegalValueException(String message) {
		super(message);
	}

	static final String buildMessage(Column column) {
		return column.getName() + " (" + column.getID() + ") は検索に使用されていません";
	}
}

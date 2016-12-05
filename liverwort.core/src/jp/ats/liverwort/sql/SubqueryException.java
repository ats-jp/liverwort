package jp.ats.liverwort.sql;

import jp.ats.liverwort.jdbc.LiException;

/**
 * サブクエリ私用する際に、パラメータの間違いから発生する例外です。
 *
 * @author 千葉 哲嗣
 */
public class SubqueryException extends LiException {

	private static final long serialVersionUID = 6839245167162134546L;

	/**
	 * 唯一のコンストラクタです。
	 *
	 * @param message エラーメッセージ
	 */
	public SubqueryException(String message) {
		super(message);
	}
}

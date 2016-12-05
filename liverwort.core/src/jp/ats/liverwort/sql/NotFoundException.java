package jp.ats.liverwort.sql;

import jp.ats.liverwort.jdbc.LiException;

/**
 * {@link Relationship} に要求された参照先やカラムがない場合にスローされる例外です。
 *
 * @author 千葉 哲嗣
 * @see Relationship#find(String)
 * @see Relationship#find(String[])
 * @see Relationship#getColumn(String)
 */
public class NotFoundException extends LiException {

	private static final long serialVersionUID = 8690198661404900748L;

	NotFoundException(String message) {
		super(message);
	}
}

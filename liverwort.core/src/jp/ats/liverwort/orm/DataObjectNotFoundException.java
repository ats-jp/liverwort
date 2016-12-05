package jp.ats.liverwort.orm;

import jp.ats.liverwort.jdbc.LiException;

/**
 * {@link PrimaryKey} を使用した検索にもかかわらず、対象のデータが存在しなかった場合にスローされる例外です。
 *
 * @author 千葉 哲嗣
 */
public class DataObjectNotFoundException extends LiException {

	private static final long serialVersionUID = 2041164796528717847L;

	DataObjectNotFoundException(String message) {
		super(message);
	}
}

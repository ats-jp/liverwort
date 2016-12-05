package jp.ats.liverwort.ext;

import jp.ats.liverwort.jdbc.LiException;
import jp.ats.liverwort.orm.QueryOption;

/**
 * ユニークキーを使用した検索にもかかわらず、検索結果が複数件あった場合にスローされる例外です。
 *
 * @author 千葉 哲嗣
 * @see Executor#willUnique()
 * @see Executor#willUnique(QueryOption...)
 */
public class NotUniqueException extends LiException {

	private static final long serialVersionUID = 4447218993715962245L;

	/**
	 * この例外のコンストラクタです。
	 */
	public NotUniqueException() {
		super();
	}
}

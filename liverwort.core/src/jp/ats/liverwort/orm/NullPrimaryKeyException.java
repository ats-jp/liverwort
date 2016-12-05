package jp.ats.liverwort.orm;

import jp.ats.liverwort.jdbc.LiException;
import jp.ats.liverwort.sql.Relationship;

/**
 * 対象となるテーブルに主キーが定義されていない場合にスローされる例外です。
 *
 * @author 千葉 哲嗣
 * @see DataObject#getPrimaryKeyBinders()
 */
public class NullPrimaryKeyException extends LiException {

	private static final long serialVersionUID = -2160138773158624947L;

	NullPrimaryKeyException(Relationship relationship) {
		super(relationship.getResourceLocator()
			+ " ("
			+ relationship.getID()
			+ ") は PK に値を持たないので、使用できません");
	}
}

package jp.ats.liverwort.selector;

import jp.ats.liverwort.jdbc.LiResult;
import jp.ats.liverwort.jdbc.ResourceLocator;
import jp.ats.liverwort.sql.Column;
import jp.ats.liverwort.sql.SelectClause;

/**
 * {@link Selector} が SQL 文生成時に SELECT 句を取得するための機能を定めたインターフェイスです。
 *
 * @author 千葉 哲嗣
 * @see Selector#Selector(Optimizer)
 */
public interface Optimizer {

	/**
	 * このクラスのインスタンスが持つ対象となるテーブルです。
	 *
	 * @return 対象となるテーブル
	 */
	ResourceLocator getResourceLocator();

	/**
	 * 検索に使用する SELECT 句です。
	 *
	 * @return SELECT 句
	 */
	SelectClause getOptimizedSelectClause();

	/**
	 * 検索結果と検索時に指定したカラムから {@link SelectedValues} を生成します。
	 *
	 * @param result 検索結果
	 * @param columns 検索時に指定したカラム
	 * @return {@link SelectedValues}
	 */
	SelectedValues convert(LiResult result, Column[] columns);
}

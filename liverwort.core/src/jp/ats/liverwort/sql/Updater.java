package jp.ats.liverwort.sql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jp.ats.liverwort.jdbc.LiPreparedStatement;
import jp.ats.liverwort.jdbc.PreparedStatementComplementer;
import jp.ats.liverwort.jdbc.ResourceLocator;

/**
 * 登録更新用 SQL 文を生成するための抽象基底クラスです。
 *
 * @author 千葉 哲嗣
 */
public abstract class Updater implements PreparedStatementComplementer {

	private final ResourceLocator locator;

	private final Set<String> columns = new LinkedHashSet<>();

	private final Map<String, Binder> values = new LinkedHashMap<>();

	private final Map<String, String> fragmentMap = new HashMap<>();

	private SQLAdjuster adjuster = SQLAdjuster.DISABLED_ADJUSTER;

	/**
	 * パラメータの表すテーブルに対する更新を行うインスタンスを生成します。
	 *
	 * @param locator 対象となるテーブル
	 */
	protected Updater(ResourceLocator locator) {
		this.locator = locator;
	}

	/**
	 * SQL 文に挿入する DB 格納可能な値を追加します。
	 * <br>
	 * 追加された順に SQL 文に反映されます。
	 *
	 * @param updatable DB格納可能な値
	 */
	public void add(Updatable updatable) {
		updatable.setValuesTo(this);
	}

	/**
	 * SQL 文に挿入する項目名とプレースホルダにバインドする値を追加します。
	 * <br>
	 * 追加された順に SQL 文に反映されます。
	 *
	 * @param columnName テーブルの項目名
	 * @param bindable バインド可能な値
	 */
	public void add(String columnName, Bindable bindable) {
		columns.add(columnName);
		values.put(columnName, bindable.toBinder());
	}

	/**
	 * SQL 文に挿入する項目名と SQL の断片を追加します。
	 * <br>
	 * 追加された順に SQL 文に反映されます。
	 *
	 * @param columnName テーブルの項目名
	 * @param fragment SQLの断片、たとえば SYSDATE など
	 */
	public void addSQLFragment(String columnName, String fragment) {
		columns.add(columnName);
		fragmentMap.put(columnName, fragment);
	}

	/**
	 * SQL 文に挿入する項目名と、プレースホルダをもつ SQL の断片を追加します。
	 * <br>
	 * 追加された順に SQL 文に反映されます。
	 *
	 * @param columnName テーブルの項目名
	 * @param fragment SQLの断片、たとえば TO_CHAR(?,'FM099') など
	 * @param bindable バインド可能な値
	 */
	public void addBindableSQLFragment(
		String columnName,
		String fragment,
		Bindable bindable) {
		addSQLFragment(columnName, fragment);
		values.put(columnName, bindable.toBinder());
	}

	/**
	 * DML に対する微調整をするための {@link SQLAdjuster} をセットします。
	 *
	 * @param adjuster SQL 文を調整する {@link SQLAdjuster}
	 */
	public void setSQLAdjuster(SQLAdjuster adjuster) {
		this.adjuster = adjuster;
	}

	@Override
	public String toString() {
		if (columns.size() == 0) throw new IllegalStateException("保存対象が設定されていません");
		return adjuster.adjustSQL(buildSQL());
	}

	@Override
	public int complement(LiPreparedStatement statement) {
		int counter = 0;
		for (Iterator<Binder> i = values.values().iterator(); i.hasNext(); counter++) {
			i.next().bind(counter + 1, statement);
		}
		return counter;
	}

	/**
	 * 更新対象となるテーブルを返します。
	 *
	 * @return 更新対象テーブル
	 */
	public ResourceLocator getResourceLocator() {
		return locator;
	}

	/**
	 * 現在保持しているカラムを返します。
	 *
	 * @return 現在保持しているカラム
	 */
	public String[] getColumnNames() {
		return columns.toArray(new String[columns.size()]);
	}

	/**
	 * 更新用 SQL 文を生成します。
	 *
	 * @return 更新用 SQL 文
	 */
	protected abstract String buildSQL();

	/**
	 * このカラム名に対応する SQL 文の一部かもしくはプレースホルダ '?' を返します。
	 *
	 * @param columnName 対象となるカラム名
	 * @return SQL 文の一部かもしくはプレースホルダ
	 */
	protected String getPlaceHolderOrFragment(String columnName) {
		String fragment = fragmentMap.get(columnName);
		if (fragment != null) return fragment;
		return "?";
	}
}

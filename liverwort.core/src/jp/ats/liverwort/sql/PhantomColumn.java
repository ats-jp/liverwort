package jp.ats.liverwort.sql;

import java.util.Objects;

import jp.ats.liverwort.jdbc.ColumnMetadata;
import jp.ats.liverwort.jdbc.ResourceLocator;

/**
 * {@link Column} クラスのインスタンス取得を簡易にするために、仮の {@link Column} として使用できるクラスです。
 * <br>
 * ただし、このクラスのインスタンスは一旦 {@link QueryBuilder} で本当の {@link Column} が確定するか、直接 {@link #prepareForSQL(Relationship)} を実行するまでは、ほとんどの機能は使用することができません。
 *
 * @author 千葉 哲嗣
 */
public class PhantomColumn extends Column {

	private final Object lock = new Object();

	private final ResourceLocator locator;

	private final String name;

	private final int phantomHashCode;

	private Column substance;

	/**
	 * このクラスのインスタンスを生成します。
	 *
	 * @param locator このカラムが属するテーブル
	 * @param name カラム名
	 */
	public PhantomColumn(ResourceLocator locator, String name) {
		this.locator = locator;
		this.name = name;
		phantomHashCode = Objects.hash(locator, name);
	}

	/**
	 * このクラスのインスタンスを生成します。
	 * <br>
	 * 使用される {@link ResourceLocator} は後に検索対象となる {@link Relationship} が持つものになります。
	 *
	 * @param name カラム名
	 */
	public PhantomColumn(String name) {
		this.locator = null;
		this.name = name;
		phantomHashCode = name.hashCode();
	}

	/**
	 * 本当の {@link Column} が決定するまでは、このクラス独自のハッシュコードを返します。
	 *
	 * @return {@link Column} が決定すると、その {@link Column} のハッシュコード、そうでなければ locator と name から算出したハッシュコード
	 */
	@Override
	public int hashCode() {
		Column substance = getSubstanceWithoutCheck();
		if (substance != null) return substance.hashCode();
		return phantomHashCode;
	}

	/**
	 * 本当の {@link Column} が決定するまでは、このクラスのインスタンス同士の equals になります。
	 *
	 * @return {@link Column} が決定すると、その {@link Column} との equals 、そうでなければ locator と name での equals
	 */
	@Override
	public boolean equals(Object o) {
		Column substance = getSubstanceWithoutCheck();
		if (substance != null) return substance.equals(o);
		if (!(o instanceof PhantomColumn)) return false;
		PhantomColumn target = (PhantomColumn) o;

		if (locator == null) return name.equals(target.name);

		return locator.equals(target.locator) && name.equals(target.name);
	}

	/**
	 * 本当の {@link Column} が決定するまでは、このクラスのインスタンス同士の比較値を返します。
	 *
	 * @return {@link Column} が決定すると、その {@link Column} との比較値 、そうでなければ locator と name での比較値
	 */
	@Override
	public int compareTo(Column target) {
		Column substance = getSubstanceWithoutCheck();
		if (substance != null) return substance.compareTo(target);
		if (!(target instanceof PhantomColumn)) return -1;
		PhantomColumn phantomColumnTarget = (PhantomColumn) target;

		if (locator == null) return name.compareTo(phantomColumnTarget.name);

		int locatorResult = locator.compareTo(phantomColumnTarget.locator);
		return locatorResult == 0 ? name.compareTo(phantomColumnTarget.name) : locatorResult;
	}

	@Override
	public String getID() {
		return getSubstanceWithCheck().getID();
	}

	@Override
	public String toString() {
		return getSubstanceWithCheck().toString();
	}

	@Override
	public Condition getCondition(Bindable bindable) {
		return getSubstanceWithCheck().getCondition(bindable);
	}

	@Override
	public Relationship getRelationship() {
		return getSubstanceWithCheck().getRelationship();
	}

	@Override
	public String getName() {
		return getSubstanceWithCheck().getName();
	}

	@Override
	public Class<?> getType() {
		return getSubstanceWithCheck().getType();
	}

	@Override
	public ColumnMetadata getColumnMetadata() {
		return getSubstanceWithCheck().getColumnMetadata();
	}

	@Override
	public String getComplementedName() {
		return getSubstanceWithCheck().getComplementedName();
	}

	@Override
	public Column findAnotherRootColumn(Relationship another) {
		return getSubstanceWithCheck().findAnotherRootColumn(another);
	}

	@Override
	public boolean isPrimaryKey() {
		return getSubstanceWithCheck().isPrimaryKey();
	}

	/**
	 * このインスタンスの Relationship ルートを決定します。
	 *
	 * @param sqlRoot このインスタンスのルートとなる {@link Relationship}
	 * @throws IllegalStateException sqlRoot がルートではないとき
	 * @throws IllegalStateException このインスタンスに既に別のルートが決定しているとき
	 */
	@Override
	public void prepareForSQL(Relationship sqlRoot) {
		if (!sqlRoot.isRoot()) throw new IllegalStateException(sqlRoot + " はルートではありません");
		synchronized (lock) {
			if (substance != null && !substance.getRelationship().getRoot().equals(sqlRoot))
				throw new IllegalStateException("このインスタンスは既に " + substance + " として使われています");

			Relationship relation;
			if (locator == null) {
				relation = RelationshipFactory.convert(sqlRoot, sqlRoot.getResourceLocator());
			} else {
				relation = RelationshipFactory.convert(sqlRoot, locator);
			}

			substance = relation.getColumn(name);
		}
	}

	static Column[] convert(String[] columnNames) {
		Column[] columns = new Column[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			columns[i] = new PhantomColumn(columnNames[i]);
		}
		return columns;
	}

	private Column getSubstanceWithoutCheck() {
		synchronized (lock) {
			return substance;
		}
	}

	private Column getSubstanceWithCheck() {
		synchronized (lock) {
			if (substance == null) throw new UnsupportedOperationException();
			return substance;
		}
	}
}

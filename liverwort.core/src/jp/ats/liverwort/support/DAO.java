package jp.ats.liverwort.support;

import java.util.Optional;

import jp.ats.liverwort.jdbc.BatchStatement;
import jp.ats.liverwort.jdbc.ResourceLocator;
import jp.ats.liverwort.orm.DataAccessHelper;
import jp.ats.liverwort.orm.DataObjectNotFoundException;
import jp.ats.liverwort.orm.PrimaryKey;
import jp.ats.liverwort.orm.RowLockOption;
import jp.ats.liverwort.orm.SequenceGenerator;
import jp.ats.liverwort.orm.UpdatableDataObject;
import jp.ats.liverwort.selector.Optimizer;
import jp.ats.liverwort.selector.SimpleOptimizer;
import jp.ats.liverwort.sql.Bindable;
import jp.ats.liverwort.sql.Condition;
import jp.ats.liverwort.sql.SQLAdjuster;
import jp.ats.liverwort.sql.Updatable;

/**
 * 自動生成される DAO の共通の振る舞いを定義したインターフェイスです。
 *
 * @author 千葉 哲嗣
 * @param <T> DTO
 */
public interface DAO<T extends DTO> {

	/**
	 * 空の検索結果
	 */
	public static final DTOIterator<DTO> EMPTY_ETERATOR = new DTOIterator<DTO>(
		DataAccessHelper.EMPTY_UPDATABLE_DATA_OBJECT_ITERATOR) {

		@Override
		public DTO next() {
			throw new UnsupportedOperationException();
		}
	};

	/**
	 * DTO を生成するメソッドです。
	 *
	 * @param data {@link DTO} の全要素の値を持つ検索結果オブジェクト
	 * @return 生成された {@link DTO}
	 */
	T createDTO(UpdatableDataObject data);

	/**
	 * サブクラスで固有の {@link ResourceLocator} を返します。
	 *
	 * @return 固有の {@link ResourceLocator}
	 */
	ResourceLocator getResourceLocator();

	/**
	 * この DAO で使用する {@link DataAccessHelper}
	 *
	 * @return {@link DataAccessHelper}
	 */
	DataAccessHelper getDataAccessHelper();

	/**
	 * 空の DTOIterator を返します。
	 *
	 * @param <T> {@link DTOIterator} の要素型
	 * @return {@link DTOIterator}
	 */
	@SuppressWarnings("unchecked")
	public static <T extends DTO> DTOIterator<T> getEmptyDTOIterator() {
		return (DTOIterator<T>) EMPTY_ETERATOR;
	}

	/**
	 * パラメータの主キーの値を持つ {@link DTO} を検索し返します。
	 * <br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 *
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する文字列
	 * @return {@link DTO} 存在しなければ null
	 */
	default Optional<T> select(QueryOptions options, String... primaryKeyMembers) {
		return select(new SimpleOptimizer(getResourceLocator()), options, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link DTO} を検索し返します。
	 * <br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 *
	 * @param primaryKeyMembers 主キーを構成する文字列
	 * @return {@link DTO} 存在しなければ null
	 */
	default Optional<T> select(String... primaryKeyMembers) {
		return select(QueryOptions.EMPTY_OPTIONS, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link DTO} を検索し返します。
	 * <br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 *
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link DTO} 存在しなければ null
	 */
	default Optional<T> select(QueryOptions options, Bindable... primaryKeyMembers) {
		return select(new SimpleOptimizer(getResourceLocator()), options, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link DTO} を検索し返します。
	 * <br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 *
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link DTO} 存在しなければ null
	 */
	default Optional<T> select(Bindable... primaryKeyMembers) {
		return select(QueryOptions.EMPTY_OPTIONS, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link DTO} を検索し返します。
	 *
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する文字列
	 * @return {@link DTO} 存在しなければ null
	 */
	default Optional<T> select(Optimizer optimizer, QueryOptions options, String... primaryKeyMembers) {
		UpdatableDataObject object;
		try {
			object = getDataAccessHelper().getUpdatableDataObject(
				optimizer,
				PrimaryKey.getInstance(getResourceLocator(), primaryKeyMembers),
				QueryOptions.care(options).get());
		} catch (DataObjectNotFoundException e) {
			return Optional.empty();
		}

		return Optional.of(createDTO(object));
	}

	/**
	 * パラメータの主キーの値を持つ {@link DTO} を検索し返します。
	 *
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link DTO} 存在しなければ null
	 */
	default Optional<T> select(Optimizer optimizer, String... primaryKeyMembers) {
		return select(optimizer, QueryOptions.EMPTY_OPTIONS, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link DTO} を検索し返します。
	 *
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link DTO} 存在しなければ null
	 */
	default Optional<T> select(Optimizer optimizer, QueryOptions options, Bindable... primaryKeyMembers) {
		UpdatableDataObject object;
		try {
			object = getDataAccessHelper().getUpdatableDataObject(
				optimizer,
				new PrimaryKey(getResourceLocator(), primaryKeyMembers),
				QueryOptions.care(options).get());
		} catch (DataObjectNotFoundException e) {
			return Optional.empty();
		}
		return Optional.of(createDTO(object));
	}

	/**
	 * パラメータの主キーの値を持つ {@link DTO} を検索し返します。
	 *
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link DTO} 存在しなければ null
	 */
	default Optional<T> select(Optimizer optimizer, Bindable... primaryKeyMembers) {
		return select(optimizer, QueryOptions.EMPTY_OPTIONS, primaryKeyMembers);
	}

	/**
	 * パラメータの条件にマッチする件数を返します。
	 *
	 * @param condition WHERE 句となる条件
	 * @return パラメータの条件にマッチする件数
	 */
	default int count(Condition condition) {
		return getDataAccessHelper().count(getResourceLocator(), condition);
	}

	/**
	 * パラメータの DTO の INSERT を行います。
	 *
	 * @param dto INSERT 対象
	 */
	default void insert(T dto) {
		getDataAccessHelper().insert(getResourceLocator(), dto, null);
	}

	/**
	 * パラメータの DTO の INSERT をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param dto INSERT 対象
	 */
	default void insert(BatchStatement statement, T dto) {
		getDataAccessHelper().insert(statement, getResourceLocator(), dto, null);
	}

	/**
	 * パラメータの DTO の INSERT を行います。
	 *
	 * @param dto INSERT 対象
	 * @param adjuster INSERT 文を調整する {@link SQLAdjuster}
	 */
	default void insert(T dto, SQLAdjuster adjuster) {
		getDataAccessHelper().insert(getResourceLocator(), dto, adjuster);
	}

	/**
	 * パラメータの DTO の INSERT をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param dto INSERT 対象
	 * @param adjuster INSERT 文を調整する {@link SQLAdjuster}
	 */
	default void insert(BatchStatement statement, T dto, SQLAdjuster adjuster) {
		getDataAccessHelper().insert(statement, getResourceLocator(), dto, adjuster);
	}

	/**
	 * パラメータの DTO の INSERT を行います。
	 *
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param dto INSERT 対象
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @return INSERT された実際の連続値
	 */
	default Bindable insert(SequenceGenerator generator, T dto, int retry) {
		return getDataAccessHelper().insert(getResourceLocator(), generator, dto, retry, null);
	}

	/**
	 * パラメータの DTO の INSERT をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param dto INSERT 対象
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @return INSERT された実際の連続値
	 */
	default Bindable insert(BatchStatement statement, SequenceGenerator generator, T dto, int retry) {
		return getDataAccessHelper().insert(statement, getResourceLocator(), generator, dto, retry, null);
	}

	/**
	 * パラメータの DTO の INSERT を行います。
	 *
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param dto INSERT 対象
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @param adjuster INSERT 文を調整する {@link SQLAdjuster}
	 * @return INSERT された実際の連続値
	 */
	default Bindable insert(SequenceGenerator generator, T dto, int retry, SQLAdjuster adjuster) {
		return getDataAccessHelper().insert(getResourceLocator(), generator, dto, retry, adjuster);
	}

	/**
	 * パラメータの DTO の INSERT をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param dto INSERT 対象
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @param adjuster INSERT 文を調整する {@link SQLAdjuster}
	 * @return INSERT された実際の連続値
	 */
	default Bindable insert(
		BatchStatement statement,
		SequenceGenerator generator,
		T dto,
		int retry,
		SQLAdjuster adjuster) {
		return getDataAccessHelper().insert(statement, getResourceLocator(), generator, dto, retry, adjuster);
	}

	/**
	 * パラメータの DTO の DELETE を行います。
	 *
	 * @param dto DELETE 対象
	 * @return 削除が成功した場合、 true
	 */
	default boolean delete(T dto) {
		int result = getDataAccessHelper().delete(getResourceLocator(), dto.getPrimaryKey().getCondition());
		if (result > 1) throw new IllegalStateException("削除件数が複数件あります。");
		return result == 1;
	}

	/**
	 * パラメータの DTO の DELETE をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param dto DELETE 対象
	 */
	default void delete(BatchStatement statement, T dto) {
		getDataAccessHelper().delete(statement, getResourceLocator(), dto.getPrimaryKey().getCondition());
	}

	/**
	 * パラメータの条件に該当する行を更新します。
	 *
	 * @param condition WHERE 句となる条件
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @return 更新件数
	 */
	default int update(Condition condition, Updatable updatable) {
		return getDataAccessHelper().update(getResourceLocator(), updatable, condition, null);
	}

	/**
	 * パラメータの条件に該当する行の更新をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param condition WHERE 句となる条件
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 */
	default void update(BatchStatement statement, Condition condition, Updatable updatable) {
		getDataAccessHelper().update(statement, getResourceLocator(), updatable, condition, null);
	}

	/**
	 * パラメータの条件に該当する行を更新します。
	 *
	 * @param condition WHERE 句となる条件
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @param adjuster UPDATE 文を調整する {@link SQLAdjuster}
	 * @return 更新件数
	 */
	default int update(Condition condition, Updatable updatable, SQLAdjuster adjuster) {
		return getDataAccessHelper().update(getResourceLocator(), updatable, condition, adjuster);
	}

	/**
	 * パラメータの条件に該当する行の更新をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param condition WHERE 句となる条件
	 * @param adjuster UPDATE 文を調整する {@link SQLAdjuster}
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 */
	default void update(BatchStatement statement, Condition condition, Updatable updatable, SQLAdjuster adjuster) {
		getDataAccessHelper().update(statement, getResourceLocator(), updatable, condition, adjuster);
	}

	/**
	 * パラメータの条件に該当する行を削除します。
	 *
	 * @param condition WHERE 句となる条件
	 * @return 削除件数
	 */
	default int delete(Condition condition) {
		return getDataAccessHelper().delete(getResourceLocator(), condition);
	}

	/**
	 * パラメータの条件に該当する行の削除をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param condition WHERE 句となる条件
	 */
	default void delete(BatchStatement statement, Condition condition) {
		getDataAccessHelper().delete(statement, getResourceLocator(), condition);
	}
}

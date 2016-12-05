package jp.ats.liverwort.ext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import jp.ats.liverwort.jdbc.Initializer;
import jp.ats.liverwort.jdbc.LiContext;
import jp.ats.liverwort.jdbc.LiManager;
import jp.ats.liverwort.jdbc.LiTransaction;
import jp.ats.liverwort.jdbc.MetadataFactory;
import jp.ats.liverwort.jdbc.OptionKey;
import jp.ats.liverwort.jdbc.TransactionFactory;
import jp.ats.liverwort.selector.AnchorOptimizerFactory;
import jp.ats.liverwort.selector.ColumnRepositoryFactory;
import jp.ats.liverwort.selector.SelectorConfigure;
import jp.ats.liverwort.sql.RelationshipFactory;

/**
 * Liverwort 全体を対象とする、簡易操作クラスです。
 *
 * @author 千葉 哲嗣
 */
public class Liverwort {

	private Class<? extends MetadataFactory> defaultMetadataFactoryClass = AnnotationMetadataFactory.class;

	private Class<? extends TransactionFactory> defaultTransactionFactoryClass = DriverTransactionFactory.class;

	private Class<? extends ColumnRepositoryFactory> defaultColumnRepositoryFactoryClass = FileColumnRepositoryFactory.class;

	/**
	 * Liverwort を使用可能な状態にします。
	 *
	 * @param initValues Liverwort を初期化するための値
	 */
	public void start(Map<OptionKey<?>, ?> initValues) {
		Initializer init = new Initializer();

		init.setOptions(new HashMap<>(initValues));

		LiConstants.SCHEMA_NAMES.extract(initValues).ifPresent(names -> {
			for (String name : names) {
				init.addSchemaName(name);
			}
		});

		LiConstants.ENABLE_LOG.extract(initValues).ifPresent(flag -> init.enableLog(flag));

		LiConstants.USE_METADATA_CACHE.extract(initValues).ifPresent(flag -> init.setUseMetadataCache(flag));

		LiConstants.LOG_STACKTRACE_FILTER.extract(initValues).ifPresent(filter -> init.setLogStackTraceFilter(Pattern.compile(filter)));

		LiConstants.ERROR_CONVERTER_CLASS.extract(initValues).ifPresent(clazz -> init.setErrorConverterClass(clazz));

		Optional.ofNullable(
			LiConstants.METADATA_FACTORY_CLASS.extract(initValues).orElseGet(
				() -> Optional.of(getDefaultMetadataFactoryClass())
					.filter(c -> LiConstants.ANNOTATED_DTO_PACKAGES.extract(initValues).isPresent())
					.orElse(null)))
			.ifPresent(clazz -> init.setMetadataFactoryClass(clazz));

		Optional.ofNullable(
			LiConstants.TRANSACTION_FACTORY_CLASS.extract(initValues).orElseGet(
				() -> Optional.of(getDefaultTransactionFactoryClass())
					.filter(
						c -> LiConstants.JDBC_DRIVER_CLASS_NAME.extract(initValues)
							.filter(name -> name.length() > 0)
							.isPresent())
					.orElse(null)))
			.ifPresent(clazz -> init.setTransactionFactoryClass(clazz));

		LiContext.get(LiManager.class).initialize(init);

		LiConstants.VALUE_EXTRACTORS_CLASS.extract(initValues)
			.ifPresent(clazz -> LiContext.get(SelectorConfigure.class).setValueExtractorsClass(clazz));

		AnchorOptimizerFactory anchorOptimizerFactory = LiContext.get(AnchorOptimizerFactory.class);

		LiConstants.CAN_ADD_NEW_ENTRIES.extract(initValues)
			.ifPresent(flag -> anchorOptimizerFactory.setCanAddNewEntries(flag));

		anchorOptimizerFactory.setColumnRepositoryFactoryClass(
			LiConstants.COLUMN_REPOSITORY_FACTORY_CLASS.extract(initValues)
				.orElseGet(() -> getDefaultColumnRepositoryFactoryClass()));
	}

	/**
	 * トランザクション内で任意の処理を実行します。
	 *
	 * @param function {@link Function} の実装
	 * @throws Exception 処理内で起こった例外
	 */
	public static void execute(Function function) throws Exception {
		if (LiContext.get(LiManager.class).hasConnection()) throw new IllegalStateException("既にトランザクションが開始されています");

		TransactionManager.start(new TransactionShell() {

			@Override
			public void execute() throws Exception {
				function.execute(getTransaction());
			}
		});
	}

	/**
	 * デフォルト {@link MetadataFactory} を返します。
	 *
	 * @return デフォルト {@link MetadataFactory}
	 */
	public synchronized Class<? extends MetadataFactory> getDefaultMetadataFactoryClass() {
		return defaultMetadataFactoryClass;
	}

	/**
	 * デフォルト {@link MetadataFactory} をセットします。
	 *
	 * @param defaultMetadataFactoryClass デフォルト {@link MetadataFactory}
	 */
	public synchronized void setDefaultMetadataFactoryClass(
		Class<? extends MetadataFactory> defaultMetadataFactoryClass) {
		this.defaultMetadataFactoryClass = defaultMetadataFactoryClass;
	}

	/**
	 * デフォルト {@link TransactionFactory} を返します。
	 *
	 * @return デフォルト {@link TransactionFactory}
	 */
	public synchronized Class<? extends TransactionFactory> getDefaultTransactionFactoryClass() {
		return defaultTransactionFactoryClass;
	}

	/**
	 * デフォルト {@link TransactionFactory} をセットします。
	 *
	 * @param defaultTransactionFactoryClass デフォルト {@link TransactionFactory}
	 */
	public synchronized void setDefaultTransactionFactoryClass(
		Class<? extends TransactionFactory> defaultTransactionFactoryClass) {
		this.defaultTransactionFactoryClass = defaultTransactionFactoryClass;
	}

	/**
	 * デフォルト {@link ColumnRepositoryFactory} を返します。
	 *
	 * @return デフォルト {@link ColumnRepositoryFactory}
	 */
	public synchronized Class<? extends ColumnRepositoryFactory> getDefaultColumnRepositoryFactoryClass() {
		return defaultColumnRepositoryFactoryClass;
	}

	/**
	 * デフォルト {@link ColumnRepositoryFactory} をセットします。
	 *
	 * @param defaultColumnRepositoryFactoryClass デフォルト {@link ColumnRepositoryFactory}
	 */
	public synchronized void setDefaultColumnRepositoryFactoryClass(
		Class<? extends ColumnRepositoryFactory> defaultColumnRepositoryFactoryClass) {
		this.defaultColumnRepositoryFactoryClass = defaultColumnRepositoryFactoryClass;
	}

	/**
	 * Liverwort が持つ定義情報の各キャッシュをクリアします。
	 */
	public static void clearCache() {
		LiContext.get(LiManager.class).clearMetadataCache();
		LiContext.get(RelationshipFactory.class).clearCache();
	}

	/**
	 * トランザクション内で行う任意の処理を表しています。
	 */
	@FunctionalInterface
	public interface Function {

		/**
		 * トランザクション内で呼び出されます。 <br>
		 * 処理が終了した時点で commit が行われます。 <br>
		 * 例外を投げた場合は rollback が行われます。
		 *
		 * @param transaction この処理のトランザクション
		 * @throws Exception 処理内で起こった例外
		 */
		void execute(LiTransaction transaction) throws Exception;
	}
}

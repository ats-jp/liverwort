package jp.ats.liverwort.jdbc;

import jp.ats.liverwort.internal.LoggingManager;
import jp.ats.liverwort.internal.Transactions;

/**
 * Liverwort の設定を管理し、トランザクションの生成、接続の管理等をおこなうハブクラスです。
 *
 * @author 千葉 哲嗣
 */
public class LiManager implements ManagementSubject {

	private final ThreadLocal<ThreadLocalValues> threadLocalValues = ThreadLocal.withInitial(
		() -> new ThreadLocalValues());

	private Object lock = new Object();

	private Configure config;

	/**
	 * 新しい {@link Initializer} を使用して Liverwort の設定を初期化します
	 *
	 * @param initializer 新しい設定を持つ {@link Initializer}
	 */
	public void initialize(Initializer initializer) {
		synchronized (lock) {
			if (config != null) throw new IllegalStateException("既に initialize が実行されています。");

			config = initializer.createConfigure();
		}

		LoggingManager.getLogger().info("Liverwort initialized. " + initializer);
	}

	/**
	 * 現在の設定を返します。
	 *
	 * @return 現在の設定を持つ {@link Configure}
	 * @throws IllegalStateException まだ {@link LiManager#initialize(Initializer)} が行われていない場合
	 */
	public Configure getConfigure() {
		synchronized (lock) {
			if (config == null) throw new IllegalStateException("設定が完了していません");
			return config;
		}
	}

	/**
	 * 現在実行中のスレッドが接続を持っているか検査します。
	 *
	 * @return スレッドが接続を持っているかどうか
	 */
	public boolean hasConnection() {
		return threadLocalValues.get().transaction != null;
	}

	/**
	 * 新しいトランザクションを開始します。
	 * <br>
	 * トランザクションを開始することで現在のスレッドに接続がセットされ、 {@link LiManager#getConnection()} で取得することができます。
	 *
	 * @return 新しいトランザクション
	 * @throws IllegalStateException 既にこのスレッド用にトランザクションが開始されている場合
	 */
	public LiTransaction startTransaction() {
		if (hasConnection()) throw new IllegalStateException("既にこのスレッド用にトランザクションが開始されています");

		Configure config = getConfigure();
		config.check();

		config.initialize();

		LiTransaction transaction = config.getTransactionFactoryWithoutCheck().createTransaction();
		LiConnection connection = transaction.getConnection();
		if (config.usesMetadataCacheWithoutCheck()) connection = new MetadataCacheConnection(connection);

		if (config.enablesLogWithoutCheck()) connection = new LoggingConnection(
			connection,
			new Logger(config.getLogOutputWithoutCheck(), config.getLogStackTracePatternWithoutCheck()));

		config.initializeMetadatas(connection);
		Metadata[] metadatas = config.getMetadatasWithoutCheck();
		if (metadatas.length > 0) connection = new MetadatasConnection(connection, metadatas);

		transaction.setConnection(connection);

		ThreadLocalValues values = threadLocalValues.get();
		values.transaction = transaction;

		return transaction;
	}

	/**
	 * 現在のスレッドが持つ接続を返します。
	 *
	 * @return 接続
	 */
	public LiConnection getConnection() {
		LiTransaction transaction = threadLocalValues.get().transaction;
		if (transaction == null) throw new IllegalStateException("このスレッドのトランザクションが開始されていません");

		LiConnection connection = transaction.getConnection();
		if (connection == null) throw new IllegalStateException("このスレッドの接続が作成されていません");

		return connection;
	}

	/**
	 * 現在のトランザクションに、パラメータで渡されたトランザクションを連動させます。
	 *
	 * @param transaction 連動させたいトランザクション
	 */
	public void synchroniseWithCurrentTransaction(Transaction transaction) {
		threadLocalValues.get().transactions.regist(transaction);
	}

	/**
	 * {@link Configure#usesMetadataCache() } を使用した場合にできるキャッシュを空にします。
	 */
	public void clearMetadataCache() {
		LiContext.get(MetadataCache.class).clearCache();
	}

	Transactions getTransactions() {
		return threadLocalValues.get().transactions;
	}

	boolean isCurrent(Configure config) {
		synchronized (lock) {
			return this.config == config;
		}
	}

	void remove(LiTransaction transaction) {
		threadLocalValues.get().remove(transaction);
	}

	private static class ThreadLocalValues {

		private final Transactions transactions = new Transactions();

		private LiTransaction transaction;

		/**
		 * このクラスで管理するトランザクションの場合のみ削除する
		 */
		private void remove(LiTransaction transaction) {
			if (this.transaction != transaction) return;
			this.transaction = null;
		}
	}
}

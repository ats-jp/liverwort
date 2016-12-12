package jp.ats.liverwort.jdbc;

import java.sql.Connection;

import jp.ats.liverwort.internal.Transactions;
import jp.ats.liverwort.internal.U;

/**
 * {@link Connection} から、トランザクション操作部分のみを抽出した抽象クラスです。
 *
 * @author 千葉 哲嗣
 * @see LiManager#startTransaction()
 */
public abstract class LiTransaction implements AutoCloseable, Transaction {

	private final LiManager manager = LiContext.get(LiManager.class);

	private LiConnection connection;

	/**
	 * 接続及び {@link LiManager#synchroniseWithCurrentTransaction(Transaction)} で登録された {@link Transaction} をコミットします。
	 *
	 * @see LiManager#synchroniseWithCurrentTransaction(Transaction)
	 */
	@Override
	public final void commit() {
		Transactions transactions = manager.getTransactions();
		try {
			commitInternal();
		} catch (Throwable t) {
			transactions.clear();
			throw new RuntimeException(t);
		}

		transactions.commit();
	}

	/**
	 * 接続及び {@link LiManager#synchroniseWithCurrentTransaction(Transaction)} で登録された {@link Transaction} をロールバックします。
	 *
	 * @see LiManager#synchroniseWithCurrentTransaction(Transaction)
	 */
	@Override
	public final void rollback() {
		Transactions transactions = manager.getTransactions();
		try {
			rollbackInternal();
		} catch (Throwable t) {
			transactions.clear();
			throw new RuntimeException(t);
		}

		transactions.rollback();
	}

	/**
	 * 接続を閉じます。
	 *
	 * @throws IllegalStateException {@link LiManager#synchroniseWithCurrentTransaction(Transaction)} で登録された {@link Transaction} があり、 {@link LiTransaction#commit()} または {@link LiTransaction#rollback} が実行されていない場合
	 * @see LiManager#synchroniseWithCurrentTransaction(Transaction)
	 */
	@Override
	public final void close() {
		//close中に例外が出るかもしれないので、Connectionの削除が先
		manager.remove(this);

		Transactions transactions = manager.getTransactions();
		try {
			closeInternal();
			if (transactions.size() > 0) throw new IllegalStateException("commit もしくは rollback が実行されていません");
		} finally {
			transactions.clear();
		}
	}

	/**
	 * このトランザクションで使用する接続を取得します。
	 *
	 * @return 接続
	 */
	public LiConnection getConnection() {
		if (connection == null) return getConnectionInternal();
		return connection;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	/**
	 * このトランザクションで使用する接続を取得します。
	 *
	 * @return 接続
	 */
	protected abstract LiConnection getConnectionInternal();

	/**
	 * 実際のコミットを行います。
	 */
	protected abstract void commitInternal();

	/**
	 * 実際のロールバックを行います。
	 */
	protected abstract void rollbackInternal();

	/**
	 * 実際に接続を閉じます。
	 */
	protected abstract void closeInternal();

	void setConnection(LiConnection connection) {
		this.connection = connection;
	}
}

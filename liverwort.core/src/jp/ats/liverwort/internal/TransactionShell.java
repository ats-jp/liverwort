package jp.ats.liverwort.internal;

import jp.ats.liverwort.jdbc.LiContext;
import jp.ats.liverwort.jdbc.LiManager;
import jp.ats.liverwort.jdbc.LiTransaction;

/**
 * 終了時に {@link LiTransaction} を自動的にクローズする抽象基底クラスです。
 *
 * @author 千葉 哲嗣
 */
public abstract class TransactionShell implements Shell {

	private final LiManager manager = LiContext.get(LiManager.class);

	private final LiTransaction transaction;

	/**
	 * デフォルトコンストラクタです。
	 */
	protected TransactionShell() {
		if (manager.hasConnection()) {
			this.transaction = null;
		} else {
			this.transaction = manager.startTransaction();
		}
	}

	@Override
	public void prepare() {
		if (transaction != null) TransactionManager.regist(transaction);
	}

	@Override
	public void doFinally() {
		if (transaction != null) transaction.close();
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	/**
	 * サブクラスで、 {@link LiTransaction} が必要な場合取得するためのメソッドです。
	 *
	 * @return 現在オープンしている {@link LiTransaction}
	 */
	protected LiTransaction getTransaction() {
		if (transaction == null)
			throw new IllegalStateException("トランザクションを開始した Shell ではないのでトランザクションがありません");

		return transaction;
	}
}

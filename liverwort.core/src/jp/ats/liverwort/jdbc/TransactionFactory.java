package jp.ats.liverwort.jdbc;

/**
 * {@link LiTransaction} を生成するファクトリのインターフェイスです。
 *
 * @author 千葉 哲嗣
 * @see Initializer#setTransactionFactoryClass(Class)
 */
@FunctionalInterface
public interface TransactionFactory {

	/**
	 * トランザクションを生成します。
	 *
	 * @return トランザクション
	 */
	LiTransaction createTransaction();
}

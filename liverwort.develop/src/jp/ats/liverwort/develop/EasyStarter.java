package jp.ats.liverwort.develop;

import java.sql.DriverManager;
import java.sql.SQLException;

import jp.ats.liverwort.jdbc.Initializer;
import jp.ats.liverwort.jdbc.LiContext;
import jp.ats.liverwort.jdbc.LiManager;
import jp.ats.liverwort.jdbc.LiTransaction;
import jp.ats.liverwort.jdbc.TransactionFactory;
import jp.ats.liverwort.jdbc.impl.JDBCTransaction;

/**
 * Liverwort を、 JDBC ドライバのクラス名、データベースの URL 、ユーザー名、パスワードのみで、開発時など、一時的に使用するためのクラスです。
 *
 * @author 千葉 哲嗣
 */
public class EasyStarter {

	private static final Object lock = new Object();

	private static String url;

	private static String userName;

	private static String password;

	/**
	 * Liverwort を使用可能な状態にします。
	 *
	 * @param jdbcDriverClassName JDBC ドライバの完全クラス名
	 * @param url データベースの URL
	 * @param schemaNames 使用するスキーマ名
	 * @param userName ユーザー名
	 * @param password パスワード
	 * @param develop 開発環境かどうか
	 */
	public static void start(
		String jdbcDriverClassName,
		String url,
		String[] schemaNames,
		String userName,
		String password,
		boolean develop) {
		try {
			Class.forName(jdbcDriverClassName);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		synchronized (lock) {
			EasyStarter.url = url;
			EasyStarter.userName = userName;
			EasyStarter.password = password;
		}

		Initializer init = new Initializer();
		for (String schemaName : schemaNames)
			init.addSchemaName(schemaName);
		init.enableLog(develop);
		init.setTransactionFactoryClass(EasyTransactionFactory.class);
		LiContext.get(LiManager.class).initialize(init);
	}

	/**
	 * {@link EasyStarter} で使用する {@link TransactionFactory} の簡易実装クラスです。
	 */
	public static class EasyTransactionFactory implements TransactionFactory {

		@Override
		public LiTransaction createTransaction() {
			try {
				synchronized (lock) {
					return new JDBCTransaction(DriverManager.getConnection(url, userName, password));
				}
			} catch (SQLException e) {
				throw new IllegalStateException(e);
			}
		}
	}
}

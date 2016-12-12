package jp.ats.liverwort.support;

import java.io.File;

import jp.ats.liverwort.internal.FileIOStream;
import jp.ats.liverwort.internal.HomeStorage;
import jp.ats.liverwort.internal.U;
import jp.ats.liverwort.jdbc.LiContext;
import jp.ats.liverwort.jdbc.LiManager;
import jp.ats.liverwort.selector.ColumnRepository;
import jp.ats.liverwort.selector.ColumnRepositoryFactory;
import jp.ats.liverwort.selector.StreamColumnRepository;

/**
 * ファイルをリポジトリのソースとして使用するシンプルなリポジトリファクトリクラスです。
 *
 * @author 千葉 哲嗣
 */
public class FileColumnRepositoryFactory implements ColumnRepositoryFactory {

	/**
	 * デフォルトリポジトリファイル名
	 */
	public static final String DEFAULT_COLUMN_REPOSITORY_FILE = "liverwort-column-repository";

	/**
	 * {@link HomeStorage} に格納するリポジトリファイル用エントリのキー
	 */
	public static final String COLUMN_REPOSITORY_FILE = "column-repository-file";

	private final File repositoryFile;

	/**
	 * このクラスのコンストラクタです。
	 */
	public FileColumnRepositoryFactory() {
		LiManager manager = LiContext.get(LiManager.class);

		repositoryFile = manager
			.getConfigure()
			.getOption(LiConstants.COLUMN_REPOSITORY_FILE)
			.map(file -> new File(file))
			.orElseGet(() -> manager
				.getConfigure()
				.getOption(LiConstants.CAN_ADD_NEW_ENTRIES)
				.filter(flag -> flag)
				.map(flag -> {
					String repositoryFileString = HomeStorage.loadProperties().getProperty(COLUMN_REPOSITORY_FILE);

					File repositoryFile = new File(repositoryFileString);
					if (!repositoryFile.exists()) throw new IllegalStateException(
						HomeStorage.getPropertiesFile().getAbsolutePath()
							+ " 内に記述されている "
							+ repositoryFileString
							+ " は存在しません。");

					return repositoryFile;
				})
				.orElseGet(() -> U.getResourceAsFile("/" + DEFAULT_COLUMN_REPOSITORY_FILE)));

		if (repositoryFile == null) throw new IllegalStateException("リポジトリファイルが存在しません");
	}

	@Override
	public ColumnRepository createColumnRepository() {
		return new StreamColumnRepository(new FileIOStream(repositoryFile));
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}

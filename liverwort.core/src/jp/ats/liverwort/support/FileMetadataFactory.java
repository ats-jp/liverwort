package jp.ats.liverwort.support;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Optional;

import jp.ats.liverwort.internal.U;
import jp.ats.liverwort.jdbc.LiContext;
import jp.ats.liverwort.jdbc.LiManager;
import jp.ats.liverwort.jdbc.Metadata;
import jp.ats.liverwort.jdbc.MetadataFactory;

/**
 * {@link VirtualSpace} を XML ファイルからロードするファクトリクラスです。
 *
 * @author 千葉 哲嗣
 */
public class FileMetadataFactory implements MetadataFactory {

	/**
	 * デフォルトの XML ファイルの場所
	 */
	public static final String XML_LOCATION = "/liverwort-metadata.xml";

	private URL xml;

	private final VirtualSpace virtualSpace;

	/**
	 * このクラスのインスタンスを生成します。
	 *
	 * @throws IOException XML の読み込みに失敗した場合
	 * @throws ClassNotFoundException XML 内で定義されている項目の型のクラスが存在しない場合
	 */
	public FileMetadataFactory() throws IOException, ClassNotFoundException {
		LiManager manager = LiContext.get(LiManager.class);

		Optional<String> fileOfOption = manager.getConfigure().getOption(LiConstants.METADATTA_XML_FILE);

		xml = fileOfOption.map(name -> {
			try {
				return new File(name).toURI().toURL();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}).orElse(FileMetadataFactory.class.getResource(XML_LOCATION));

		virtualSpace = FileVirtualSpaceFactory.getInstance(xml);
	}

	@Override
	public Metadata[] createMetadatas(Metadata depends) {
		if (!virtualSpace.isStarted()) {
			virtualSpace.start(depends);
		}
		return new Metadata[] { virtualSpace };
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}

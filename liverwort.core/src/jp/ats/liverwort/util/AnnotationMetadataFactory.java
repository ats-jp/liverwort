package jp.ats.liverwort.util;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import jp.ats.liverwort.internal.U;
import jp.ats.liverwort.jdbc.ColumnMetadata;
import jp.ats.liverwort.jdbc.LiContext;
import jp.ats.liverwort.jdbc.LiManager;
import jp.ats.liverwort.jdbc.Metadata;
import jp.ats.liverwort.jdbc.MetadataFactory;
import jp.ats.liverwort.jdbc.ResourceLocator;
import jp.ats.liverwort.support.DTO;
import jp.ats.liverwort.support.annotation.FKs;
import jp.ats.liverwort.support.annotation.PseudoFK;
import jp.ats.liverwort.support.annotation.PseudoPK;

/**
 * {@link VirtualSpace} を {@link DTO} に付与されたアノテーションからロードするファクトリクラスです。
 *
 * @author 千葉 哲嗣
 */
public class AnnotationMetadataFactory implements MetadataFactory {

	private static final ColumnMetadata[] emptyColumns = new ColumnMetadata[] {};

	private final VirtualSpace virtualSpace;

	/**
	 * このクラスのインスタンスを生成します。
	 */
	public AnnotationMetadataFactory() {
		virtualSpace = getInstance(
			LiContext.get(LiManager.class).getConfigure().getOption(LiConstants.ANNOTATED_DTO_PACKAGES).orElseThrow(
				() -> new NullPointerException()));
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

	/**
	 * アノテーションの調査をするクラスをロードするためのクラスローダーを返します。
	 *
	 * @return {@link ClassLoader}
	 */
	protected ClassLoader getClassLoader() {
		return AnnotationMetadataFactory.class.getClassLoader();
	}

	/**
	 * アノテーションの調査をするクラスの対象かどうかを返します。
	 * <br>
	 * オーバーライドすることで条件を変えることができます。
	 *
	 * @param clazz 調査対象のクラス
	 * @return アノテーションの調査をするクラスの対象かどうか
	 */
	protected boolean matches(Class<?> clazz) {
		return DTO.class.isAssignableFrom(clazz) && !clazz.isInterface();
	}

	private VirtualSpace getInstance(String[] packages) {
		VirtualSpace space = new VirtualSpace();
		for (String name : packages) {
			listClasses(getClassLoader(), name).stream()
				.map(AnnotationMetadataFactory::convert)
				.forEach(table -> space.addTable(table));
		}

		return space;
	}

	private static TableSource convert(Class<?> clazz) {
		PseudoPK pk = clazz.getAnnotation(PseudoPK.class);
		FKs fks = clazz.getAnnotation(FKs.class);

		List<ForeignKeySource> fkSources = new LinkedList<>();
		if (fks != null) fkSources = Arrays.asList(fks.value())
			.stream()
			.map(AnnotationMetadataFactory::createSource)
			.collect(Collectors.toList());

		PseudoFK fk = clazz.getAnnotation(PseudoFK.class);
		if (fk != null) fkSources.add(createSource(fk));

		return new TableSource(
			new ResourceLocator(clazz.getSimpleName()),
			null,
			emptyColumns,
			pk == null ? null : createSource(pk),
			fkSources.toArray(new ForeignKeySource[fkSources.size()]));
	}

	private static PrimaryKeySource createSource(PseudoPK annotation) {
		return new PrimaryKeySource(annotation.name(), annotation.columns(), true);
	}

	private static ForeignKeySource createSource(PseudoFK annotation) {
		return new ForeignKeySource(
			annotation.name(),
			annotation.columns(),
			new ResourceLocator(annotation.references()));
	}

	private List<Class<?>> listClasses(ClassLoader loader, String packageName) {
		String path = packageName.replace('.', '/');

		URL url = loader.getResource(path);

		if (url == null) throw new IllegalStateException("パッケージ名 " + packageName + " は存在しません");

		if ("file".equals(url.getProtocol())) return forFile(packageName, loader, url);

		if ("jar".equals(url.getProtocol())) return forJar(path, loader, url);

		throw new Error();
	}

	private List<Class<?>> forFile(String packageName, ClassLoader loader, URL url) {
		File[] files = new File(url.getFile()).listFiles((dir, name) -> name.endsWith(".class"));

		return Arrays.asList(files)
			.stream()
			.map(file -> packageName + "." + file.getName().replaceAll(".class$", ""))
			.map(name -> U.call(() -> loader.loadClass(name)))
			.filter(this::matches)
			.collect(Collectors.toList());
	}

	private List<Class<?>> forJar(String path, ClassLoader loader, URL url) {
		try (JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile()) {
			return Collections.list(jarFile.entries())
				.stream()
				.map(entry -> entry.getName())
				.filter(name -> name.startsWith(path) && name.endsWith(".class"))
				.map(name -> name.replace('/', '.').replaceAll(".class$", ""))
				.map(name -> U.call(() -> loader.loadClass(name.replace('/', '.').replaceAll(".class$", ""))))
				.filter(this::matches)
				.collect(Collectors.toList());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}

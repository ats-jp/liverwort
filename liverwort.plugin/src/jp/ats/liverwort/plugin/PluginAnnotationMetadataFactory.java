package jp.ats.liverwort.plugin;

import jp.ats.liverwort.support.DTO;
import jp.ats.liverwort.util.AnnotationMetadataFactory;

public class PluginAnnotationMetadataFactory extends AnnotationMetadataFactory {

	private static final String dtoClassName = DTO.class.getName();

	private static ClassLoader loader;

	static void setClassLoader(ClassLoader loader) {
		PluginAnnotationMetadataFactory.loader = loader;
	}

	@Override
	protected ClassLoader getClassLoader() {
		return loader;
	}

	@Override
	protected boolean matches(Class<?> clazz) {
		return hasDTO(clazz.getInterfaces()) && !clazz.isInterface();
	}

	private static boolean hasDTO(Class<?>[] interfaces) {
		for (Class<?> clazz : interfaces) {
			if (clazz.getName().equals(dtoClassName)) return true;
		}

		return false;
	}
}

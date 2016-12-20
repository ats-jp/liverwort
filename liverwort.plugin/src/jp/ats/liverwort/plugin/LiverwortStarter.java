package jp.ats.liverwort.plugin;

import java.util.Map;

import jp.ats.liverwort.jdbc.LiContext;
import jp.ats.liverwort.jdbc.OptionKey;
import jp.ats.liverwort.util.Liverwort;

/**
 * Liverwort 全体を対象とする、簡易操作クラスです。
 *
 * @author 千葉 哲嗣
 */
public class LiverwortStarter {

	/**
	 * Liverwort を使用可能な状態にします。
	 *
	 * @param initValues Liverwort を初期化するための値
	 */
	public static void start(
		JavaProjectClassLoader loader,
		Map<OptionKey<?>, ?> initValues) throws Exception {
		PluginDriverTransactionFactory.setClassLoader(loader);
		PluginAnnotationMetadataFactory.setClassLoader(loader);

		LiContext.newStrategy();
		LiContext.updateStrategy();

		Liverwort liverwort = new Liverwort();

		liverwort.setDefaultTransactionFactoryClass(
			PluginDriverTransactionFactory.class);
		liverwort.setDefaultMetadataFactoryClass(
			PluginAnnotationMetadataFactory.class);

		liverwort.start(initValues);
	}
}

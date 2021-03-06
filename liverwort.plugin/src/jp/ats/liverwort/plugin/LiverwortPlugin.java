package jp.ats.liverwort.plugin;

import static jp.ats.liverwort.internal.U.isAvailable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import jp.ats.liverwort.develop.CodeFormatter;
import jp.ats.liverwort.internal.HomeStorage;
import jp.ats.liverwort.internal.U;
import jp.ats.liverwort.jdbc.LiContext;
import jp.ats.liverwort.jdbc.LiManager;
import jp.ats.liverwort.jdbc.LiTransaction;
import jp.ats.liverwort.jdbc.MetadataFactory;
import jp.ats.liverwort.jdbc.OptionKey;
import jp.ats.liverwort.jdbc.TransactionFactory;
import jp.ats.liverwort.plugin.views.ClassBuilderView;
import jp.ats.liverwort.plugin.views.QueryEditorView;
import jp.ats.liverwort.selector.ColumnRepositoryFactory;
import jp.ats.liverwort.support.Query;
import jp.ats.liverwort.util.FileColumnRepositoryFactory;
import jp.ats.liverwort.util.LiConstants;

public class LiverwortPlugin extends AbstractUIPlugin {

	private static final String pluginID = "jp.ats.liverwort.plugin";

	private static final String currentProjectKeyName = "project-name";

	private static final Pattern typeSignaturePattern = Pattern.compile("[Q|L]([^;]+);");

	private static final Pattern queryClassPattern = Pattern.compile(Query.class.getSimpleName());

	private static LiverwortPlugin plugin;

	private final Map<IJavaProject, ColumnRepositoryFactory> columnRepositoryFactoryMap = new HashMap<>();

	private IJavaProject currentProject;

	private QueryEditorView queryEditorView;

	private ClassBuilderView classBuilderView;

	private String[] schemaNames = {};

	private LiTransaction transaction;

	private final Map<String, String> outputPackages = new HashMap<>();

	private Class<?> daoParentClass;

	private Class<?> dtoParentClass;

	private Class<?> queryParentClass;

	private CodeFormatter codeFormatter;

	private boolean useNumberClass;

	private boolean notUseNullGuard;

	public static LiverwortPlugin getDefault() {
		return plugin;
	}

	public static void checkRequiredClass(
		boolean required,
		IJavaProject project,
		Class<?> superInterface,
		String className) throws JavaProjectException {
		if (className == null || className.length() == 0) {
			if (!required) return;
			throw new JavaProjectException(superInterface.getName() + " の実装クラス名が空です");
		}

		try {
			IType target = project.findType(className);
			if (target == null) {
				throw new JavaProjectException("存在するクラスを指定する必要があります");
			}
			IType factoryType = project.findType(superInterface.getName());
			if (factoryType == null) {
				throw new JavaProjectException("プロジェクト内に " + superInterface.getName() + " が見つかりません");
			}

			if (target.isInterface()) {
				throw new JavaProjectException("インターフェイスは指定できません");
			}

			if (Flags.isAbstract(target.getFlags())) {
				throw new JavaProjectException("抽象クラスは指定できません");
			}

			String superclass;
			while (true) {
				String[] types = target.getSuperInterfaceNames();
				for (String type : types) {
					IType[] resolved = resolveType(project, target, type);
					for (IType itype : resolved)
						if (factoryType.equals(itype)) return;
				}
				superclass = target.getSuperclassName();
				if (superclass == null) break;

				IType[] resolved = resolveType(project, target, superclass);
				if (resolved.length == 0)
					throw new JavaProjectException(target.getFullyQualifiedName() + " が見つかりません");

				target = resolved[0];
			}

			throw new JavaProjectException(
				"指定されたクラスは " + superInterface.getName() + " を実装していません");
		} catch (JavaModelException e) {
			throw new JavaProjectException(className + " に問題があります");
		}
	}

	public LiverwortPlugin() {
		super();
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		String projectName = InstanceScope.INSTANCE.getNode(pluginID).get(currentProjectKeyName, null);
		if (projectName == null || projectName.equals("")) return;
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (project.isOpen() && project.hasNature(JavaCore.NATURE_ID)) {
			try {
				setProject((IJavaProject) project.getNature(JavaCore.NATURE_ID));
			} catch (Exception e) {
				currentProject = null;
				MessageDialog.openError(null, Constants.TITLE, cause(e).getMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (currentProject != null) {
			InstanceScope.INSTANCE.getNode(pluginID).put(
				currentProjectKeyName,
				currentProject.getElementName());
		}

		closeTransaction();
		super.stop(context);
	}

	public IJavaProject getProject() {
		return currentProject;
	}

	public void setProject(IJavaProject project) throws JavaProjectException {
		if (currentProject != null && currentProject.equals(project)) return;
		setProjectAndRefresh(project);
	}

	public void setProjectAndRefresh(IJavaProject project) throws JavaProjectException {
		checkProject(project);
		currentProject = project;
		refresh();
		if (queryEditorView != null) queryEditorView.reset();
		if (classBuilderView != null) classBuilderView.reset();
	}

	public ColumnRepositoryFactory getColumnRepositoryFactory() {
		ColumnRepositoryFactory factory = columnRepositoryFactoryMap.get(currentProject);
		if (factory == null) throw new IllegalStateException();
		return factory;
	}

	public Class<?> getDAOParentClass() {
		return daoParentClass;
	}

	public Class<?> getDTOParentClass() {
		return dtoParentClass;
	}

	public Class<?> getQueryParentClass() {
		return queryParentClass;
	}

	public CodeFormatter getCodeFormatter() {
		return codeFormatter;
	}

	public boolean useNumberClass() {
		return useNumberClass;
	}

	public boolean notUseNullGuard() {
		return notUseNullGuard;
	}

	public void storePersistentProperties(
		IJavaProject project,
		Properties properties) throws JavaProjectException {
		try (OutputStream output = new BufferedOutputStream(
			new FileOutputStream(getPropertiesFile(project)))) {
			properties.store(output, "");
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		refresh();
	}

	public static final class JavaProjectException extends Exception {

		private static final long serialVersionUID = 1825520626876129705L;

		private JavaProjectException(String message) {
			super(message);
		}

		private JavaProjectException(Exception e) {
			super(e);
			e.printStackTrace();
		}
	}

	public static Properties getPersistentProperties(IJavaProject project) {
		Properties properties = new Properties();
		try {
			InputStream input = new BufferedInputStream(
				new FileInputStream(getPropertiesFile(project)));
			try {
				properties.load(input);
			} finally {
				input.close();
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		return properties;
	}

	public static boolean save(
		IJavaProject project,
		String key,
		String value) {
		try {
			project.getProject().setPersistentProperty(new QualifiedName("", key), value);
		} catch (CoreException e) {
			return false;
		}

		return true;
	}

	public static String load(IJavaProject project, String key) {
		try {
			String value = project.getProject().getPersistentProperty(new QualifiedName("", key));
			return value == null ? "" : value;
		} catch (CoreException e) {
			return "";
		}
	}

	public void setSchemaNames(String[] names) {
		schemaNames = names;
	}

	public String[] getSchemaNames() {
		return schemaNames;
	}

	public QueryEditorView getQueryEditorView() {
		return queryEditorView;
	}

	public void setQueryEditorView(QueryEditorView view) {
		this.queryEditorView = view;
	}

	public ClassBuilderView getClassBuilderView() {
		return classBuilderView;
	}

	public void setClassBuilderView(ClassBuilderView view) {
		this.classBuilderView = view;
	}

	public String getOutputPackage(String schemaName) {
		return outputPackages.get(schemaName);
	}

	public static String regularizeColumnRepositoryFilePath(IJavaProject project, String path) {
		return path.substring(project.getProject().getLocation().toFile().getAbsolutePath().length() + 1);
	}

	public static String generalizeColumnRepositoryFilePath(IJavaProject project, String path) {
		return project.getProject().getLocation().toFile().getAbsolutePath() + U.FILE_SEPARATOR + path;
	}

	public static String[] splitByBlankAndRemoveEmptyString(String target) {
		String regex = " +";
		String[] values = (target == null ? "" : target).split(regex);
		List<String> list = new LinkedList<>();
		for (String value : values) {
			if (isAvailable(value)) list.add(value);
		}

		return list.toArray(new String[list.size()]);
	}

	private static Throwable cause(Throwable t) {
		Throwable cause = t.getCause();
		if (cause == null) return t;
		return cause(cause);
	}

	private void refresh() throws JavaProjectException {
		closeTransaction();

		outputPackages.clear();

		Map<OptionKey<?>, Object> init = new HashMap<>();

		Properties properties = getPersistentProperties(currentProject);

		init.put(
			LiConstants.ANNOTATED_DTO_PACKAGES,
			splitByBlankAndRemoveEmptyString(properties.getProperty(
				Constants.OUTPUT_PACKAGE_NAMES)));

		String[] schemaNameArray = splitByBlankAndRemoveEmptyString(
			properties.getProperty(Constants.SCHEMA_NAMES));

		setSchemaNames(schemaNameArray);

		init.put(LiConstants.SCHEMA_NAMES, schemaNameArray);

		String[] packageNameArray = splitByBlankAndRemoveEmptyString(
			properties.getProperty(Constants.OUTPUT_PACKAGE_NAMES));
		for (int i = 0; i < schemaNameArray.length; i++) {
			outputPackages.put(schemaNameArray[i], packageNameArray[i]);
		}

		String columnRepositoryFile = generalizeColumnRepositoryFilePath(
			currentProject,
			properties.getProperty(Constants.COLUMN_REPOSITORY_FILE));
		init.put(LiConstants.COLUMN_REPOSITORY_FILE, columnRepositoryFile);

		//初回まだ保存されていないのでここでとりあえず
		storeColumnRepositoryFileToHome(columnRepositoryFile);

		JavaProjectClassLoader loader;
		try {
			loader = new JavaProjectClassLoader(
				getClass().getClassLoader(),
				currentProject,
				true);
		} catch (JavaModelException e) {
			throw new JavaProjectException(e);
		}

		String jdbcDriverClass = properties.getProperty(Constants.JDBC_DRIVER_CLASS);
		if (U.isAvailable(jdbcDriverClass)) {
			init.put(LiConstants.JDBC_DRIVER_CLASS_NAME, jdbcDriverClass);
		}

		String jdbcURL = LiverwortPlugin.load(currentProject, Constants.JDBC_URL);
		if (U.isAvailable(jdbcURL)) {
			init.put(LiConstants.JDBC_URL, jdbcURL);
			init.put(LiConstants.JDBC_USER, LiverwortPlugin.load(currentProject, Constants.JDBC_USER));
			init.put(LiConstants.JDBC_PASSWORD, LiverwortPlugin.load(currentProject, Constants.JDBC_PASSWORD));
		}

		Class<? extends ColumnRepositoryFactory> columnRepositoryFactoryClass;
		try {
			{
				String classString = properties.getProperty(Constants.TRANSACTION_FACTORY_CLASS);
				if (U.isAvailable(classString))
					init.put(LiConstants.TRANSACTION_FACTORY_CLASS, Class.forName(classString, false, loader));
			}

			{
				String classString = properties.getProperty(Constants.METADATA_FACTORY_CLASS);
				if (isAvailable(classString))
					init.put(LiConstants.METADATA_FACTORY_CLASS, Class.forName(classString, false, loader));
			}

			{
				String classString = properties.getProperty(Constants.COLUMN_REPOSITORY_FACTORY_CLASS);
				if (U.isAvailable(classString)) {
					@SuppressWarnings("unchecked")
					Class<? extends ColumnRepositoryFactory> casted = (Class<? extends ColumnRepositoryFactory>) Class
						.forName(classString, false, loader);

					columnRepositoryFactoryClass = casted;
					init.put(LiConstants.COLUMN_REPOSITORY_FACTORY_CLASS, columnRepositoryFactoryClass);
				} else {
					columnRepositoryFactoryClass = FileColumnRepositoryFactory.class;
				}
			}
		} catch (ClassNotFoundException e) {
			throw new JavaProjectException(e);
		}

		init.put(LiConstants.USE_METADATA_CACHE, true);

		try {
			LiverwortStarter.start(loader, init);

			columnRepositoryFactoryMap.put(
				currentProject,
				columnRepositoryFactoryClass.newInstance());

			transaction = LiContext.get(LiManager.class).startTransaction();
		} catch (Exception e) {
			throw new JavaProjectException(e);
		}

		try {
			String daoParentClassName = properties.getProperty(Constants.DAO_PARENT_CLASS);
			if (isAvailable(daoParentClassName)) {
				daoParentClass = Class.forName(daoParentClassName, false, loader);
			} else {
				daoParentClass = null;
			}

			String dtoParentClassName = properties.getProperty(Constants.DTO_PARENT_CLASS);
			if (isAvailable(dtoParentClassName)) {
				dtoParentClass = Class.forName(dtoParentClassName, false, loader);
			} else {
				dtoParentClass = null;
			}

			String queryParentClassName = properties.getProperty(Constants.QUERY_PARENT_CLASS);
			if (isAvailable(queryParentClassName)) {
				queryParentClass = Class.forName(queryParentClassName, false, loader);
			} else {
				queryParentClass = null;
			}

			String codeFormatterClassName = properties.getProperty(Constants.CODE_FORMATTER_CLASS);
			if (isAvailable(codeFormatterClassName)) {
				ClassLoader pluginLoader = new JavaProjectClassLoader(
					getClass().getClassLoader(),
					currentProject,
					true);
				codeFormatter = (CodeFormatter) Class.forName(codeFormatterClassName, false, pluginLoader).newInstance();
			} else {
				codeFormatter = null;
			}

			String useNumberClassString = properties.getProperty(Constants.USE_NUMBER_CLASS);
			if (isAvailable(useNumberClassString)) {
				useNumberClass = Boolean.parseBoolean(useNumberClassString);
			} else {
				useNumberClass = false;
			}

			String notUseNullGuardString = properties.getProperty(Constants.NOT_USE_NULL_GUARD);
			if (isAvailable(notUseNullGuardString)) {
				notUseNullGuard = Boolean.parseBoolean(notUseNullGuardString);
			} else {
				notUseNullGuard = false;
			}
		} catch (Exception e) {
			throw new JavaProjectException(e);
		}
	}

	private void closeTransaction() {
		if (transaction == null) return;
		transaction.close();
		transaction = null;
	}

	private static void checkProject(IJavaProject project) throws JavaProjectException {
		if (project == null) return;
		Properties properties = getPersistentProperties(project);
		checkRequiredClass(
			false,
			project,
			TransactionFactory.class,
			properties.getProperty(Constants.TRANSACTION_FACTORY_CLASS));
		checkRequiredClass(
			false,
			project,
			ColumnRepositoryFactory.class,
			properties.getProperty(Constants.COLUMN_REPOSITORY_FACTORY_CLASS));
		checkRequiredClass(
			false,
			project,
			MetadataFactory.class,
			properties.getProperty(Constants.METADATA_FACTORY_CLASS));
	}

	public static IType findFiledType(IField field) throws JavaModelException {
		Matcher matcher = typeSignaturePattern.matcher(field.getTypeSignature());
		matcher.matches();
		String[][] resolved = field.getDeclaringType().resolveType(matcher.group(1));

		return field.getJavaProject().findType(resolved[0][0], resolved[0][1]);
	}

	public static boolean checkQueryClass(IType type) throws JavaModelException {
		String[] interfaces = type.getSuperInterfaceNames();
		for (String name : interfaces) {
			if (queryClassPattern.matcher(name).matches()) return true;
		}

		return false;
	}

	public static void storeColumnRepositoryFileToHome(String file) {
		Properties properties = HomeStorage.loadProperties();
		properties.put(FileColumnRepositoryFactory.COLUMN_REPOSITORY_FILE, file);
		HomeStorage.storeProperties(properties);
	}

	private static File getPropertiesFile(IJavaProject project) throws IOException {
		File file = new File(project.getProject().getLocation().toFile(), ".liverwort.plugin");
		if (!file.exists()) {
			file.createNewFile();

			OutputStream output = new BufferedOutputStream(new FileOutputStream(file));
			try {
				new Properties().store(output, "");
			} finally {
				output.close();
			}
		}

		return file;
	}

	private static IType[] resolveType(
		IJavaProject project,
		IType base,
		String className) throws JavaModelException {
		if (base.isBinary()) {
			return new IType[] { project.findType(className) };
		}

		String[][] resolved = base.resolveType(className);
		if (resolved == null) return new IType[0];
		List<IType> types = new LinkedList<>();
		for (String[] element : resolved)
			types.add(project.findType(String.join(".", element)));

		return types.toArray(new IType[types.size()]);
	}
}

package jp.ats.liverwort.plugin;

import org.eclipse.jface.resource.ImageDescriptor;

public class Constants {

	public static final String TITLE = "Liverwort";

	public static final String SCHEMA_NAMES = "schemaNames";

	public static final String OUTPUT_PACKAGE_NAMES = "outputPackageNames";

	public static final String JDBC_DRIVER_CLASS = "jdbcDriverClass";

	public static final String JDBC_URL = "jdbcURL";

	public static final String JDBC_USER = "jdbcUser";

	public static final String JDBC_PASSWORD = "jdbcPassword";

	public static final String COLUMN_REPOSITORY_FILE = "columnRepositoryFile";

	public static final String COLUMN_REPOSITORY_FACTORY_CLASS = "columnRepositoryFactoryClass";

	public static final String TRANSACTION_FACTORY_CLASS = "transactionFactoryClass";

	public static final String METADATA_FACTORY_CLASS = "metadataFactoryClass";

	public static final String DAO_PARENT_CLASS = "daoParentClass";

	public static final String DTO_PARENT_CLASS = "dtoParentClass";

	public static final String QUERY_PARENT_CLASS = "queryParentClass";

	public static final String CODE_FORMATTER_CLASS = "codeFormatterClass";

	public static final String USE_NUMBER_CLASS = "useNumberClass";

	public static final String NOT_USE_NULL_GUARD = "notUseNullGuard";

	public static final ImageDescriptor LIVERWORT_ICON;

	public static final ImageDescriptor COLLAPSE_ALL_ICON;

	public static final ImageDescriptor DERETE_ALL_EDIT_ICON;

	public static final ImageDescriptor SAVE_ICON;

	public static final ImageDescriptor REFESH_ICON;

	public static final ImageDescriptor UNDO_ICON;

	public static final ImageDescriptor REDO_ICON;

	public static final ImageDescriptor REMOVE_ICON;

	public static final ImageDescriptor PACKAGE_ICON;

	public static final ImageDescriptor CLASS_ICON;

	public static final ImageDescriptor ANCHOR_ICON;

	public static final ImageDescriptor ERROR_ICON;

	public static final ImageDescriptor RELATIONSHIP_ICON;

	public static final ImageDescriptor COLUMN_ICON;

	public static final ImageDescriptor COLUMN_DISABLE_ICON;

	public static final ImageDescriptor UNMARK_COLUMN_ICON;

	public static final ImageDescriptor PRIMARY_KEY_ICON;

	public static final ImageDescriptor FOREIGN_KEY_ICON;

	public static final ImageDescriptor SCHEMA_ICON;

	public static final ImageDescriptor TABLE_ICON;

	public static final ImageDescriptor UNBUILT_TABLE_ICON;

	private static final String ID = "jp.ats.liverwort.plugin";

	static {
		LIVERWORT_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/liverwort.png");

		COLLAPSE_ALL_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/collapse_all.gif");

		DERETE_ALL_EDIT_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/delete_all_edit.gif");

		SAVE_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/save.gif");

		REFESH_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/refresh.gif");

		UNDO_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/undo.gif");

		REDO_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/redo.gif");

		REMOVE_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/remove.gif");

		PACKAGE_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/package.gif");

		CLASS_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/class.gif");

		ANCHOR_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/anchor.gif");

		ERROR_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/error.gif");

		RELATIONSHIP_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/relationship.png");

		COLUMN_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/column.png");

		COLUMN_DISABLE_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/column_disable.png");

		UNMARK_COLUMN_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/unmark_column.png");

		PRIMARY_KEY_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/primary_key.png");

		FOREIGN_KEY_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/foreign_key.png");

		SCHEMA_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/schema.gif");

		TABLE_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/table.png");

		UNBUILT_TABLE_ICON = LiverwortPlugin.imageDescriptorFromPlugin(ID, "icons/unbuilt_table.png");
	}
}

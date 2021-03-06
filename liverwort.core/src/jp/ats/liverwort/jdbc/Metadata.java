package jp.ats.liverwort.jdbc;

/**
 * データベースの主にテーブルに関するメタデータを保持し、参照することが可能であることを表すインターフェイスです。
 *
 * @author 千葉 哲嗣
 * @see MetadataFactory#createMetadatas(Metadata)
 */
public interface Metadata {

	/**
	 * 空配列
	 */
	Metadata[] EMPTY_ARRAY = {};

	/**
	 * パラメータで指定されたスキーマに存在するテーブルを返します。
	 *
	 * @param schemaName 対象となるスキーマ名
	 * @return 存在するテーブルの配列
	 */
	ResourceLocator[] getTables(String schemaName);

	/**
	 * パラメータで指定されたテーブルの、定義情報を返します。
	 *
	 * @param locator 対象となるテーブル
	 * @return テーブル定義情報
	 */
	TableMetadata getTableMetadata(ResourceLocator locator);

	/**
	 * パラメータで指定されたテーブルに存在するカラムの、定義情報を返します。
	 *
	 * @param locator 対象となるテーブル
	 * @return カラム定義情報の配列
	 */
	ColumnMetadata[] getColumnMetadatas(ResourceLocator locator);

	/**
	 * パラメータで指定されたテーブルに存在する主キーを構成する情報を返します。
	 *
	 * @param locator 対象となるテーブル
	 * @return 主キーを構成する情報
	 */
	PrimaryKeyMetadata getPrimaryKeyMetadata(ResourceLocator locator);

	/**
	 * パラメータで指定されたテーブルが外部キー参照しているテーブルを返します。
	 *
	 * @param locator 対象となるテーブル
	 * @return locator が参照しているテーブル
	 */
	ResourceLocator[] getResourcesOfImportedKey(ResourceLocator locator);

	/**
	 * パラメータで指定されたテーブルが外部キー参照されているテーブルを返します。
	 *
	 * @param locator 対象となるテーブル
	 * @return locator が参照されているテーブル
	 */
	ResourceLocator[] getResourcesOfExportedKey(ResourceLocator locator);

	/**
	 * パラメータで指定された両テーブル間の関係情報を返します。
	 *
	 * @param exported 参照されている側のテーブル
	 * @param imported 参照している側のテーブル
	 * @return 両テーブル間の関係情報の配列
	 */
	CrossReference[] getCrossReferences(
		ResourceLocator exported,
		ResourceLocator imported);
}

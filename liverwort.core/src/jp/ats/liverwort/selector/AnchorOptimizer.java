package jp.ats.liverwort.selector;

import java.util.Objects;

import jp.ats.liverwort.internal.U;
import jp.ats.liverwort.jdbc.LiContext;
import jp.ats.liverwort.jdbc.LiResult;
import jp.ats.liverwort.jdbc.ResourceLocator;
import jp.ats.liverwort.sql.Column;
import jp.ats.liverwort.sql.SelectClause;

/**
 * static かつ final なフィールドを ID として {@link ColumnRepository} から SELECT 句構成カラムを取得する {@link Optimizer} です。
 *
 * @author 千葉 哲嗣
 */
public class AnchorOptimizer implements Optimizer {

	private final ResourceLocator hint;

	private final boolean canAddNewEntries;

	private final ValueExtractors extractors = LiContext.get(SelectorConfigure.class).getValueExtractors();

	private final String id;

	private final Class<?> using;

	private final ColumnRepository repository;

	@Override
	public String toString() {
		return U.toString(this);
	}

	AnchorOptimizer(
		AnchorOptimizerFactory factory,
		String id,
		ResourceLocator hint,
		Class<?> using,
		boolean canAddNewEntries) {
		Objects.requireNonNull(factory);
		Objects.requireNonNull(id);

		this.hint = hint;
		this.canAddNewEntries = canAddNewEntries;
		this.id = id;
		this.using = using;

		if (canAddNewEntries) {
			repository = factory.createColumnRepository();
		} else {
			repository = new StrictColumnRepository(factory.createColumnRepository());
		}
	}

	@Override
	public ResourceLocator getResourceLocator() {
		ResourceLocator locator = repository.getResourceLocator(id);
		if (locator == null) {
			if (canAddNewEntries && hint != null) {
				repository.add(id, hint, using);
				return hint;
			}

			throw new IllegalStateException(id + " がリポジトリにありません");
		}

		return locator;
	}

	@Override
	public SelectClause getOptimizedSelectClause() {
		Column[] columns = getColumns();

		if (columns.length == 0) {
			if (canAddNewEntries) return new InitialSelectClause();

			// canAddNewEntries されていないのに 検索項目が 0 件のものはエラーとする
			throw new IllegalStateException("この optimizer は項目が一つもありません");
		}

		SelectClause clause = new SelectClause();
		for (int i = 0; i < columns.length; i++) {
			clause.add(columns[i]);
		}
		return clause;
	}

	@Override
	public SelectedValues convert(LiResult result, Column[] columns) {
		if (canAddNewEntries) return new AddingSelectedValues(
			extractors,
			repository,
			id,
			result,
			columns);

		return new ConcreteSelectedValues(result, columns, extractors);
	}

	private Column[] getColumns() {
		return repository.getColumns(id);
	}

	private static class InitialSelectClause extends SelectClause {

		@Override
		public String toString(boolean joining) {
			if (getColumnsSize() == 0) return "SELECT * ";
			return super.toString(joining);
		}

		@Override
		protected SelectClause createNewInstance() {
			return new InitialSelectClause();
		}
	}

	private class AddingSelectedValues extends ConcreteSelectedValues {

		private final String id;

		private final ColumnRepository repository;

		private AddingSelectedValues(
			ValueExtractors extractors,
			ColumnRepository repository,
			String id,
			LiResult result,
			Column[] columns) {
			super(result, columns, extractors);
			this.repository = repository;
			this.id = id;
		}

		@Override
		public boolean isNull(Column column) {
			try {
				return super.isNull(column);
			} catch (IllegalValueException e) {
				repository.addColumn(id, column, using);
				throw newException(e, id);
			} finally {
				repository.markColumn(id, column, using);
				repository.commit();
			}
		}

		@Override
		public Object getObject(Column column) {
			try {
				return super.getObject(column);
			} catch (IllegalValueException e) {
				repository.addColumn(id, column, using);
				throw newException(e, id);
			} finally {
				repository.markColumn(id, column, using);
				repository.commit();
			}
		}
	}

	private static IllegalValueException newException(IllegalValueException e, String id) {
		return new IllegalValueException(e.getMessage() + U.LINE_SEPARATOR + "新たに " + id + " に追加されました");
	}
}

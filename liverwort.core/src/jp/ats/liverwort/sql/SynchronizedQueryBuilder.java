package jp.ats.liverwort.sql;

import jp.ats.liverwort.jdbc.LiPreparedStatement;

/**
 * SQL の SELECT 文を生成するクラスです。
 *
 * @author 千葉 哲嗣
 */
public class SynchronizedQueryBuilder extends QueryBuilder {

	/**
	 * {@link FromClause} が表すテーブルに対する SELECT 文を生成するインスタンスを生成します。
	 *
	 * @param fromClause FROM 句
	 */
	public SynchronizedQueryBuilder(FromClause fromClause) {
		super(fromClause);
	}

	@Override
	public synchronized void setSelectClause(SelectClause clause) {
		super.setSelectClause(clause);
	}

	@Override
	public synchronized SelectClause getSelectClause() {
		return super.getSelectClause();
	}

	@Override
	public synchronized void setWhereClause(Condition clause) {
		super.setWhereClause(clause);
	}

	@Override
	public synchronized Condition getWhereClause() {
		return super.getWhereClause();
	}

	@Override
	public synchronized void setGroupByClause(GroupByClause clause) {
		super.setGroupByClause(clause);
	}

	@Override
	public synchronized GroupByClause getGroupByClause() {
		return super.getGroupByClause();
	}

	@Override
	public synchronized void setHavingClause(Condition clause) {
		super.setHavingClause(clause);
	}

	@Override
	public synchronized Condition getHavingClause() {
		return super.getHavingClause();
	}

	@Override
	public synchronized void setOrderByClause(OrderByClause clause) {
		super.setOrderByClause(clause);
	}

	@Override
	public synchronized OrderByClause getOrderByClause() {
		return super.getOrderByClause();
	}

	@Override
	public synchronized void setAdjuster(SQLAdjuster adjuster) {
		super.setAdjuster(adjuster);
	}

	@Override
	public synchronized SQLAdjuster getAdjuster() {
		return super.getAdjuster();
	}

	@Override
	public synchronized String toString() {
		return super.toString();
	}

	@Override
	public synchronized int complement(LiPreparedStatement statement) {
		return super.complement(statement);
	}
}

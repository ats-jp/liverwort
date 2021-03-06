package jp.ats.liverwort.jdbc.impl;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import jp.ats.liverwort.internal.U;
import jp.ats.liverwort.jdbc.Configure;
import jp.ats.liverwort.jdbc.LiPreparedStatement;
import jp.ats.liverwort.jdbc.LiResultSet;

/**
 * Liverwort が使用する {@link LiPreparedStatement} の標準実装クラスです。
 *
 * @author 千葉 哲嗣
 */
class ConcretePreparedStatement implements LiPreparedStatement {

	private final Configure config;

	private final PreparedStatement statement;

	/**
	 * インスタンスを生成します。
	 *
	 * @param statement {@link PreparedStatement}
	 */
	ConcretePreparedStatement(Configure config, PreparedStatement statement) {
		this.config = config;
		this.statement = statement;
	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) {
		try {
			statement.setBoolean(parameterIndex, x);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public void setDouble(int parameterIndex, double x) {
		try {
			statement.setDouble(parameterIndex, x);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public void setFloat(int parameterIndex, float x) {
		try {
			statement.setFloat(parameterIndex, x);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public void setInt(int parameterIndex, int x) {
		try {
			statement.setInt(parameterIndex, x);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public void setLong(int parameterIndex, long x) {
		try {
			statement.setLong(parameterIndex, x);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public void setString(int parameterIndex, String x) {
		try {
			statement.setString(parameterIndex, x);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x) {
		try {
			statement.setTimestamp(parameterIndex, x);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x) {
		try {
			statement.setBigDecimal(parameterIndex, x);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public void setObject(int parameterIndex, Object x) {
		try {
			statement.setObject(parameterIndex, x);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream stream, int length) {
		try {
			statement.setBinaryStream(parameterIndex, stream, length);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) {
		try {
			statement.setCharacterStream(parameterIndex, reader, length);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) {
		try {
			statement.setBytes(parameterIndex, x);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public void setBlob(int parameterIndex, Blob blob) {
		try {
			statement.setBlob(parameterIndex, blob);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public void setClob(int parameterIndex, Clob clob) {
		try {
			statement.setClob(parameterIndex, clob);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public void setNull(int parameterIndex, int type) {
		try {
			statement.setNull(parameterIndex, type);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public LiResultSet executeQuery() {
		try {
			return new ConcreteResultSet(config, statement.executeQuery(), this);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public int executeUpdate() {
		try {
			return statement.executeUpdate();
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public boolean execute() {
		try {
			return statement.execute();
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public LiResultSet getResultSet() {
		try {
			return new ConcreteResultSet(config, statement.getResultSet(), this);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public int getUpdateCount() {
		try {
			return statement.getUpdateCount();
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public boolean getMoreResults() {
		try {
			return statement.getMoreResults();
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public void close() {
		U.close(statement);
	}

	@Override
	protected void finalize() {
		close();
	}
}

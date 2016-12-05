package jp.ats.liverwort.j2ee;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import jp.ats.liverwort.ext.Liverwort;
import jp.ats.liverwort.ext.ParsableOptionKey;
import jp.ats.liverwort.ext.TransactionManager;
import jp.ats.liverwort.ext.TransactionShell;
import jp.ats.liverwort.jdbc.LiContext;
import jp.ats.liverwort.jdbc.LiManager;
import jp.ats.liverwort.jdbc.LiTransaction;

/**
 * {@link Filter} の範囲でトランザクションを管理するためのクラスです。
 *
 * @author 千葉 哲嗣
 */
public class LiverwortTransactionFilter implements Filter {

	private static final ThreadLocal<LiTransaction> transaction = new ThreadLocal<>();

	/**
	 * 現在のトランザクションを返します。
	 *
	 * @return 現在のトランザクション
	 */
	public static LiTransaction getTransaction() {
		return transaction.get();
	}

	@Override
	public void init(final FilterConfig config) throws ServletException {
		new Liverwort().start(
			Collections.list(config.getInitParameterNames()).stream().collect(
				Collectors.toConcurrentMap(
					key -> ParsableOptionKey.convert(key),
					key -> ParsableOptionKey.convert(key).parse(config.getInitParameter(key)))));
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
		throws IOException, ServletException {
		if (LiContext.get(LiManager.class).hasConnection()) {
			chain.doFilter(request, response);
			return;
		}

		try {
			TransactionManager.start(new TransactionShell() {

				@Override
				public void execute() throws Exception {
					transaction.set(getTransaction());
					chain.doFilter(request, response);
				}
			});
		} catch (Throwable t) {
			Throwable rootCause = ServletUtilities.getRootCause(t);
			throw new ServletException(rootCause);
		} finally {
			transaction.set(null);
		}
	}

	@Override
	public void destroy() {}
}

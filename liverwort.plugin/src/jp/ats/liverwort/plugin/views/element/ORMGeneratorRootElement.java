package jp.ats.liverwort.plugin.views.element;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;

import jp.ats.liverwort.ext.TransactionManager;
import jp.ats.liverwort.ext.TransactionShell;
import jp.ats.liverwort.jdbc.LiConnection;
import jp.ats.liverwort.jdbc.LiContext;
import jp.ats.liverwort.jdbc.LiManager;

public class ORMGeneratorRootElement implements Element {

	private final SchemaElement[] children;

	public ORMGeneratorRootElement(final String[] schemas) {
		final List<SchemaElement> list = new LinkedList<SchemaElement>();
		try {
			TransactionManager.start(new TransactionShell() {

				@Override
				public void execute() throws Exception {
					LiConnection connection = LiContext.get(LiManager.class)
						.getConnection();
					for (String schema : schemas) {
						list.add(new SchemaElement(connection, schema));
					}
				}
			});
		} catch (Throwable t) {
			throw new IllegalStateException(t);
		}

		children = list.toArray(new SchemaElement[list.size()]);
	}

	public ORMGeneratorRootElement() {
		children = new SchemaElement[] {};
	}

	@Override
	public void addActionToContextMenu(IMenuManager manager) {}

	@Override
	public void doubleClick() {}

	@Override
	public int getCategory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Element[] getChildren() {
		return children;
	}

	@Override
	public Image getIcon() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Element getParent() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasChildren() {
		return false;
	}
}

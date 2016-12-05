package jp.ats.liverwort.plugin.popup.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import jp.ats.liverwort.plugin.Constants;
import jp.ats.liverwort.plugin.LiverwortPlugin;
import jp.ats.liverwort.plugin.LiverwortPlugin.JavaProjectException;
import jp.ats.liverwort.plugin.views.QueryEditorView;
import jp.ats.liverwort.util.U;

public class ChangeProjectHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String id = event.getCommand().getId();
		if ("jp.ats.liverwort.plugin.restartQueryEditor".equals(id)) {
			Starter.start("jp.ats.liverwort.plugin.queryEditorView");
		} else if ("jp.ats.liverwort.plugin.restartORMGenerator".equals(id)) {
			Starter.start("jp.ats.liverwort.plugin.classBuilderView");
		} else {
			throw new IllegalStateException(id);
		}

		QueryEditorView queryEditor = LiverwortPlugin.getDefault()
			.getQueryEditorView();
		if (queryEditor != null && queryEditor.hasEdit()) {
			if (!MessageDialog.openConfirm(
				null,
				Constants.TITLE,
				"定義情報を再度読み込み直します"
					+ U.LINE_SEPARATOR
					+ "（保存されていない変更は失われてしまいます）"))
				return null;
		}

		ISelection selection = HandlerUtil.getActiveMenuSelection(event);

		if (selection == null) return null;

		IStructuredSelection structured = (IStructuredSelection) selection;
		Object element = structured.getFirstElement();
		if (element == null) return null;

		if (!(element instanceof IJavaProject)) return null;

		IJavaProject project = (IJavaProject) element;

		try {
			LiverwortPlugin.getDefault().setProjectAndRefresh(project);
		} catch (JavaProjectException e) {
			MessageDialog.openError(
				null,
				Constants.TITLE,
				"設定に問題があります" + U.LINE_SEPARATOR + e.getMessage());
		}

		return null;
	}
}

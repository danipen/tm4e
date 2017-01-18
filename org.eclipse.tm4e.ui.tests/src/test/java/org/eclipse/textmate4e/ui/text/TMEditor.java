package org.eclipse.textmate4e.ui.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.textmate4e.ui.internal.text.Command;
import org.eclipse.textmate4e.ui.internal.text.DocumentReplaceCommand;
import org.eclipse.textmate4e.ui.internal.text.DocumentSetCommand;
import org.eclipse.textmate4e.ui.internal.text.StyleRangesCollector;
import org.eclipse.textmate4e.ui.internal.text.TextViewerInvalidateTextPresentationCommand;
import org.eclipse.tm4e.core.grammar.IGrammar;
import org.eclipse.tm4e.ui.text.TMPresentationReconciler;
import org.eclipse.tm4e.ui.themes.ITokenProvider;

public class TMEditor {

	private final TextViewer viewer;
	private final Document document;
	private final Shell shell;
	private StyleRangesCollector collector;

	private final List<ICommand> commands;
	private TMPresentationReconciler reconciler;

	public TMEditor(IGrammar grammar, ITokenProvider tokenProvider, String text) {
		shell = new Shell();
		viewer = new TextViewer(shell, SWT.NONE);
		document = new Document();
		viewer.setDocument(document);
		commands = new ArrayList<>();
		collector = new StyleRangesCollector();

		setAndExecute(text);

		reconciler = new TMPresentationReconciler();
		reconciler.addTMPresentationReconcilerListener(collector);
		reconciler.setGrammar(grammar);
		reconciler.setTokenProvider(tokenProvider);
		reconciler.install(viewer);

	}

	public void set(String text) {
		commands.add(new DocumentSetCommand(text, document));
	}

	private void setAndExecute(String text) {
		Command command = new DocumentSetCommand(text, document);
		commands.add(command);
		collector.setCommand(command);
	}

	public void replace(int pos, int length, String text) throws BadLocationException {
		commands.add(new DocumentReplaceCommand(pos, length, text, document));
	}

	/**
	 * Invalidates the given range of the text presentation.
	 *
	 * @param offset
	 *            the offset of the range to be invalidated
	 * @param length
	 *            the length of the range to be invalidated
	 * 
	 */
	public void invalidateTextPresentation(int offset, int length) {
		commands.add(new TextViewerInvalidateTextPresentationCommand(offset, length, viewer));
	}

	public List<ICommand> execute() {		
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (ICommand command : commands) {
					collector.executeCommand((Command) command);
				}
				shell.getDisplay().syncExec(new Runnable() {

					@Override
					public void run() {
						shell.dispose();
					}
				});
			}
		}).start();
		
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		reconciler.uninstall();
		return commands;
	}

}

/**
 *  Copyright (c) 2015-2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.tm4e.languageconfiguration.internal.widgets;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Sort the selected column and refresh the viewer.
 *
 */
public class ColumnSelectionAdapter extends SelectionAdapter {

	private final TableColumn fTableColumn;
	private final TableViewer tableViewer;
	private final int fColumnIndex;
	private final ColumnViewerComparator viewerComparator;

	public ColumnSelectionAdapter(TableColumn column, TableViewer tableViewer, int index, ColumnViewerComparator vc) {
		fTableColumn = column;
		this.tableViewer = tableViewer;
		fColumnIndex = index;
		viewerComparator = vc;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		viewerComparator.setColumn(fColumnIndex);
		int dir = viewerComparator.getDirection();
		Table table = tableViewer.getTable();
		table.setSortDirection(dir);
		table.setSortColumn(fTableColumn);
		tableViewer.refresh();
	}
}
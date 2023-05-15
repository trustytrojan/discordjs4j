package swing_extensions;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

public abstract class ReorderableJList<T> extends MyList<T> {
	public ReorderableJList() {
		setDragEnabled(true);
		setDropMode(DropMode.INSERT);
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		setTransferHandler(new TransferHandler() {
			@Override
			public int getSourceActions(JComponent c) {
				return MOVE;
			}

			@Override
			protected Transferable createTransferable(JComponent c) {
				if (c != ReorderableJList.this)
					throw new IllegalArgumentException();
				final var selectedIndices = getSelectedIndices();
				final var rangeStr = "" + selectedIndices[0] + '-' + selectedIndices[selectedIndices.length - 1];
				return new StringSelection(rangeStr);
			}

			@Override
			public boolean canImport(TransferSupport support) {
				return support.isDrop() && support.isDataFlavorSupported(DataFlavor.stringFlavor);
			}

			@Override
			public boolean importData(TransferSupport support) {
				if (support.getComponent() != ReorderableJList.this) {
					throw new IllegalArgumentException();
				}

				final int from, to;

				try {
					final var transferable = support.getTransferable();
					final var sourceRangeStr = (String) transferable.getTransferData(DataFlavor.stringFlavor);
					final var sourceRangeStrs = sourceRangeStr.split(",");
					from = Integer.parseInt(sourceRangeStrs[0]);
					to = Integer.parseInt(sourceRangeStrs[1]);
				} catch (UnsupportedFlavorException | IOException e) {
					e.printStackTrace();
					return false;
				}

				final var targetIndex = ((JList.DropLocation) support.getDropLocation()).getIndex();

				if (from != targetIndex && targetIndex != model.getSize()) {
					final var sourceElements = model.subList(from, to);
					model.removeAll(sourceElements);
					model.addAll(targetIndex, sourceElements);

					setSelectedIndex(targetIndex);
					ensureIndexIsVisible(targetIndex);
					return true;
				}

				return false;
			}
		});
	}
}

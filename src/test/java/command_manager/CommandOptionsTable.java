package command_manager;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import discord.structures.ApplicationCommandOption;
import swing_extensions.MyTable;

final class CommandOptionsTable extends MyTable {
	CommandOptionsTable() {
		super("Type", "Name", "Description", "Required?");
		setEditable(true);

		typeColumnSetup();
		requiredColumnSetup();
		dropdownMenuSetup();
	}

	void clearAndFill(final List<ApplicationCommandOption> options) {
		clear();
		options.forEach(o -> addRow(o.type, o.name, o.description, o.required));
	}

	private void dropdownMenuSetup() {
		final var dropdown = new JPopupMenu();
		dropdown.add("Delete").addActionListener((final var e) -> removeRow(getSelectedRow()));

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (e.isPopupTrigger())
					dropdown.setVisible(true);
			}
		});
	}

	private void typeColumnSetup() {
		final var typeColumn = columnModel.getColumn(0);
		typeColumn.setCellRenderer(new CommandOptionTypeComboBox());
		typeColumn.setCellEditor(new DefaultCellEditor(new CommandOptionTypeComboBox()));
	}

	private void requiredColumnSetup() {
		final var requiredColumn = columnModel.getColumn(3);
		requiredColumn.setCellRenderer(new CheckBoxRenderer());
		requiredColumn.setCellEditor(new DefaultCellEditor(new JCheckBox()));
	}

	private static class CommandOptionTypeComboBox extends JComboBox<ApplicationCommandOption.Type> implements TableCellRenderer {
		CommandOptionTypeComboBox() {
			Stream.of(ApplicationCommandOption.Type.values()).forEach(this::addItem);
		}
	
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			setSelectedItem(value);
			return this;
		}
	}

	private static class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			setSelected((value == null) ? false : (boolean) value);
			return this;
		}
	}
}

package command_manager;

import java.awt.Component;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import discord.structures.ApplicationCommandOption;

public class CommandOptionsTable extends MyTable {
	CommandOptionsTable() {
		super("Type", "Name", "Description", "Required?");
		setEditable(true);
		typeColumnSetup();
		requiredColumnSetup();
		dropdownMenuSetup();
	}

	void clearAndFill(final List<ApplicationCommandOption> options) {
		clear();
		for (final var option : options) {
			addRow(option.type, option.name, option.description, option.required);
		}
	}

	private void dropdownMenuSetup() {
		final var dropdown = new JPopupMenu();
		dropdown.add("Delete").addActionListener((final var e) -> removeRow(getSelectedRow()));
	}

	private void typeColumnSetup() {
		final var typeColumn = columnModel.getColumn(0);

		typeColumn.setCellRenderer(new DefaultTableCellRenderer() {
			private final CommandOptionTypeDropdown renderer = new CommandOptionTypeDropdown();

			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				renderer.setSelectedItem(value);
				return renderer;
			}
		});

		typeColumn.setCellEditor(new DefaultCellEditor(new CommandOptionTypeDropdown()));
	}

	private void requiredColumnSetup() {
		final var requiredColumn = columnModel.getColumn(3);

		requiredColumn.setCellRenderer(new DefaultTableCellRenderer() {
			private final JCheckBox renderer = new JCheckBox();

			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				renderer.setSelected((value == null) ? false : ((Boolean) value));
				return renderer;
			}
		});

		requiredColumn.setCellEditor(new DefaultCellEditor(new JCheckBox()));
	}
}

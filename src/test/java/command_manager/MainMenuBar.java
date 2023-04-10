package command_manager;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

final class MainMenuBar extends JMenuBar {
	private static final JMenu commandMenu = new JMenu("Command");
	private static final JMenuItem createCommand = commandMenu.add("Create...");

	static final MainMenuBar instance = new MainMenuBar();

	static {
		SwingUtils.onAction(createCommand, (e) -> {
			CommandDialog.clearInputs();
			CommandDialog.show();
		});

		instance.add(commandMenu);
	}

	private MainMenuBar() {}
}

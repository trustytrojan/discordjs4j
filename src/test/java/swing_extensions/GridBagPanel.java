package swing_extensions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class GridBagPanel extends MyPanel {
	private static final Insets INSETS_5 = new Insets(5, 5, 5, 5);

	public static GridBagConstraints constraintsInsets5() {
		final var c = new GridBagConstraints();
		c.insets = INSETS_5;
		return c;
	}

	public GridBagPanel() {
		super(new GridBagLayout());
	}
}

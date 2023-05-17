package swing_extensions;

import java.util.stream.Stream;

import javax.swing.JButton;

public class BottomButtonsPanel extends RightFlowPanel {
	public BottomButtonsPanel(JButton... buttons) {
		Stream.of(buttons).forEach(this::add);
	}
}

package swing_extensions;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public final class NumericTextField extends JTextField {
	public NumericTextField() {
		((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
					throws BadLocationException {
				fb.insertString(offset, text.replaceAll("\\D", ""), attr); // Allow only digits
			}

			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
					throws BadLocationException {
				fb.replace(offset, length, text.replaceAll("\\D", ""), attrs); // Allow only digits
			}
		});
	}
}

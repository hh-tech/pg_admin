package com.hhdb.csadmin.common.ui;

import java.awt.Toolkit;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import com.hh.frame.common.log.LM;

public class BaseTextField extends JTextField {
	private static final long serialVersionUID = 1L;
	private int maxLength;
	private Toolkit toolkit;
	private NumberFormat integerFormatter;

	public BaseTextField() {
		this(-1, false);
	}

	public BaseTextField(boolean alignLeft) {
		this(-1, alignLeft);
	}

	public BaseTextField(int maxLength) {
		this(maxLength, false);
	}

	public BaseTextField(int maxLength, boolean alignLeft) {
		super();
		this.maxLength = maxLength;
		toolkit = Toolkit.getDefaultToolkit();
		integerFormatter = NumberFormat.getNumberInstance();
		integerFormatter.setParseIntegerOnly(true);
		setHorizontalAlignment(alignLeft ? JTextField.LEFT : JTextField.RIGHT);
	}

	public int getValue() {
		int retVal = 0;
		try {
			retVal = integerFormatter.parse(getText()).intValue();
		} catch (ParseException e) {
			LM.error(LM.Model.CS.name(), e);
			toolkit.beep();
		}
		return retVal;
	}

	public String getStringValue() {
		return Integer.toString(getValue());
	}

	public int getCharsLength() {
		return getText().length();
	}

	public boolean isZero() {
		return getValue() == 0;
	}

	public void setValue(int value) {
		setText(integerFormatter.format(value));
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	protected Document createDefaultModel() {
		return new WholeNumberDocument();
	}

	protected class WholeNumberDocument extends PlainDocument {
		private static final long serialVersionUID = 1L;

		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			if (maxLength != -1) {
				if (getLength() >= maxLength) {
					toolkit.beep();
					return;
				}
			}
			char[] source = str.toCharArray();
			char[] result = new char[source.length];
			int j = 0;
			for (int i = 0; i < result.length; i++) {
				if (Character.isDigit(source[i]) || (offs == 0 && i == 0 && source[i] == '-')) {
					result[j++] = source[i];
				} else {
					toolkit.beep();
				}
			}
			super.insertString(offs, new String(result, 0, j), a);
		}
	}

	public String getEditorValue() {
		return getStringValue();
	}
}

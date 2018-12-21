package com.x.collaboration.core.message.dialog;

public class TextMessage extends DialogMessage {

	private String text;

	public TextMessage(String from, String person, String text) {
		super(DialogType.text, from, person);
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}

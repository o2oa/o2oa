package com.x.collaboration.core.message.dialog;

public class FileMessage extends DialogMessage {

	private String folder;
	private String attachment;

	public FileMessage(String from, String person, String folder, String attachment) {
		super(DialogType.text, from, person);
		this.folder = folder;
		this.attachment = attachment;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}


}

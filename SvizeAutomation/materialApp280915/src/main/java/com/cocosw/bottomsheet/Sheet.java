package com.cocosw.bottomsheet;

public class Sheet {
	private String text;
	private int iconId;

	public Sheet(String text, int id) {
		this.text = text;
		this.iconId = id;
	}

	public String getText() {
		return text;
	}

	public int getIconId() {
		return iconId;
	}

}

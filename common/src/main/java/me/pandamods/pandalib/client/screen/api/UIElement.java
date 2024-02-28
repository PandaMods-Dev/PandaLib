package me.pandamods.pandalib.client.screen.api;

public interface UIElement {
	Widget parent();

	int getLocalX();
	int getLocalY();
	int width();
	int height();

	default int getX() {
		if (parent() != null)
			return getLocalX() + parent().getX();
		return getLocalX();
	}
	default int getY() {
		if (parent() != null)
			return getLocalY() + parent().getY();
		return getLocalY();
	}
}

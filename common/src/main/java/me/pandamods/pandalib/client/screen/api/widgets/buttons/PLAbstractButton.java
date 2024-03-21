package me.pandamods.pandalib.client.screen.api.widgets.buttons;

import me.pandamods.pandalib.client.screen.api.PLScreen;
import me.pandamods.pandalib.client.screen.api.UIElement;
import me.pandamods.pandalib.client.screen.api.Widget;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.Component;

public abstract class PLAbstractButton extends AbstractButton implements UIElement {
	private PLScreen screen;
	private Widget parent;

	public PLAbstractButton(int x, int y, int height, int width, Component component) {
		super(x, y, height, width, component);
	}

	@Override
	public Widget getParent() {
		return parent;
	}

	@Override
	public void setParent(Widget parent) {
		this.parent = parent;
	}

	@Override
	public PLScreen getScreen() {
		return screen;
	}

	@Override
	public void setScreen(PLScreen screen) {
		this.screen = screen;
	}

	@Override
	public int width() {
		return width();
	}

	@Override
	public int height() {
		return height();
	}
}

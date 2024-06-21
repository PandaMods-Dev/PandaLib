/*
 * Copyright (C) 2024 Oliver Froberg (The Panda Oliver)
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 * You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package me.pandamods.pandalib.api.client.screen.elements;

import me.pandamods.pandalib.api.client.screen.PLRenderable;
import me.pandamods.pandalib.api.utils.screen.PLGuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratableEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UIElementHolder extends AbstractUIElement implements UIElementHolderAccessor, PLRenderable {
	private final List<UIElement> children = new ArrayList<>();
	private final List<NarratableEntry> narratables = new ArrayList<>();
	private final List<UIElementHolder> holders = new ArrayList<>();
	private final List<PLRenderable> renderables = new ArrayList<>();

	@Override
	public <T extends UIElement> T addElement(T element) {
		element.setParent(this);
		element.setScreen(getScreen());
		return UIElementHolderAccessor.super.addElement(element);
	}

	@Override
	public List<UIElement> getChildren() {
		return children;
	}

	@Override
	public List<NarratableEntry> getNarratables() {
		return narratables;
	}

	@Override
	public List<PLRenderable> getRenderables() {
		return renderables;
	}

	@Override
	public List<UIElementHolder> getHolders() {
		return holders;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		if (!isVisible())
			return false;
		if (super.isMouseOver(mouseX, mouseY))
			return true;
		return isOutOfBoundsInteractionAllowed() && this.getElementAt(mouseX, mouseY).isPresent();
	}

	public boolean isOutOfBoundsInteractionAllowed() {
		return true;
	}

	@Override
	public void tick() {
		this.children.forEach(UIElement::tick);
	}

	@Override
	public void render(PLGuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		if (isVisible()) {
			UIElementHolderAccessor.super.render(guiGraphics, mouseX, mouseY, partialTick);
		}
	}
}
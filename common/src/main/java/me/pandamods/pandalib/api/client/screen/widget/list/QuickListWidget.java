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

package me.pandamods.pandalib.api.client.screen.widget.list;

import me.pandamods.pandalib.api.client.screen.elements.UIElementHolder;
import me.pandamods.pandalib.api.client.screen.layouts.PLGrid;
import me.pandamods.pandalib.api.client.screen.layouts.PLLayout;
import me.pandamods.pandalib.api.utils.screen.PLGuiGraphics;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QuickListWidget extends UIElementHolder {
	private final int rows;
	public final PLLayout[] elements;

	private QuickListWidget(int rows, PLLayout[] elements) {
		this.rows = rows;
		this.elements = elements;
	}

	@Override
	public void init() {
		PLGrid grid = this.addElement(new PLGrid());
		PLGrid.RowHelper helper = grid.createRowHelper(this.rows);
		for (PLLayout element : elements) {
			helper.addChild(element);
		}
		grid.arrangeElements();
		this.setSize(grid.getWidth(), grid.getHeight());
	}

	@Override
	public void render(PLGuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		if (this.isVisible()) {
			guiGraphics.fill(this.getX() - 1, this.getY() - 1,
					this.getX() + this.getWidth() + 1, this.getY() + this.getHeight() + 1, Color.WHITE.getRGB());

			guiGraphics.fill(this.getX(), this.getY(),
					this.getX() + this.getWidth(), this.getY() + this.getHeight(), Color.BLACK.getRGB());
		}
		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}

	public static Builder builder(int rows) {
		return new Builder(rows);
	}

	public static class Builder {
		private final int rows;
		private final List<PLLayout> elements;

		private Builder(int rows) {
			this.rows = rows;
			this.elements = new ArrayList<>();
		}

		public Builder addElement(PLLayout element) {
			this.elements.add(element);
			return this;
		}

		public Builder addElements(Collection<? extends PLLayout> elements) {
			this.elements.addAll(elements);
			return this;
		}

		public QuickListWidget build() {
			return new QuickListWidget(rows, elements.toArray(new PLLayout[0]));
		}
	}
}

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

package me.pandamods.pandalib.api.client.screen.config.option;

import me.pandamods.pandalib.api.client.screen.elements.widgets.buttons.ToggleButton;
import me.pandamods.pandalib.api.client.screen.layouts.PLGrid;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;

public class BooleanOption extends AbstractConfigOption<Boolean> {
	private ToggleButton button;

	public BooleanOption(Component name, Field field) {
		super(name, field);
		button = new ToggleButton(0, 0, 20);
	}

	@Override
	protected void setValue(Boolean value) {
		button.setState(value);
	}

	@Override
	protected Boolean getValue() {
		return button.getState();
	}

	@Override
	public void init() {
		PLGrid grid = this.addElement(new PLGrid()).spacing(2);
		grid.defaultCellSetting().alignVerticallyMiddle();

		grid.addChild(button, 0, 0);
		addActionButtons(grid, 2);

		grid.arrangeElements();
		super.init();
	}
}
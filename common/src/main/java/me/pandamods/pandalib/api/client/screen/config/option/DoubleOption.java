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

import me.pandamods.pandalib.api.client.screen.elements.widgets.PLEditBox;
import me.pandamods.pandalib.api.client.screen.layouts.PLGrid;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class DoubleOption extends AbstractConfigOption<Double> {
	private PLEditBox inputField;

	public DoubleOption(Component name, Field field) {
		super(name, field);
		inputField = new PLEditBox(Minecraft.getInstance().font, name);
		inputField.setPosition(0, 0);
		inputField.setSize(100, 16);
	}

	@Override
	protected void setValue(Double value) {
		inputField.setValue(String.valueOf(value));
	}

	@Override
	protected Double getValue() {
		return Double.valueOf(inputField.getValue());
	}

	@Override
	public void init() {
		PLGrid grid = this.addElement(new PLGrid()).spacing(2);
		grid.defaultCellSetting().alignVerticallyMiddle();

		grid.addChild(inputField = new PLEditBox(Minecraft.getInstance().font, this.inputField, this.name), 0, 0);
		this.inputField.setMaxLength(Integer.MAX_VALUE);
		this.inputField.setFilter(s -> Pattern.compile("-?[0-9]*.?[0-9]*").matcher(s).matches());

		addActionButtons(grid, 2);

		grid.arrangeElements();
		super.init();
	}
}
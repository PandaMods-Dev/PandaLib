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

import me.pandamods.pandalib.api.client.screen.elements.widgets.inputs.SliderInput;
import me.pandamods.pandalib.api.client.screen.layouts.PLGrid;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;

public class FloatOption extends AbstractConfigOption<Float> {
//	private PLEditBox inputField;
	private final SliderInput slider;

	public FloatOption(Component name, Field field) {
		super(name, field);
//		inputField = new PLEditBox(Minecraft.getInstance().font, name);
//		inputField.setPosition(0, 0);
//		inputField.setSize(100, 16);

		slider = new SliderInput(0, 10, 0.25f);
		slider.setPosition(0, 0);
		slider.setSize(150, 16);
	}

	@Override
	protected void setValue(Float value) {
		slider.setValue(value);
	}

	@Override
	protected Float getValue() {
		return (float) slider.getValue();
	}

	@Override
	public void init() {
		PLGrid grid = this.addElement(new PLGrid()).spacing(2);
		grid.defaultCellSetting().alignVerticallyMiddle();

//		grid.addChild(inputField = new PLEditBox(Minecraft.getInstance().font, this.inputField, this.name), 0, 0);
//		this.inputField.setMaxLength(Integer.MAX_VALUE);
//		this.inputField.setFilter(s -> Pattern.compile("-?[0-9]*.?[0-9]*").matcher(s).matches());

		grid.addChild(slider, 0, 0);

		addActionButtons(grid, 2);

		grid.arrangeElements();
		super.init();
	}
}
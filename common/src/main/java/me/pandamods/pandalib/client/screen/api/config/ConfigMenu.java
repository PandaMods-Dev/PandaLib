package me.pandamods.pandalib.client.screen.api.config;

import me.pandamods.pandalib.client.screen.api.PLScreen;
import me.pandamods.pandalib.utils.animation.interpolation.NumberInterpolator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.Set;

public class ConfigMenu extends PLScreen {
	private ConfigCategoryList categoryList = new ConfigCategoryList(this);

	protected ConfigMenu(Component title) {
		super(title);
	}

	@Override
	protected void init() {
		this.addElement(categoryList);
		super.init();
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderDirtBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}
}
package me.pandamods.pandalib.api.client.screen.config.option;

import me.pandamods.pandalib.api.client.screen.component.UIComponent;
import me.pandamods.pandalib.api.client.screen.component.UIComponentHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ConfigOption<T> extends UIComponentHolder {
	public final net.minecraft.network.chat.Component name;
	protected final Supplier<T> load;
	protected final Consumer<T> save;
	protected final Supplier<T> loadDefault;

	public ConfigOption(net.minecraft.network.chat.Component name, Supplier<T> load, Consumer<T> save, Supplier<T> loadDefault) {
		this.name = name;
		this.load = load;
		this.save = save;
		this.loadDefault = loadDefault;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		Font font = Minecraft.getInstance().font;
		guiGraphics.drawString(font, name, this.getX() + 2, this.getY() + (this.getHeight() - font.lineHeight) / 2, 0xFFFFFF);

		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}

	@Override
	protected void init() {
		super.init();
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int getWidth() {
		return this.getParent().map(UIComponent::getWidth).orElse(0);
	}

	@Override
	public int getHeight() {
		return 20;
	}

	protected abstract void setValue(T value);
	protected abstract T getValue();

	public void save() {
		this.save.accept(this.getValue());
	}

	public void load() {
		this.setValue(this.load.get());
	}

	public void reset() {
		this.setValue(this.loadDefault.get());
	}
}

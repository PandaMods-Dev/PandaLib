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

package me.pandamods.pandalib.api.client.screen.config.menu;

import me.pandamods.pandalib.api.client.screen.PLScreen;
import me.pandamods.pandalib.api.client.screen.elements.UIElementHolder;
import me.pandamods.pandalib.api.client.screen.config.category.AbstractConfigCategory;
import me.pandamods.pandalib.api.client.screen.config.category.ConfigCategory;
import me.pandamods.pandalib.api.client.screen.elements.widgets.buttons.AbstractPLButton;
import me.pandamods.pandalib.api.client.screen.elements.widgets.buttons.PLButton;
import me.pandamods.pandalib.api.client.screen.layouts.PLGrid;
import me.pandamods.pandalib.api.client.screen.layouts.PLLayout;
import me.pandamods.pandalib.api.client.screen.layouts.PLSpacerElement;
import me.pandamods.pandalib.api.client.screen.widget.list.QuickListWidget;
import me.pandamods.pandalib.api.config.ConfigData;
import me.pandamods.pandalib.api.config.PandaLibConfig;
import me.pandamods.pandalib.api.config.holders.ConfigHolder;
import me.pandamods.pandalib.api.utils.PLCommonComponents;
import me.pandamods.pandalib.api.utils.screen.PLGuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import java.awt.*;

public class ConfigMenu<T extends ConfigData> extends PLScreen {
	private final Screen parent;
	private final CategoryList categoryList = new CategoryList();
	private final CategoryAddress addressBar = new CategoryAddress();
	private final ConfigHolder<T> configHolder;
	private AbstractConfigCategory category;
	private final AbstractConfigCategory rootCategory;
	
	public ConfigMenu(Screen parent, Class<T> config, AbstractConfigCategory category) {
		this(parent, PandaLibConfig.getConfig(config), category);
	}

	protected ConfigMenu(Screen parent, ConfigHolder<T> configHolder, AbstractConfigCategory category) {
		this.parent = parent;
		this.configHolder = configHolder;
		this.category = this.rootCategory = category;

		this.load();
	}

	@Override
	public Component getTitle() {
		return getCategory().getName();
	}

	@Override
	public void init() {
		this.addressBar.setSize(this.getWidth(), 20);
//		this.addElement(this.addressBar);

		this.categoryList.setPosition(0, this.addressBar.getHeight());
		this.categoryList.setSize(100, this.getHeight() - this.categoryList.getY());
		this.addElement(this.categoryList);

		this.category.setPosition(this.categoryList.getWidth(), this.addressBar.getHeight());
		this.category.setSize(this.getWidth() - this.category.getX(), this.getHeight() - this.category.getY());
//		this.addElement(this.category);

		PLGrid actionGrid = new PLGrid();
		actionGrid.spacing(4).defaultCellSetting();
		PLGrid.ColumnHelper actionHelper = actionGrid.createColumnHelper(1);

		actionHelper.addChild(PLButton.builder(PLCommonComponents.SAVE, button -> this.save()).width(50).build());
		actionHelper.addChild(PLButton.builder(PLCommonComponents.RESET, button -> this.reset()).width(50).build());
		actionHelper.addChild(PLButton.builder(PLCommonComponents.CLOSE, button -> this.close()).width(50).build());

		actionGrid.visitChildren(this::addElement);
		actionGrid.arrangeElements();
	}

	@Override
	public void render(PLGuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(guiGraphics, mouseX, mouseY, partialTick);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}

	public void save() {
		this.rootCategory.save();
		this.configHolder.save();
		this.close();
	}

	public void load() {
		this.rootCategory.load();
	}

	public void reset() {
		this.rootCategory.reset();
	}

	public void setCategory(AbstractConfigCategory category) {
		this.category = category;
		this.rebuildWidgets();
	}

	public AbstractConfigCategory getCategory() {
		return category;
	}

	@Override
	public void close() {
		this.getMinecraft().setScreen(parent);
	}

	public class CategoryList extends UIElementHolder {
		@Override
		public void init() {
			PLGrid grid = this.addElement(new PLGrid()).spacing(2);
			PLGrid.RowHelper helper = grid.createRowHelper(1);
			for (AbstractConfigCategory category : ConfigMenu.this.getCategory().getCategories()) {
				helper.addChild(PLButton.builder(category.getName(), button -> setCategory(category)).width(90).build());
			}
			grid.arrangeElements();
			super.init();
		}

		@Override
		public void render(PLGuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
			guiGraphics.renderSeparatorLine(this.maxX(), this.getRelativeY(), this.height, true);
			super.render(guiGraphics, mouseX, mouseY, partialTick);
		}
	}

	public class CategoryAddress extends UIElementHolder {
		@Override
		public void init() {
			PLGrid grid = this.addElement(new PLGrid()).spacing(2);
			grid.defaultCellSetting().alignVerticallyMiddle();
			PLGrid.ColumnHelper helper = grid.createColumnHelper(1);

			AbstractConfigCategory previousCategory = ConfigMenu.this.getCategory().getParentCategory();
			PLButton backButton = PLButton.builder(PLCommonComponents.BACK, button -> setCategory(previousCategory)).build();
			backButton.setSize(50, this.getHeight() - 4);
			backButton.setActive(previousCategory != null);
			helper.addChild(backButton);
			helper.addChild(PLSpacerElement.width(4));

			addCategoryDist(helper, ConfigMenu.this.category);
			grid.arrangeElements();
			super.init();
		}

		private void addCategoryDist(PLGrid.ColumnHelper helper, AbstractConfigCategory abstractCategory) {
			AbstractConfigCategory parentCategory = abstractCategory.getParentCategory();
			if (parentCategory != null)
				addCategoryDist(helper, parentCategory);

			helper.addChild(new CategoryButton(abstractCategory));
			if (abstractCategory instanceof ConfigCategory category2 && !category2.getCategories().isEmpty())
				helper.addChild(new CategoryArrowButton(category2));
		}

		@Override
		public void render(PLGuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
			guiGraphics.renderSeparatorLine(getRelativeX(), maxY(), getWidth(), false);
			super.render(guiGraphics, mouseX, mouseY, partialTick);
		}

		private class CategoryButton extends AbstractPLButton {
			private final Font font;
			private final AbstractConfigCategory category;

			public CategoryButton(AbstractConfigCategory category) {
				super(category.getName());
				this.category = category;
				this.font = Minecraft.getInstance().font;
				this.setSize(font.width(getMessage()), 20);
				this.width = font.width(getMessage());
			}

			@Override
			public void render(PLGuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
				this.checkHoverState(mouseX, mouseY);
				int y = this.getY() + (this.height - this.font.lineHeight) / 2;
				guiGraphics.drawString(this.font, getMessage(), this.getX(), y, Color.white.getRGB());

				y += this.font.lineHeight;
				if (this.isHoveredOrFocused())
					guiGraphics.fill(this.getX(), y, this.getX() + this.width, y + 1, Color.white.getRGB());
			}

			@Override
			public void onPress() {
				ConfigMenu.this.setCategory(category);
			}
		}

		private class CategoryArrowButton extends UIElementHolder {
			private final Font font;
			private final QuickListWidget categoryList;

			public CategoryArrowButton(ConfigCategory category) {
				this.font = Minecraft.getInstance().font;
				this.width = font.width(">");
				this.height = 20;

				this.categoryList = QuickListWidget.builder(1)
						.addElements(category.getCategories().stream().map(child ->
								PLButton.builder(child.getName(), button -> ConfigMenu.this.setCategory(child))
										.size(150, font.lineHeight + 8)
										.build()
						).toList()).build();
			}

			@Override
			public void init() {
				this.addElement(this.categoryList);
				this.categoryList.setVisible(false);
				this.categoryList.setPosition(this.getX(), this.getY() + this.getHeight() + 2);
			}

			@Override
			public void render(PLGuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
				this.checkHoverState(mouseX, mouseY);
				int y = this.getRelativeY() + (this.height - this.font.lineHeight) / 2;
				guiGraphics.drawString(this.font, ">", this.getRelativeX(), y, Color.white.getRGB());

				y += this.font.lineHeight;
				if (this.isHoveredOrFocused())
					guiGraphics.fill(this.getRelativeX(), y, this.getRelativeX() + this.width, y + 1, Color.white.getRGB());

				this.categoryList.setVisible(this.isFocused());

				guiGraphics.pose().pushPose();
				guiGraphics.pose().translate(0,0, 999);
				super.render(guiGraphics, mouseX, mouseY, partialTick);
				guiGraphics.pose().popPose();
			}

			protected void onPress() {
				this.playDownSound(Minecraft.getInstance().getSoundManager());
			}

			@Override
			public boolean isFocused() {
				for (PLLayout element : this.categoryList.elements) {
					if (element instanceof GuiEventListener listener && listener.isFocused())
						return true;
				}
				return super.isFocused() || this.categoryList.isFocused();
			}

			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (super.mouseClicked(mouseX, mouseY, button))
					return true;

				if (this.isActiveAndVisible()) {
					if (this.isValidClickButton(button)) {
						this.onPress();
						return true;
					}
					return false;
				}
				return false;
			}

			@Override
			public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
				if (super.keyReleased(keyCode, scanCode, modifiers))
					return true;

				if (!this.isActiveAndVisible())
					return false;
				if (CommonInputs.selected(keyCode)) {
					this.onPress();
					return true;
				}
				return false;
			}

			public void playDownSound(SoundManager handler) {
				handler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
			}

			protected boolean isValidClickButton(int button) {
				return button == 0;
			}
		}
	}
}
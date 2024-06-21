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

import me.pandamods.pandalib.api.utils.ScreenUtils;
import me.pandamods.pandalib.api.utils.screen.PLGuiGraphics;
import org.joml.Math;

import java.awt.*;

public class ScrollPane extends UIElementHolder {
	private static final int SCROLLBAR_BACKGROUND_COLOR = new Color(0, 0, 0, 150).getRGB();
	private static final int SCROLLBAR_COLOR_DARK = new Color(100, 100, 100, 150).getRGB();
	private static final int SCROLLBAR_COLOR = new Color(150, 150, 150, 150).getRGB();

	public int scrollbarWidth = 4;
	public int scrollbarHeight = 4;

	public float scrollXDistance = 0;
	public float scrollYDistance = 0;

	private boolean scrollingX = false;
	private boolean scrollingY = false;

	private final UIElementHolder viewport;

	public ScrollPane(UIElementHolder viewport) {
		this.viewport = viewport;
		this.viewport.setRelativeX(0);
		this.viewport.setRelativeY(0);
		this.viewport.setSize(this.getWidth(), this.getHeight());
		this.addElement(viewport);
	}

	public UIElementHolder getViewport() {
		return viewport;
	}

	public int getContentWidth() {
		return viewport.getWidth();
	}

	public int getContentHeight() {
		return viewport.getHeight();
	}

	public boolean isScrollbarXShown() {
		return getContentWidth() > this.getWidth();
	}

	public boolean isScrollbarYShown() {
		return getContentHeight() > this.getHeight();
	}

	public void applyScrollLimits() {
		this.scrollXDistance = Math.clamp(0, getContentWidth() - this.getWidth(), scrollXDistance);
		this.scrollYDistance = Math.clamp(0, getContentHeight() - this.getHeight(), scrollYDistance);
	}

	@Override
	public void render(PLGuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.getViewport().setRelativeX((int) -scrollXDistance);
		this.getViewport().setRelativeY((int) -scrollYDistance);

		guiGraphics.enableScissor(minX(), minY(), maxX(), maxY());
//		guiGraphics.pose().pushPose();
//		guiGraphics.pose().translate(getChildOffsetX(), getChildOffsetY(), 0);
		renderElement(guiGraphics, mouseX, mouseY, partialTick);
//		guiGraphics.pose().popPose();
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.disableScissor();

		if (isScrollbarXShown())
			renderXScrollbar(guiGraphics);
		if (isScrollbarYShown())
			renderYScrollbar(guiGraphics);
	}

	protected void renderElement(PLGuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

	private void renderXScrollbar(PLGuiGraphics guiGraphics) {
		guiGraphics.fill(minX(), maxY() - scrollbarHeight,
				isScrollbarYShown() ? maxX() - scrollbarWidth : maxX(), maxY(), SCROLLBAR_BACKGROUND_COLOR);

		int size = getScrollbarXSize();
		float scrollPercentage = getXScrollPercentage();
		int x = this.getX() + (int) (((isScrollbarYShown() ? this.getWidth() - scrollbarWidth : this.getWidth()) - size) * scrollPercentage);
		guiGraphics.fill(x, maxY() - scrollbarHeight, x + size, maxY(), SCROLLBAR_COLOR_DARK);
		guiGraphics.fill(x, maxY() - scrollbarHeight, x + size - 1, maxY() - 1, SCROLLBAR_COLOR);
	}

	private void renderYScrollbar(PLGuiGraphics guiGraphics) {
		guiGraphics.fill(maxX() - scrollbarWidth, minY(), maxX(), maxY(), SCROLLBAR_BACKGROUND_COLOR);

		int size = getScrollbarYSize();
		float scrollPercentage = getYScrollPercentage();
		int y = this.getY() + (int) ((this.getHeight() - size) * scrollPercentage);
		guiGraphics.fill(maxX() - scrollbarWidth, y, maxX(), y + size, SCROLLBAR_COLOR_DARK);
		guiGraphics.fill(maxX() - scrollbarWidth, y, maxX() - 1, y + size - 1, SCROLLBAR_COLOR);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;

		this.scrollXDistance -= (int) (scrollX * 10);
		this.scrollYDistance -= (int) (scrollY * 10);
		applyScrollLimits();
		return true;
	}

	private int getMaxScrollX() {
		return this.getContentWidth() - this.getWidth();
	}

	private int getMaxScrollY() {
		return this.getContentHeight() - this.getHeight();
	}

	public int getScrollbarXSize() {
		int width = isScrollbarYShown() ? getWidth() - scrollbarHeight : getWidth();
		int barLength = (width * width) / this.getContentWidth();

		if (barLength < 32) barLength = 32;
		return barLength;
	}

	public int getScrollbarYSize() {
		int barLength = (getHeight() * getHeight()) / this.getContentHeight();

		if (barLength < 32) barLength = 32;
		return barLength;
	}

	public float getXScrollPercentage() {
		return this.scrollXDistance / (this.getContentWidth() - this.getWidth());
	}

	public float getYScrollPercentage() {
		return this.scrollYDistance / (this.getContentHeight() - this.getHeight());
	}

	public boolean isMouseOverXScrollLine(double mouseX, double mouseY) {
		if (!isScrollbarXShown()) return false;
		return ScreenUtils.isMouseOver(mouseX, mouseY, minX(), maxY() - scrollbarHeight, maxX(), maxY());
	}

	public boolean isMouseOverXScrollBar(double mouseX, double mouseY) {
		if (!isScrollbarXShown()) return false;
		int size = getScrollbarXSize();
		float scrollPercentage = getXScrollPercentage();
		int x = this.getX() + (int) (((isScrollbarYShown() ? this.getWidth() - scrollbarWidth : this.getWidth()) - size) * scrollPercentage);
		return ScreenUtils.isMouseOver(mouseX, mouseY, x, maxY() - scrollbarHeight, x + size, maxY());
	}

	public boolean isMouseOverYScrollLine(double mouseX, double mouseY) {
		if (!isScrollbarYShown()) return false;
		return ScreenUtils.isMouseOver(mouseX, mouseY, maxX() - scrollbarWidth, minY(), maxX(), maxY());
	}

	public boolean isMouseOverYScrollBar(double mouseX, double mouseY) {
		if (!isScrollbarYShown()) return false;
		int size = getScrollbarYSize();
		float scrollPercentage = getYScrollPercentage();
		int y = this.getY() + (int) ((this.getHeight() - size) * scrollPercentage);
		return ScreenUtils.isMouseOver(mouseX, mouseY, maxX() - scrollbarWidth, y, maxX(), y + size);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (scrollingY) {
			int maxScroll = getHeight() - getScrollbarYSize();
			double moved = dragY / maxScroll;
			this.scrollYDistance += (float) (getMaxScrollY() * moved);
		} else if (scrollingX) {
			int maxScroll = getWidth() - getScrollbarXSize();
			double moved = dragX / maxScroll;
			this.scrollXDistance += (float) (getMaxScrollX() * moved);
		}
		if (scrollingY || scrollingX) {
			applyScrollLimits();
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (isMouseOverXScrollLine(mouseX, mouseY)) {
			scrollingX = isMouseOverXScrollBar(mouseX, mouseY);
		} else if (isMouseOverYScrollLine(mouseX, mouseY)) {
			scrollingY = isMouseOverYScrollBar(mouseX, mouseY);
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		scrollingX = false;
		scrollingY = false;
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean isOutOfBoundsInteractionAllowed() {
		return false;
	}
}

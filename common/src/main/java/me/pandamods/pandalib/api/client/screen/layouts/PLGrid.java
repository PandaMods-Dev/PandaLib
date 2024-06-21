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

package me.pandamods.pandalib.api.client.screen.layouts;

import com.mojang.math.Divisor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Mth;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class PLGrid {
	private final List<PLLayout> children = new ArrayList<>();
	private final List<CellInhabitant> cellInhabitants = new ArrayList<>();
	private final PLLayoutSettings defaultCellSettings = PLLayoutSettings.defaults();

	private int rowSpacing = 0;
	private int columnSpacing = 0;

	private int columns = 0;
	private int rows = 0;

	private int contentWidth = 0;
	private int contentHeight = 0;

	private float contentDeltaX = 0;
	private float contentDeltaY = 0;

	public void setDeltaX(int x) {
		this.contentDeltaX = x;
	}

	public void setDeltaY(int y) {
		this.contentDeltaY = y;
	}

	public void setDelta(float x, float y) {
		this.contentDeltaX = x;
		this.contentDeltaY = y;
	}

	public float getDeltaX() {
		return contentDeltaX;
	}

	public float getDeltaY() {
		return contentDeltaY;
	}

	public int getWidth() {
		return contentWidth;
	}

	public int getHeight() {
		return contentHeight;
	}

	public void visitChildren(Consumer<PLLayout> consumer) {
		this.children.forEach(consumer);
	}

	public void arrangeElements() {
		int row, column, maxOccupiedRow = 0, maxOccupiedColumn = 0;

		// Find the maximum occupied row and column index among all cell inhabitants
		for (CellInhabitant cellInhabitant : this.cellInhabitants) {
			maxOccupiedRow = Math.max(cellInhabitant.getLastOccupiedRow(), maxOccupiedRow);
			maxOccupiedColumn = Math.max(cellInhabitant.getLastOccupiedColumn(), maxOccupiedColumn);
		}

		int[] maxRowHeights = new int[maxOccupiedRow + 1];
		int[] maxColumnWidths = new int[maxOccupiedColumn + 1];

		// Calculate the maximum width (for each column) and the maximum height (for each row)
		for (CellInhabitant cellInhabitant : this.cellInhabitants) {
			int cellHeight = cellInhabitant.getHeight() - (cellInhabitant.occupiedRows - 1) * this.rowSpacing;
			Divisor heightDivisor = new Divisor(cellHeight, cellInhabitant.occupiedRows);

			for (row = cellInhabitant.row; row <= cellInhabitant.getLastOccupiedRow(); ++row) {
				maxRowHeights[row] = Math.max(maxRowHeights[row], heightDivisor.nextInt());
			}

			int cellWidth = cellInhabitant.getWidth() - (cellInhabitant.occupiedColumns - 1) * this.columnSpacing;
			Divisor widthDivisor = new Divisor(cellWidth, cellInhabitant.occupiedColumns);

			for (column = cellInhabitant.column; column <= cellInhabitant.getLastOccupiedColumn(); ++column) {
				maxColumnWidths[column] = Math.max(maxColumnWidths[column], widthDivisor.nextInt());
			}
		}

		// Preparation for position calculation
		int[] columnOffSets = new int[maxOccupiedColumn + 1];
		int[] rowOffSets = new int[maxOccupiedRow + 1];
		columnOffSets[0] = 0;
		for (int offset = 1; offset <= maxOccupiedColumn; ++offset) {
			columnOffSets[offset] = columnOffSets[offset - 1] + maxColumnWidths[offset - 1] + this.columnSpacing;
		}
		rowOffSets[0] = 0;
		for (int offset = 1; offset <= maxOccupiedRow; ++offset) {
			rowOffSets[offset] = rowOffSets[offset - 1] + maxRowHeights[offset - 1] + this.rowSpacing;
		}

		// Adjust the position of each cell inhabitant
		for (CellInhabitant cellInhabitant : this.cellInhabitants) {
			int cellWidth = 0;
			int occupiedWidth;
			for (occupiedWidth = cellInhabitant.column; occupiedWidth <= cellInhabitant.getLastOccupiedColumn(); ++occupiedWidth) {
				cellWidth += maxColumnWidths[occupiedWidth];
			}
			cellWidth += this.columnSpacing * (cellInhabitant.occupiedColumns - 1);
			cellInhabitant.setX(columnOffSets[cellInhabitant.column], cellWidth);

			int cellHeight = 0;
			int occupiedHeight;
			for (occupiedHeight = cellInhabitant.row; occupiedHeight <= cellInhabitant.getLastOccupiedRow(); ++occupiedHeight) {
				cellHeight += maxRowHeights[occupiedHeight];
			}
			cellHeight += this.rowSpacing * (cellInhabitant.occupiedRows - 1);
			cellInhabitant.setY(rowOffSets[cellInhabitant.row], cellHeight);
		}

		// Update the grid cell of the current object
		this.contentWidth = columnOffSets[maxOccupiedColumn] + maxColumnWidths[maxOccupiedColumn];
		this.contentHeight = rowOffSets[maxOccupiedRow] + maxRowHeights[maxOccupiedRow];
	}

	public PLLayoutSettings newCellSettings() {
		return this.defaultCellSettings.copy();
	}

	public <T extends PLLayout> T addChild(T child, int row, int column) {
		return this.addChild(child, row, column, this.newCellSettings());
	}

	public <T extends PLLayout> T addChild(T child, int row, int column, PLLayoutSettings layoutSettings) {
		return this.addChild(child, row, column, 1, 1, layoutSettings);
	}

	public <T extends PLLayout> T addChild(T child, int row, int column, int occupiedRows, int occupiedColumns) {
		return this.addChild(child, row, column, occupiedRows, occupiedColumns, this.newCellSettings());
	}

	public <T extends PLLayout> T addChild(T child, int row, int column,
										   int occupiedRows, int occupiedColumns, PLLayoutSettings layoutSettings) {
		if (occupiedRows < 1)
			throw new IllegalArgumentException("Occupied rows must be at least 1");
		if (occupiedColumns < 1)
			throw new IllegalArgumentException("Occupied columns must be at least 1");
		this.cellInhabitants.add(new CellInhabitant(child, row, column, occupiedRows, occupiedColumns, layoutSettings));

		if (column >  this.columns)
			this.columns = column;
		if (row >  this.rows)
			this.rows = row;
		return child;
	}

	public List<PLLayout> getChildren() {
		return children;
	}

	public PLGrid columnSpacing(int columnSpacing) {
		this.columnSpacing = columnSpacing;
		return this;
	}

	public PLGrid rowSpacing(int rowSpacing) {
		this.rowSpacing = rowSpacing;
		return this;
	}

	public PLGrid spacing(int spacing) {
		return this.columnSpacing(spacing).rowSpacing(spacing);
	}

	public PLLayoutSettings defaultCellSetting() {
		return this.defaultCellSettings;
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	public RowHelper createRowHelper(int columns) {
		return new RowHelper(columns);
	}

	public ColumnHelper createColumnHelper(int rows) {
		return new ColumnHelper(rows);
	}

	@Environment(EnvType.CLIENT)
	static class CellInhabitant {
		public final PLLayout child;
		public final PLLayoutSettings.LayoutSettingsImpl layoutSettings;

		final int row;
		final int column;

		final int occupiedRows;
		final int occupiedColumns;

		CellInhabitant(PLLayout child, int row, int column,
					   int occupiedRows, int occupiedColumns, PLLayoutSettings layoutSettings) {
			this.child = child;
			this.layoutSettings = layoutSettings.getExposed();
			this.row = row;
			this.column = column;
			this.occupiedRows = occupiedRows;
			this.occupiedColumns = occupiedColumns;
		}

		public int getLastOccupiedRow() {
			return this.row + this.occupiedRows - 1;
		}

		public int getLastOccupiedColumn() {
			return this.column + this.occupiedColumns - 1;
		}


		public int getHeight() {
			return this.child.getHeight() + this.layoutSettings.paddingTop + this.layoutSettings.paddingBottom;
		}

		public int getWidth() {
			return this.child.getWidth() + this.layoutSettings.paddingLeft + this.layoutSettings.paddingRight;
		}

		public void setX(int x, int width) {
			float min = this.layoutSettings.paddingLeft;
			float max = width - this.child.getWidth() - this.layoutSettings.paddingRight;
			int i = (int) Mth.lerp(this.layoutSettings.xAlignment, min, max);
			this.child.setX(i + x);
		}

		public void setY(int y, int height) {
			float min = this.layoutSettings.paddingTop;
			float max = height - this.child.getHeight() - this.layoutSettings.paddingBottom;
			int i = Math.round(Mth.lerp(this.layoutSettings.yAlignment, min, max));
			this.child.setY(i + y);
		}
	}

	@Environment(EnvType.CLIENT)
	public final class RowHelper {
		private final int columns;
		private int index;

		RowHelper(int columns) {
			this.columns = columns;
		}

		public <T extends PLLayout> T addChild(T child) {
			return this.addChild(child, 1);
		}

		public <T extends PLLayout> T addChild(T child, int occupiedColumns) {
			return this.addChild(child, occupiedColumns, this.defaultCellSetting());
		}

		public <T extends PLLayout> T addChild(T child, PLLayoutSettings layoutSettings) {
			return this.addChild(child, 1, layoutSettings);
		}

		public <T extends PLLayout> T addChild(T child, int occupiedColumns, PLLayoutSettings layoutSettings) {
			int x = this.index / this.columns;
			int y = this.index % this.columns;
			if (y + occupiedColumns > this.columns) {
				++x;
				y = 0;
				this.index = Mth.roundToward(this.index, this.columns);
			}
			this.index += occupiedColumns;
			return PLGrid.this.addChild(child, x, y, 1, occupiedColumns, layoutSettings);
		}

		public PLGrid getGrid() {
			return PLGrid.this;
		}

		public PLLayoutSettings newCellSettings() {
			return PLGrid.this.newCellSettings();
		}

		public PLLayoutSettings defaultCellSetting() {
			return PLGrid.this.defaultCellSetting();
		}
	}

	@Environment(EnvType.CLIENT)
	public final class ColumnHelper {
		private final int rows;
		private int index;

		ColumnHelper(int rows) {
			this.rows = rows;
		}

		public <T extends PLLayout> T addChild(T child) {
			return this.addChild(child, 1);
		}

		public <T extends PLLayout> T addChild(T child, int occupiedRows) {
			return this.addChild(child, occupiedRows, this.defaultCellSetting());
		}

		public <T extends PLLayout> T addChild(T child, PLLayoutSettings layoutSettings) {
			return this.addChild(child, 1, layoutSettings);
		}

		public <T extends PLLayout> T addChild(T child, int occupiedRows, PLLayoutSettings layoutSettings) {
			int x = this.index % this.rows;
			int y = this.index / this.rows;
			if (y + occupiedRows > this.rows) {
				x = 0;
				++y;
				this.index = Mth.roundToward(this.index, this.rows);
			}
			this.index += occupiedRows;
			return PLGrid.this.addChild(child, x, y, occupiedRows, 1, layoutSettings);
		}

		public PLGrid getGrid() {
			return PLGrid.this;
		}

		public PLLayoutSettings newCellSettings() {
			return PLGrid.this.newCellSettings();
		}

		public PLLayoutSettings defaultCellSetting() {
			return PLGrid.this.defaultCellSetting();
		}
	}
}

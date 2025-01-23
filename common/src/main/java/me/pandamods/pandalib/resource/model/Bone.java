/*
 * Copyright (C) 2024 Oliver Froberg (The Panda Oliver)
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 * You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package me.pandamods.pandalib.resource.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bone {
	private Bone parent = null;
	private final List<Bone> children = new ArrayList<>();
	private final List<Bone> viewChildren = Collections.unmodifiableList(children);

	public Bone(Bone parent) {
		if (parent != null)
			setParent(parent);
	}
	
	public void setParent(Bone parent) {
		if (this.parent == parent) return;
		if (this.parent != null)
			this.parent.children.remove(this);
		this.parent = parent;
		if (parent != null)
			parent.children.add(this);
	}

	public Bone getParent() {
		return parent;
	}

	public List<Bone> getChildren() {
		return this.viewChildren;
	}
}

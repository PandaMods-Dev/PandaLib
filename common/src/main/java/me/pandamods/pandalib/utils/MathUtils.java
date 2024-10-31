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

package me.pandamods.pandalib.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MathUtils {
	public static Matrix4f lerpMatrix(Matrix4f matrix, Matrix4f other, float alpha) {
		return lerpMatrix(matrix, other, alpha, new Matrix4f());
	}

	public static Matrix4f lerpMatrix(Matrix4f matrix, Matrix4f other, float alpha, Matrix4f dist) {
		Vector3f translation = new Vector3f();
		Quaternionf rotation = new Quaternionf();
		Vector3f scale = new Vector3f();

		Vector3f otherTranslation = new Vector3f();
		Quaternionf otherRotation = new Quaternionf();
		Vector3f otherScale = new Vector3f();

		matrix.getTranslation(translation);
		matrix.getUnnormalizedRotation(rotation);
		matrix.getScale(scale);

		other.getTranslation(otherTranslation);
		other.getUnnormalizedRotation(otherRotation);
		other.getScale(otherScale);

		translation.lerp(otherTranslation, alpha);
		rotation.slerp(otherRotation, alpha);
		scale.lerp(otherScale, alpha);

		return dist.translationRotateScale(translation, rotation, scale);
	}

	public static Vector3f rotateVector(Vector3f target, Vector3f rotation) {
		return target.rotateZ(rotation.z).rotateY(rotation.y).rotateX(rotation.x);
	}

	public static PoseStack rotateVector(PoseStack stack, Vector3f rotation) {
		Quaternionf quaternionf = new Quaternionf().rotateZYX(rotation.z, rotation.y, rotation.x);
		stack.mulPose(new Quaternion(quaternionf.x, quaternionf.y, quaternionf.z, quaternionf.w));
		return stack;
	}

	public static Vector3f rotateAroundOrigin(Vector3f target, Vector3f origin, Vector3f rotation) {
		target.add(origin);
		rotateVector(target, rotation);
		return target.sub(origin);
	}

	public static PoseStack rotateAroundOrigin(PoseStack stack, Vector3f origin, Vector3f rotation) {
		stack.translate(origin.x, origin.y, origin.z);
		rotateVector(stack, rotation);
		stack.translate(-origin.x, -origin.y, -origin.z);
		return stack;
	}
}

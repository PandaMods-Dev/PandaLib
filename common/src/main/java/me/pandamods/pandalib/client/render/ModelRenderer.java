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

package me.pandamods.pandalib.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.pandamods.pandalib.resource.model.Mesh;
import me.pandamods.pandalib.resource.model.Model;
import me.pandamods.pandalib.resource.model.Node;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.*;

import java.awt.*;
import java.util.List;
import java.util.function.Function;

public class ModelRenderer {
	public static void render(Model model, PoseStack poseStack, int overlayUV, int lightmapUV, Function<String, VertexConsumer> vertexConsumerProvider) {
		List<Mesh> meshes = model.getMeshes();

		renderNode(model, model.getRootNode(), poseStack, overlayUV, lightmapUV, vertexConsumerProvider, meshes);
	}

	public static void renderNode(Model model, Node node, PoseStack poseStack, int overlayUV, int lightmapUV,
								  Function<String, VertexConsumer> vertexConsumerProvider, List<Mesh> meshes) {
		if (node.isVisible()) {
			for (Integer meshIndex : node.getMeshIndexes()) {
				Mesh mesh = meshes.get(meshIndex);
				renderMesh(model, mesh, node, poseStack, overlayUV, lightmapUV, vertexConsumerProvider.apply(mesh.getMaterialName()));
			}
		}
		node.getChildren().forEach(child -> renderNode(model, child, poseStack, overlayUV, lightmapUV, vertexConsumerProvider, meshes));
	}

	public static void renderMesh(Model model, Mesh mesh, Node meshNode, PoseStack poseStack, int overlayUV, int lightmapUV, VertexConsumer vertexConsumer) {
		Vector2f uvCoords = new Vector2f();

		Vector3f position = new Vector3f();
		Vector3f normal = new Vector3f();

		for (Integer i : mesh.getIndices()) {
			float posX = mesh.getVertices()[i * 3];
			float posY = mesh.getVertices()[i * 3 + 1];
			float posZ = mesh.getVertices()[i * 3 + 2];
			position.set(posX, posY, posZ).mulPosition(meshNode.getGlobalTransform());

			float u = mesh.getUvs()[i * 2];
			float v = mesh.getUvs()[i * 2 + 1];
			uvCoords.set(u, v);

			float normX = mesh.getNormals()[i * 3];
			float normY = mesh.getNormals()[i * 3 + 1];
			float normZ = mesh.getNormals()[i * 3 + 2];
			normal.set(normX, normY, normZ).mulDirection(meshNode.getGlobalTransform());

			if (mesh.getBoneIndices() != null && mesh.getBoneWeights() != null) {
				Vector3f finalPosition = new Vector3f();
				Vector3f finalNormal = new Vector3f();

				boolean hasWeights = false;

				for (int j = 0; j < 4; j++) {
					int boneIndex = mesh.getBoneIndices()[i * 4 + j];
					float boneWeight = mesh.getBoneWeights()[i * 4 + j];
					if (boneIndex == -1 || boneWeight == 0) continue;
					hasWeights = true;

					Node boneNode = model.getNodes().get(boneIndex);
					Matrix4f boneTransform = boneNode.getGlobalTransform();
					Matrix4f inverseBoneTransform = new Matrix4f(boneNode.getInitialGlobalTransform()).invert();

					Vector3f bonePosition = new Vector3f(position).mulPosition(inverseBoneTransform).mulPosition(boneTransform);
					Vector3f boneNormal = new Vector3f(normal).mulDirection(inverseBoneTransform).mulDirection(boneTransform);

					finalPosition.add(bonePosition.mul(boneWeight));
					finalNormal.add(boneNormal.mul(boneWeight));
				}

				if (hasWeights) {
					position.set(finalPosition);
					normal.set(finalNormal);
				}
			}

			vertexConsumer
					.vertex(poseStack.last().pose(), position.x(), position.y(), position.z())
					.color(1f, 1f, 1f, 1f)
					.uv(uvCoords.x(), uvCoords.y())
					.overlayCoords(overlayUV)
					.uv2(lightmapUV)
					.normal(poseStack.last().normal(), normal.x(), normal.y(), normal.z())
					.endVertex();
		}
	}

	@Deprecated()
	public static void renderModelDebug(Model model, PoseStack poseStack, MultiBufferSource bufferSource) {
	}
}

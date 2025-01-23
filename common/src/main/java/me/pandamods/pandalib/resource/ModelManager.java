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

package me.pandamods.pandalib.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import me.pandamods.pandalib.PandaLib;
import me.pandamods.pandalib.registry.IdentifiableResourceReloadListener;
import me.pandamods.pandalib.resource.model.Model;
import me.pandamods.pandalib.utils.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ModelManager implements IdentifiableResourceReloadListener {
	public static final ResourceLocation ID = PandaLib.resourceLocation("model_manager");
	
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new GsonBuilder().create();
	
	private Map<ResourceLocation, Model> models = ImmutableMap.of();
	
	@Override
	public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
		Map<ResourceLocation, Model> models = Maps.newHashMap();
		
		return preparationBarrier.wait(Unit.INSTANCE)
				.thenAcceptAsync(unused -> {
					Set<Map.Entry<ResourceLocation, Resource>> entries = resourceManager.listResources("pandalib/model", 
							resource -> resource.getPath().endsWith(".plm")).entrySet();
					for (Map.Entry<ResourceLocation, Resource> entry : entries) {
						ResourceLocation resourceLocation = entry.getKey();
						Resource resource = entry.getValue();
						
						JsonObject modelJson;
						try (BufferedReader reader = resource.openAsReader()) {
							modelJson = GSON.fromJson(reader, JsonObject.class);
							
							if (modelJson == null) {
								LOGGER.error("Failed to load model {}", resourceLocation);
							}
						} catch (IOException e) {
							LOGGER.error("Failed to load model {}", resourceLocation, e);
							continue;
						}
						assert modelJson != null;
						
						if (!modelJson.has("plm_version" ))
							throw new IllegalArgumentException("Unsupported PLM format " + resourceLocation);
						else if (modelJson.getAsJsonPrimitive("plm_version").getAsString().equalsIgnoreCase(Constants.SUPPORTED_PLM_VERSION))
							throw new IllegalArgumentException(String.format("Unsupported PLM version %s, expected [%s] but found [%s]", 
									resourceLocation, Constants.SUPPORTED_PLM_VERSION,  modelJson.getAsJsonPrimitive("plm_version").getAsString()));
						
						models.put(resourceLocation, parseModel(modelJson));
					}
					this.models = ImmutableMap.copyOf(models);
				}, gameExecutor);
	}
	
	private Model parseModel(JsonObject modelJson) {
		return null;
	}

	@Override
	public ResourceLocation getResourceID() {
		return ID;
	}
}

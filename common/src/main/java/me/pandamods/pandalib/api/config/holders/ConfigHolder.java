package me.pandamods.pandalib.api.config.holders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.architectury.platform.Platform;
import me.pandamods.pandalib.api.client.screen.config.*;
import me.pandamods.pandalib.api.client.screen.config.auto.ConfigScreenProvider;
import me.pandamods.pandalib.api.config.Config;
import me.pandamods.pandalib.api.config.ConfigData;
import me.pandamods.pandalib.core.utils.ClassUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigHolder<T extends ConfigData> {
	public final Logger logger;
	private final Gson gson;

	private final Class<T> configClass;
	private final Config definition;
	private final ResourceLocation resourceLocation;
	private final boolean synchronize;
	private T config;

	public ConfigHolder(Class<T> configClass, Config config) {
		this.configClass = configClass;
		this.definition = config;
		this.logger = LoggerFactory.getLogger(config.modId() + " | Config");
		this.gson = getNewDefault().buildGson(new GsonBuilder()).setPrettyPrinting().create();

		this.resourceLocation = new ResourceLocation(config.modId(), config.name());
		this.synchronize = config.synchronize();

		if (this.load()) {
			save();
		}
	}

	public Gson getGson() {
		return gson;
	}

	public Class<T> getConfigClass() {
		return configClass;
	}

	public Config getDefinition() {
		return definition;
	}

	public boolean shouldSynchronize() {
		return synchronize;
	}

	public Path getConfigPath() {
		Path path = Platform.getConfigFolder();
		if (!definition.parentDirectory().isBlank()) path = path.resolve(definition.parentDirectory());
		return path.resolve(definition.name() + ".json");
	}

	public void save() {
		JsonObject jsonObject = this.getGson().toJsonTree(this.config).getAsJsonObject();
		this.config.onSave(this, jsonObject);
		Path configPath = getConfigPath();
		try {
			Files.createDirectories(configPath.getParent());
			BufferedWriter writer = Files.newBufferedWriter(configPath);
			this.getGson().toJson(jsonObject, writer);
			writer.close();
			this.logger.info("Successfully saved config '{}'", definition.name());
		} catch (IOException e) {
			this.logger.info("Failed to save config '{}'", definition.name());
			throw new RuntimeException(e);
		}
	}

	public boolean load() {
		Path configPath = getConfigPath();
		if (Files.exists(configPath)) {
			try (BufferedReader reader = Files.newBufferedReader(configPath)) {
				JsonObject jsonObject = this.getGson().fromJson(reader, JsonObject.class);
				this.config = this.getGson().fromJson(jsonObject, configClass);
				this.config.onLoad(this, jsonObject);
			} catch (IOException e) {
				this.logger.error("Failed to load config '{}', using default", definition.name(), e);
				resetToDefault();
				return false;
			}
		} else {
			resetToDefault();
			save();
		}
		this.logger.info("Successfully loaded config '{}'", definition.name());
		return true;
	}

	public void resetToDefault() {
		this.config = getNewDefault();
	}

	/**
	 * @return Newly created class
	 */
	public T getNewDefault() {
		return ClassUtils.constructUnsafely(configClass);
	}

	public ResourceLocation resourceLocation() {
		return resourceLocation;
	}

	public MutableComponent getName() {
		return Component.translatable(String.format("config.%s.%s", resourceLocation.getNamespace(), resourceLocation.getPath()));
	}

	public String modID() {
		return getDefinition().modId();
	}

	/**
	 * @return Local config settings
	 */
	public T get() {
		return config;
	}

	@Environment(EnvType.CLIENT)
	public ConfigMenu.Builder<T> buildScreen(Screen parent) {
		return new ConfigScreenProvider<T>(parent, this).getBuilder();
	}
}

/*
 * Copyright (C) 2025 Oliver Froberg (The Panda Oliver)
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 * You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package me.pandamods.pandalib.forge.mixin;

import me.pandamods.pandalib.forge.platform.RegistrationHelperImpl;
import me.pandamods.pandalib.platform.Services;
import net.minecraftforge.registries.NewRegistryEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NewRegistryEvent.class, remap = false)
public class NewRegistryEventMixin {
	@Inject(method = "fill", at = @At(value = "FIELD", target = "Lnet/minecraftforge/registries/NewRegistryEvent;registries:Ljava/util/List;"))
	public void registerRegistries(CallbackInfo ci) {
		if (Services.REGISTRATION instanceof RegistrationHelperImpl helper) {
			helper.registerNewRegistries();
		}
	}
}

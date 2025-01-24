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

package me.pandamods.pandalib.utils.animation;

public interface Easing {
	double ease(double t);
	
	Easing easeInSine = t -> 1 - Math.cos((t * Math.PI) / 2);
	Easing easeOutSine = t -> Math.sin((t * Math.PI) / 2);
	Easing easeInOutSine = t -> -(Math.cos(Math.PI * t) - 1) / 2;
	
	Easing easeInQuad = t -> t * t;
	Easing easeOutQuad = t -> 1 - (1 - t) * (1 - t);
	Easing easeInOutQuad = t -> t < 0.5 ? 2 * t * t : 1 - (-2 * t + 2) * (-2 * t + 2) / 2;
	
	Easing easeInCubic = t -> t * t * t;
	Easing easeOutCubic = t -> 1 - Math.pow(1 - t, 3);
	Easing easeInOutCubic = t -> t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
	
	Easing easeInQuart = t -> t * t * t * t;
	Easing easeOutQuart = t -> 1 - Math.pow(1 - t, 4);
	Easing easeInOutQuart = t -> t < 0.5 ? 8 * t * t * t * t : 1 - Math.pow(-2 * t + 2, 4) / 2;
	
	Easing easeInQuint = t -> t * t * t * t * t;
	Easing easeOutQuint = t -> 1 - Math.pow(1 - t, 5);
	Easing easeInOutQuint = t -> t < 0.5 ? 16 * t * t * t * t * t : 1 - Math.pow(-2 * t + 2, 5) / 2;
	
	Easing easeInExpo = t -> t == 0 ? 0 : Math.pow(2, 10 * (t - 1));
	Easing easeOutExpo = t -> t == 1 ? 1 : 1 - Math.pow(2, -10 * t);
	Easing easeInOutExpo = t -> t == 0 ? 0 : t == 1 ? 1 : t < 0.5 ? Math.pow(2, 20 * t - 10) / 2 : (2 - Math.pow(2, -20 * t + 10)) / 2;
	
	Easing easeInCirc = t -> 1 - Math.sqrt(1 - t * t);
	Easing easeOutCirc = t -> Math.sqrt(1 - Math.pow(t - 1, 2));
	Easing easeInOutCirc = t -> t < 0.5 ? (1 - Math.sqrt(1 - 4 * t * t)) / 2 : (Math.sqrt(1 - -4 * t * t) + 1) / 2;
	
	Easing easeInBack = t -> {
		double c1 = 1.70158;
		double c3 = c1 + 1;
		
		return c3 * t * t * t - c1 * t * t;
	};
	Easing easeOutBack = t -> {
		double c1 = 1.70158;
		double c3 = c1 + 1;
		
		return 1 + c3 * (t - 1) * (t - 1) * (t - 1) - c1 * (t - 1) * (t - 1);
	};
	Easing easeInOutBack = t -> {
		double c1 = 1.70158;
		double c2 = c1 * 1.525;

		return t < 0.5
				? (Math.pow(2 * t, 2) * ((c2 + 1) * 2 * t - c2)) / 2
				: (Math.pow(2 * t - 2, 2) * ((c2 + 1) * (t * 2 - 2) + c2) + 2) / 2;
	};
	
	Easing easeInElastic = t -> {
		double c4 = (2 * Math.PI) / 3;

		return t == 0
				? 0
				: t == 1
				? 1
				: -Math.pow(2, 10 * t - 10) * Math.sin((t * 10 - 10.75) * c4);
	};
	Easing easeOutElastic = t -> {
		double c4 = (2 * Math.PI) / 3;
		
		return t == 0
				? 0
				: t == 1
				? 1
				: Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * c4) + 1;
	};
	Easing easeInOutElastic = t -> {
		double c5 = (2 * Math.PI) / 4.5;

		return t == 0
				? 0
				: t == 1
				? 1
				: t < 0.5
				? -(Math.pow(2, 20 * t - 10) * Math.sin((20 * t - 11.125) * c5)) / 2
				: (Math.pow(2, -20 * t + 10) * Math.sin((20 * t - 11.125) * c5)) / 2 + 1;
	};

	Easing easeOutBounce = t -> {
		double n1 = 7.5625;
		double d1 = 2.75;

		if (t < 1 / d1) {
			return n1 * t * t;
		} else if (t < 2 / d1) {
			return n1 * (t -= 1.5 / d1) * t + 0.75;
		} else if (t < 2.5 / d1) {
			return n1 * (t -= 2.25 / d1) * t + 0.9375;
		} else {
			return n1 * (t -= 2.625 / d1) * t + 0.984375;
		}
	};
	Easing easeInBounce = t -> 1 - easeOutBounce.ease(1 - t);
	Easing easeInOutBounce = t -> t < 0.5 ? (1 - easeOutBounce.ease(1 - 2 * t)) / 2 : (1 + easeOutBounce.ease(2 * t - 1)) / 2;
}

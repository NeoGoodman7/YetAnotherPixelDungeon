/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.consideredhamster.yetanotherpixeldungeon.ios;

import com.badlogic.gdx.Gdx;
import com.consideredhamster.yetanotherpixeldungeon.YetAnotherPixelDungeon;
import com.watabou.noosa.Game;
import com.watabou.utils.PlatformSupport;

import org.robovm.apple.audiotoolbox.AudioServices;
import org.robovm.apple.uikit.UIApplication;

public class IOSPlatformSupport extends PlatformSupport {
	@Override
	public void updateDisplaySize() {
		//non-zero safe insets on left/top/right means device has a notch, show status bar
		if (Gdx.graphics.getSafeInsetTop() != 0
				|| Gdx.graphics.getSafeInsetLeft() != 0
				|| Gdx.graphics.getSafeInsetRight() != 0){
			UIApplication.getSharedApplication().setStatusBarHidden(false);
		} else {
			UIApplication.getSharedApplication().setStatusBarHidden(true);
		}

		if (!YetAnotherPixelDungeon.fullscreen()) {
			int insetChange = Gdx.graphics.getSafeInsetBottom() - Game.bottomInset;
			Game.bottomInset = Gdx.graphics.getSafeInsetBottom();
			Game.height -= insetChange;
			Game.dispHeight = Game.height;
		} else {
			Game.height += Game.bottomInset;
			Game.dispHeight = Game.height;
			Game.bottomInset = 0;
		}
		Gdx.gl.glViewport(0, Game.bottomInset, Game.width, Game.height);
	}

	@Override
	public void updateSystemUI() {
		int prevInset = Game.bottomInset;
		updateDisplaySize();
		if (prevInset != Game.bottomInset) {
			YetAnotherPixelDungeon.seamlessResetScene();
		}
	}

	// @Override
	// public boolean connectedToUnmeteredNetwork() {
	// 	SCNetworkReachability test = new SCNetworkReachability("www.apple.com");
	// 	return !test.getFlags().contains(SCNetworkReachabilityFlags.IsWWAN);
	// }

	public void vibrate( int millis ){
		//gives a short vibrate on iPhone 6+, no vibration otherwise
		AudioServices.playSystemSound(1520);
	}
}

/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
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

package com.consideredhamster.yetanotherpixeldungeon.android;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.consideredhamster.yetanotherpixeldungeon.YetAnotherPixelDungeon;
import com.consideredhamster.yetanotherpixeldungeon.scenes.PixelScene;
import com.consideredhamster.yetanotherpixeldungeon.visuals.ui.GameLog;
import com.watabou.noosa.Game;
import com.watabou.utils.PlatformSupport;

public class AndroidPlatformSupport extends PlatformSupport {

	@Override
	public void updateDisplaySize(){
		if (YetAnotherPixelDungeon.landscape() != null) {
			android.util.Log.d("App", "RequestedOrientation " + YetAnotherPixelDungeon.landscape());
			AndroidGame.instance.setRequestedOrientation( YetAnotherPixelDungeon.landscape() ?
					ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE :
					ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT );
		}

		GLSurfaceView view = (GLSurfaceView) ((AndroidGraphics)Gdx.graphics).getView();
		
		if (view.getMeasuredWidth() == 0 || view.getMeasuredHeight() == 0)
			return;
		
		Game.dispWidth = view.getMeasuredWidth();
		Game.dispHeight = view.getMeasuredHeight();

		boolean fullscreen = Build.VERSION.SDK_INT < Build.VERSION_CODES.N
				|| !AndroidGame.instance.isInMultiWindowMode();

		if (fullscreen && YetAnotherPixelDungeon.landscape() != null
				&& (Game.dispWidth >= Game.dispHeight) != YetAnotherPixelDungeon.landscape()){
			int tmp = Game.dispWidth;
			Game.dispWidth = Game.dispHeight;
			Game.dispHeight = tmp;
		}
		
		float dispRatio = Game.dispWidth / (float)Game.dispHeight;
		
		float renderWidth = dispRatio > 1 ? PixelScene.MIN_WIDTH_L : PixelScene.MIN_WIDTH_P;
		float renderHeight = dispRatio > 1 ? PixelScene.MIN_HEIGHT_L : PixelScene.MIN_HEIGHT_P;

		// todo
		boolean powerSaver = false;
		//force power saver in this case as all devices must run at at least 2x scale.
		if (Game.dispWidth < renderWidth*2 || Game.dispHeight < renderHeight*2) {
			powerSaver = true;
			// YetAnotherPixelDungeon.put(SPDSettings.KEY_POWER_SAVER, true);
		}
		if (powerSaver && fullscreen){
			
			int maxZoom = (int)Math.min(Game.dispWidth/renderWidth, Game.dispHeight/renderHeight);
			
			renderWidth *= Math.max( 2, Math.round(1f + maxZoom*0.4f));
			renderHeight *= Math.max( 2, Math.round(1f + maxZoom*0.4f));
			
			if (dispRatio > renderWidth / renderHeight){
				renderWidth = renderHeight * dispRatio;
			} else {
				renderHeight = renderWidth / dispRatio;
			}
			
			final int finalW = Math.round(renderWidth);
			final int finalH = Math.round(renderHeight);
			if (finalW != Game.width || finalH != Game.height){
				
				AndroidGame.instance.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						android.util.Log.d("App", "setFixedSize " + YetAnotherPixelDungeon.landscape());
						view.getHolder().setFixedSize(finalW, finalH);
					}
				});
				
			}
		} else {
			AndroidGame.instance.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					android.util.Log.d("App", "setSizeFromLayout " + YetAnotherPixelDungeon.landscape());
					view.getHolder().setSizeFromLayout();
				}
			});
		}
	}

	@Override
	public void updateSystemUI() {
		
		AndroidGame.instance.runOnUiThread(new Runnable() {
			@SuppressLint("NewApi")
			@Override
			public void run() {
				boolean fullscreen = Build.VERSION.SDK_INT < Build.VERSION_CODES.N
						|| !AndroidGame.instance.isInMultiWindowMode();
				
				if (fullscreen){
					AndroidGame.instance.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				} else {
					AndroidGame.instance.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
							WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				}
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
					if (YetAnotherPixelDungeon.fullscreen()) {
						AndroidGame.instance.getWindow().getDecorView().setSystemUiVisibility(
								View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
										| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
										| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
					} else {
						AndroidGame.instance.getWindow().getDecorView().setSystemUiVisibility(
								View.SYSTEM_UI_FLAG_LAYOUT_STABLE );
					}
				}
			}
		});
		
	}
	
	// @Override
	// @SuppressWarnings("deprecation")
	// public boolean connectedToUnmeteredNetwork() {
	// 	//Returns true if using unmetered connection, use shortcut method if available
	// 	ConnectivityManager cm = (ConnectivityManager) AndroidGame.instance.getSystemService(Context.CONNECTIVITY_SERVICE);
	// 	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
	// 		return !cm.isActiveNetworkMetered();
	// 	} else {
	// 		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
	// 		return activeNetwork != null && activeNetwork.isConnectedOrConnecting() &&
	// 				(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI
	// 				|| activeNetwork.getType() == ConnectivityManager.TYPE_WIMAX
	// 				|| activeNetwork.getType() == ConnectivityManager.TYPE_BLUETOOTH
	// 				|| activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET);
	// 	}
	// }

	@Override
	public void updateImmersiveMode() {
		try {
			((AndroidGame) AndroidGame.instance).useImmersiveMode(YetAnotherPixelDungeon.immersed());
		}catch(Exception e) {
			Game.reportException(e);
		}
	}
}

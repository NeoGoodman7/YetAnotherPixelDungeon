/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Yet Another Pixel Dungeon
 * Copyright (C) 2015-2016 Considered Hamster
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
package com.consideredhamster.yetanotherpixeldungeon.visuals.windows;

import com.consideredhamster.yetanotherpixeldungeon.YetAnotherPixelDungeon;
import com.consideredhamster.yetanotherpixeldungeon.misc.utils.Utils;
import com.consideredhamster.yetanotherpixeldungeon.scenes.PixelScene;
import com.consideredhamster.yetanotherpixeldungeon.visuals.Assets;
import com.consideredhamster.yetanotherpixeldungeon.visuals.ui.CheckBox;
import com.consideredhamster.yetanotherpixeldungeon.visuals.ui.RedButton;
import com.consideredhamster.yetanotherpixeldungeon.visuals.ui.Window;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.DeviceCompat;

public class WndSettings extends Window {
	
	private static final String TXT_ZOOM_IN			= "+";
	private static final String TXT_ZOOM_OUT		= "-";
	private static final String TXT_ZOOM_DEFAULT	= "Default Zoom";

	private static final String TXT_FULLSCREEN		= "Fullscreen";
	private static final String TXT_SCALE_UP		= "Scale up UI";
	private static final String TXT_IMMERSIVE		= "Immersive mode";
	
	private static final String TXT_MUSIC	        = "Music";
	
	private static final String TXT_SOUND	        = "Sound FX";

	private static final String TXT_BUTTONS         = "Waterskins/lantern: %s";

    private static final String[] TXT_BUTTONS_VAR  = {
            "Right",
            "Left",
    };

	private static final String TXT_BRIGHTNESS	    = "Brightness";

	private static final String TXT_LOADING_TIPS  = "Loading tips: %s";

	private static final String[] TXT_TIPS_DELAY  = {
            "Disabled",
            "Normal delay",
            "Doubled delay",
            "Until tapped",
    };

    private static final String TXT_SEARCH_BTN  = "Search btn: %s";

    private static final String[] TXT_SEARCH_VAR  = {
            "Default behv.",
            "Reversed behv.",
    };

	private static final String TXT_SWITCH_PORT 	= "Switch to portrait";
    private static final String TXT_SWITCH_LAND 	= "Switch to landscape";

	private static final int WIDTH		= 112;
    private static final int BTN_HEIGHT	= 20;
	private static final int GAP 		= 2;
	
	private RedButton btnZoomOut;
	private RedButton btnZoomIn;


    public WndSettings( final boolean inGame ) {
		super();

        RedButton btnOrientation = null;
        CheckBox btnImmersive = null;
        CheckBox chkScaleUp = null;
        CheckBox chkBrightness = null;

        if (inGame) {
			int w = BTN_HEIGHT;
			
			btnZoomOut = new RedButton( TXT_ZOOM_OUT ) {
				@Override
				protected void onClick() {
					zoom( Camera.main.zoom - 1 );
				}
			};
			add( btnZoomOut.setRect( 0, 0, w, BTN_HEIGHT) );
			
			btnZoomIn = new RedButton( TXT_ZOOM_IN ) {
				@Override
				protected void onClick() {
					zoom( Camera.main.zoom + 1 );
				}
			};
			add( btnZoomIn.setRect( WIDTH - w, 0, w, BTN_HEIGHT) );
			
			add( new RedButton( TXT_ZOOM_DEFAULT ) {
				@Override
				protected void onClick() {
					zoom( PixelScene.defaultZoom );
				}
			}.setRect(btnZoomOut.right(), 0, WIDTH - btnZoomIn.width() - btnZoomOut.width(), BTN_HEIGHT) );
			
			updateEnabled();

            RedButton btnSearchBtn = new RedButton( searchButtonsText( YetAnotherPixelDungeon.searchButton() ) ) {
                @Override
                protected void onClick(){

                    boolean val = !YetAnotherPixelDungeon.searchButton();

                    YetAnotherPixelDungeon.searchButton( val );

                    text.text( searchButtonsText( val ) );
                    text.measure();
                    layout();
                }
            };
            btnSearchBtn.setRect( 0, BTN_HEIGHT + GAP, WIDTH, BTN_HEIGHT );
            add( btnSearchBtn );

            chkBrightness = new CheckBox( TXT_BRIGHTNESS ) {
                @Override
                protected void onClick() {
                    super.onClick();
                    YetAnotherPixelDungeon.brightness(checked());
                }
            };
            chkBrightness.setRect(0, btnSearchBtn.bottom()+ GAP, WIDTH, BTN_HEIGHT);
            chkBrightness.checked(YetAnotherPixelDungeon.brightness());
            add(chkBrightness);


        } else {
            if (DeviceCompat.isAndroid()) {
                btnOrientation = new RedButton(orientationText()) {
                    @Override
                    protected void onClick() {
                        YetAnotherPixelDungeon.landscape(!PixelScene.landscape());
                    }
                };
                btnOrientation.setRect(0, 0, WIDTH, BTN_HEIGHT);
                add(btnOrientation);
            }

            CheckBox chkFullscreen = new CheckBox( TXT_FULLSCREEN ) {
                @Override
                protected void onClick() {
                    super.onClick();
                    YetAnotherPixelDungeon.fullscreen(checked());
                }
            };
            if (DeviceCompat.supportsFullScreen()){
                chkFullscreen.checked(YetAnotherPixelDungeon.fullscreen());
            } else {
                chkFullscreen.checked(true);
                chkFullscreen.enable(false);
            }
            if (btnOrientation != null) {
                chkFullscreen.setRect(0, btnOrientation.bottom() + GAP, WIDTH, BTN_HEIGHT);
            }else {
                chkFullscreen.setRect(0, 0, WIDTH, BTN_HEIGHT);
            }
            add(chkFullscreen);

            chkScaleUp = new CheckBox( TXT_SCALE_UP ) {
                @Override
                protected void onClick() {
                    super.onClick();
                    YetAnotherPixelDungeon.scaleUp(checked());
                }
            };

            chkScaleUp.setRect(0, chkFullscreen.bottom() + GAP, WIDTH, BTN_HEIGHT);
            chkScaleUp.checked(YetAnotherPixelDungeon.scaleUp());
            add(chkScaleUp);

            if (DeviceCompat.isAndroid()) {
                btnImmersive = new CheckBox(TXT_IMMERSIVE) {
                    @Override
                    protected void onClick() {
                        super.onClick();
                        YetAnotherPixelDungeon.immerse(checked());
                    }
                };
                btnImmersive.setRect(0, chkScaleUp.bottom() + GAP, WIDTH, BTN_HEIGHT);
                btnImmersive.checked(YetAnotherPixelDungeon.immersed());
                add(btnImmersive);
            }
		}
		
		CheckBox chkMusic = new CheckBox( TXT_MUSIC ) {
			@Override
			protected void onClick() {
				super.onClick();
				YetAnotherPixelDungeon.music(checked());
			}
		};
        if (!inGame) {
            if (btnImmersive != null) {
                chkMusic.setRect(0, btnImmersive.bottom() + GAP, WIDTH, BTN_HEIGHT);
            } else {
                chkMusic.setRect(0, chkScaleUp.bottom() + GAP, WIDTH, BTN_HEIGHT);
            }
        } else {
            chkMusic.setRect(0, chkBrightness.bottom() + GAP, WIDTH, BTN_HEIGHT);
        }
		chkMusic.checked( YetAnotherPixelDungeon.music() );
		add(chkMusic);

        CheckBox chkSound = new CheckBox( TXT_SOUND ) {
            @Override
            protected void onClick() {
                super.onClick();
                YetAnotherPixelDungeon.soundFx(checked());
                Sample.INSTANCE.play(Assets.SND_CLICK);
            }
        };
        chkSound.setRect(0, chkMusic.bottom() + GAP, WIDTH, BTN_HEIGHT);
        chkSound.checked(YetAnotherPixelDungeon.soundFx());
        add(chkSound);

//        RedButton btnTracks = new RedButton( buttonsText( YetAnotherPixelDungeon.buttons() ) ) {
//            @Override
//            protected void onClick() {
//                super.onClick();
//
//                boolean val = !YetAnotherPixelDungeon.buttons();
//
//                YetAnotherPixelDungeon.buttons( val );
//
//                Sample.INSTANCE.play( Assets.SND_CLICK );
//
//                text.text( buttonsText( val ) );
//                text.measure();
//                layout();
//            }
//        };
//
//        btnTracks.setRect(0, chkSound.bottom() + GAP, WIDTH, BTN_HEIGHT);
//        add(btnTracks);

        RedButton btnTipsDelay = new RedButton( loadingTipsText( YetAnotherPixelDungeon.loadingTips() ) ) {
            @Override
            protected void onClick() {
                int val = YetAnotherPixelDungeon.loadingTips();
                val = val < 3 ? val + 1 : 0;
                YetAnotherPixelDungeon.loadingTips(val);

                text.text( loadingTipsText( val ) );
                text.measure();
                layout();
            }
        };

//        btnTipsDelay.setRect(0, btnTracks.bottom() + GAP, WIDTH, BTN_HEIGHT);
        btnTipsDelay.setRect(0, chkSound.bottom() + GAP, WIDTH, BTN_HEIGHT);
        add(btnTipsDelay);

        resize(WIDTH, (int) btnTipsDelay.bottom());


//			CheckBox btnQuickslot = new CheckBox( TXT_QUICKSLOT ) {
//				@Override
//				protected void onClick() {
//					super.onClick();
//					Toolbar.secondQuickslot(checked());
//				}
//			};
//			btnQuickslot.setRect( 0, btnBrightness.bottom() + GAP, WIDTH, BTN_HEIGHT );
//			btnQuickslot.checked( Toolbar.secondQuickslot() );
//			add( btnQuickslot );

//			resize( WIDTH, (int)btnQuickslot.bottom() );

	}
	
	private void zoom( float value ) {

		Camera.main.zoom( value );
		YetAnotherPixelDungeon.zoom((int) (value - PixelScene.defaultZoom));

		updateEnabled();
	}
	
	private void updateEnabled() {
		float zoom = Camera.main.zoom;
		btnZoomIn.enable(zoom < PixelScene.maxZoom);
		btnZoomOut.enable(zoom > PixelScene.minZoom);
	}
	
	private String orientationText() {
		return PixelScene.landscape() ? TXT_SWITCH_PORT : TXT_SWITCH_LAND;
	}

    private String searchButtonsText( boolean val ) {
        return Utils.format( TXT_SEARCH_BTN, TXT_SEARCH_VAR[ val ? 1 : 0 ] );
    }

    private String loadingTipsText( int val ) {
        return Utils.format( TXT_LOADING_TIPS, TXT_TIPS_DELAY[ val ] );
    }

    private String buttonsText( boolean val ) {
        return Utils.format( TXT_BUTTONS, TXT_BUTTONS_VAR[ val ? 1 : 0 ] );
    }
}

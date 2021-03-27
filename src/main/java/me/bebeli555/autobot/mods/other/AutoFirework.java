package me.bebeli555.autobot.mods.other;

import me.bebeli555.autobot.AutoBot;
import me.bebeli555.autobot.gui.Group;
import me.bebeli555.autobot.gui.Mode;
import me.bebeli555.autobot.gui.Setting;
import me.bebeli555.autobot.utils.InventoryUtil;
import me.bebeli555.autobot.utils.PlayerUtil;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;

public class AutoFirework extends AutoBot {
	private static Thread thread, thread2;
	private static boolean lagback;
	private static int lagbackCounter;
	
	public static Setting delay = new Setting(Mode.DOUBLE, "Delay", 2.8, "The delay between clicks on the firework", "In seconds");
	public static Setting antiLagback = new Setting(Mode.BOOLEAN, "AntiLagback", true, "Doesnt click on a firework if ur lagbacking on 2b2t");
	
	public AutoFirework() {
		super(Group.OTHER, "AutoFirework", "Clicks on fireworks for you when flying with elytra");
	}
	
	@Override
	public void onEnabled() {
		thread = new Thread() {
			public void run() {
				while(thread != null && thread.equals(this)) {
					loop();
					
					AutoBot.sleep(50);
				}
			}
		};
		thread.start();
		
		thread2 = new Thread() {
			public void run() {
				while (thread2 != null && thread2.equals(this)) {
					double speed = PlayerUtil.getSpeed(mc.player);
					if (speed > 4) {
						lagback = true;
					}
					
					if (lagback) {
						if (speed < 1) {
							lagbackCounter++;
							if (lagbackCounter > 4) {
								lagback = false;
								lagbackCounter = 0;
							}
						} else {
							lagbackCounter = 0;
						}
					}
					
					AutoBot.sleep(50);
				}
			}
		};
		thread2.start();
	}
	
	@Override
	public void onDisabled() {
		thread = null;
		thread2 = null;
	}
	
	public void loop() {
		if (mc.player == null || !mc.player.isElytraFlying()) {
			return;
		}
		
		if (!InventoryUtil.hasItem(Items.FIREWORKS)) {
			sendMessage("You have no fireworks in inventory", true);
			toggleModule();
			return;
		}
		
		//Put the best firework to hand
		if (mc.player.getHeldItemMainhand().getItem() != Items.FIREWORKS) {
			InventoryUtil.switchItem(InventoryUtil.getItem(Items.FIREWORKS), false);
		}
		
		//Click
		if (!lagback) {
			mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
			sleepUntil(() -> !mc.player.isElytraFlying(), (int)(delay.doubleValue() * 1000));
		}
	}
}

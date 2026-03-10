package me.r1ver.skin.bukkit.listener;

import me.r1ver.skin.Skin;
import me.r1ver.skin.bukkit.skin.SkinLibrary;
import me.r1ver.skin.bukkit.skin.SkinType;
import me.r1ver.skin.bukkit.utils.SkinAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PlayerListener implements Listener {

	private final Skin plugin;

	public PlayerListener(Skin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPreLogin(AsyncPlayerPreLoginEvent e) {
		boolean hasData = plugin.getMysql().contains(e.getUniqueId()).join();
		if (!hasData) {
			String[] textures = SkinAPI.getFromName(e.getName());
			if (textures == null)
				return;
		} else {
			String skinNick = plugin.getMysql().getSkin(e.getUniqueId()).join();
			if (skinNick != null) {
			}
		}
	}

	@EventHandler
	public void clickInv(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		String skinName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
		SkinType skin = SkinLibrary.getSkin(skinName).orElse(null);
		if (skin != null) {
			SkinAPI.changePlayerSkin(player, skin.getValue(), skin.getSignature());
			plugin.getMysql().updateSkin(player.getUniqueId(), skin.getNick());

			player.sendMessage("§aSkin alterada!");
			player.closeInventory();
		}
		e.setCancelled(true);
	}
}
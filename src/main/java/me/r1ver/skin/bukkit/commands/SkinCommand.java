package me.r1ver.skin.bukkit.commands;

import me.r1ver.skin.Skin;
import me.r1ver.skin.bukkit.skin.SkinSelector;
import me.r1ver.skin.bukkit.utils.SkinAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class SkinCommand implements CommandExecutor {

	private final Skin plugin;
	private final String PREFIX = "§6§lSKIN §f";

	public SkinCommand(Skin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Este comando é exclusivo para jogadores.");
			return true;
		}

		Player player = (Player) sender;
		if (args.length == 0) {
			new SkinSelector().openMenuSkins(player);
			return true;
		}
		if (!player.hasPermission("skinchanger.use") || !player.hasPermission("skinchanger.admin")) {
			player.sendMessage(PREFIX + "Você precisa de VIP para usar skins customizadas.");
			return true;
		}

		String targetName = args[0];
		if (targetName.equalsIgnoreCase("atualizar")) {
			handleUpdate(player);
			return true;
		}
		handleSetSkin(player, targetName);
		return true;
	}

	private void handleUpdate(Player player) {
		player.sendMessage(PREFIX + "Buscando sua skin original...");
		CompletableFuture.runAsync(() -> {
			String[] textures = SkinAPI.getFromName(player.getName());

			if (textures == null) {
				player.sendMessage(PREFIX + "Não foi encontrada uma skin original para seu nome.");
				return;
			}
			plugin.getMysql().deleteUser(player.getUniqueId());
			SkinAPI.changePlayerSkin(player, textures[0], textures[1]);
			player.sendMessage(PREFIX + "Sua skin foi restaurada com sucesso!");
		});
	}

	private void handleSetSkin(Player player, String skinNick) {
		player.sendMessage(PREFIX + "Buscando skin de §6" + skinNick + "§f...");

		CompletableFuture.runAsync(() -> {
			String[] textures = SkinAPI.getFromName(skinNick);

			if (textures == null) {
				player.sendMessage(PREFIX + "Jogador original não encontrado.");
				return;
			}
			plugin.getMysql().updateSkin(player.getUniqueId(), skinNick);
			SkinAPI.changePlayerSkin(player, textures[0], textures[1]);
			player.sendMessage(PREFIX + "Sua skin foi alterada para §6" + skinNick);
		});
	}
}
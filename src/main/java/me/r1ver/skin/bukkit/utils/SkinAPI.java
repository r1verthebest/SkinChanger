package me.r1ver.skin.bukkit.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SkinAPI {

	private static final Map<String, String[]> REQUEST_CACHE = new ConcurrentHashMap<>();

	public static String[] getFromName(String name) {
		if (REQUEST_CACHE.containsKey(name.toLowerCase())) {
			return REQUEST_CACHE.get(name.toLowerCase());
		}

		try {
			URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
			InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
			String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();

			URL url_1 = new URL(
					"https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
			InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
			JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties")
					.getAsJsonArray().get(0).getAsJsonObject();

			String texture = textureProperty.get("value").getAsString();
			String signature = textureProperty.get("signature").getAsString();

			String[] result = new String[] { texture, signature };
			REQUEST_CACHE.put(name.toLowerCase(), result);
			return result;
		} catch (Exception e) {
			Bukkit.getLogger().warning("Erro ao buscar skin da Mojang para: " + name);
			return null;
		}
	}

	public static void changePlayerSkin(Player player, String value, String signature) {
		EntityPlayer ep = ((CraftPlayer) player).getHandle();
		GameProfile profile = ep.getProfile();

		profile.getProperties().removeAll("textures");
		profile.getProperties().put("textures", new Property("textures", value, signature));

		respawnPlayer(player);
	}

	@SuppressWarnings("deprecation")
	public static void respawnPlayer(Player p) {
		if (!p.isOnline())
			return;

		CraftPlayer cp = (CraftPlayer) p;
		EntityPlayer ep = cp.getHandle();
		Location l = p.getLocation();

		PacketPlayOutPlayerInfo removeInfo = new PacketPlayOutPlayerInfo(
				PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep);
		PacketPlayOutPlayerInfo addInfo = new PacketPlayOutPlayerInfo(
				PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep);

		PacketPlayOutEntityDestroy removeEntity = new PacketPlayOutEntityDestroy(ep.getId());
		PacketPlayOutNamedEntitySpawn addNamed = new PacketPlayOutNamedEntitySpawn(ep);

		PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(ep.getWorld().worldProvider.getDimension(),
				ep.getWorld().getDifficulty(), ep.getWorld().getWorldData().getType(),
				ep.playerInteractManager.getGameMode());

		PacketPlayOutPosition pos = new PacketPlayOutPosition(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(),
				Collections.emptySet());
		for (Player pOnline : Bukkit.getOnlinePlayers()) {
			PlayerConnection conn = ((CraftPlayer) pOnline).getHandle().playerConnection;

			if (pOnline.equals(p)) {
				conn.sendPacket(removeInfo);
				conn.sendPacket(addInfo);
				conn.sendPacket(respawn);
				conn.sendPacket(pos);
				cp.updateScaledHealth();
				cp.getHandle().triggerHealthUpdate();
				cp.updateInventory();
			} else {
				conn.sendPacket(removeEntity);
				conn.sendPacket(removeInfo);
				conn.sendPacket(addInfo);
				conn.sendPacket(addNamed);
				sendEquipment(p, pOnline);
			}
		}
	}

	private static void sendEquipment(Player target, Player watcher) {
		EntityPlayer et = ((CraftPlayer) target).getHandle();
		PlayerConnection conn = ((CraftPlayer) watcher).getHandle().playerConnection;

		for (int i = 0; i < 5; i++) {
			ItemStack item = et.getEquipment(i);
			if (item != null) {
				conn.sendPacket(new PacketPlayOutEntityEquipment(et.getId(), i, item));
			}
		}
	}
}
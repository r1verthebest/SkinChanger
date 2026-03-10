package me.r1ver.skin.bukkit.skin;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.r1ver.skin.Skin;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SkinLibrary {

	private static final Map<String, SkinType> SKINS = new ConcurrentHashMap<>();

	public static void load(Skin plugin) {
		SKINS.clear();

		File file = new File(plugin.getDataFolder(), "skins.yml");
		if (!file.exists()) {
			plugin.saveResource("skins.yml", false);
		}

		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		ConfigurationSection section = config.getConfigurationSection("skins");

		if (section == null)
			return;

		for (String key : section.getKeys(false)) {
			try {
				String name = key;
				String nick = section.getString(key + ".nick");
				String value = section.getString(key + ".value");
				String signature = section.getString(key + ".signature");
				String categoryStr = section.getString(key + ".category");
				SkinCategory category = SkinCategory.getByName(categoryStr).orElse(SkinCategory.DESENHO);

				SkinType skin = new SkinType(name, nick, value, signature, category);

				SKINS.put(name.toLowerCase(), skin);
				SKINS.put(nick.toLowerCase(), skin);

			} catch (Exception e) {
				plugin.getLogger().warning("Falha ao carregar skin: " + key);
			}
		}
		plugin.getLogger().info(SKINS.size() + " skins carregadas da biblioteca.");
	}

	public static Optional<SkinType> getSkin(String nameOrNick) {
		if (nameOrNick == null)
			return Optional.empty();
		return Optional.ofNullable(SKINS.get(nameOrNick.toLowerCase()));
	}

	public static List<SkinType> getByCategory(SkinCategory category) {
		List<SkinType> result = new ArrayList<>();
		for (SkinType skin : SKINS.values()) {
			if (skin.getCategoria() == category && !result.contains(skin)) {
				result.add(skin);
			}
		}
		return result;
	}
}
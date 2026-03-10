package me.r1ver.skin;

import lombok.Getter;
import me.r1ver.skin.bukkit.commands.SkinCommand;
import me.r1ver.skin.bukkit.listener.PlayerListener;
import me.r1ver.skin.bukkit.skin.SkinLibrary;
import me.r1ver.skin.database.MySQL;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

@Getter
public class Skin extends JavaPlugin {

	@Getter
	private static Skin instance;
	private MySQL mysql;

	@Override
	public void onEnable() {
		instance = this;
		try {
			setupConfig();
			setupDatabase();
			setupManagers();
			registerListeners();
			registerCommands();
			saveDefaultConfig();
			saveResource("skins.yml", false);

			getLogger().info("Plugin habilitado com sucesso!");
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, "Falha crítica ao iniciar o plugin. Desativando...", e);
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	private void setupConfig() {
		saveDefaultConfig();
	}

	private void setupDatabase() {
		var section = getConfig().getConfigurationSection("MySQL");
		if (section == null) {
			throw new IllegalStateException("Seção 'MySQL' não encontrada no config.yml");
		}

		this.mysql = new MySQL(section.getString("user"), section.getString("host"), section.getString("password"),
				section.getString("database"), section.getInt("port"));

		this.mysql.createTables();
	}

	private void setupManagers() {
		SkinLibrary.load(this);
	}

	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
	}

	private void registerCommands() {
		var command = getCommand("skin");
		if (command != null) {
			command.setExecutor(new SkinCommand(this));
		}
	}

	@Override
	public void onDisable() {
		if (mysql != null) {
			mysql.close();
		}
	}
}
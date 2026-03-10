package me.r1ver.skin.bukkit.skin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.r1ver.skin.bukkit.ItemBuilder;

import java.util.Arrays;
import java.util.List;

public class SkinSelector {

	private static final String TITLE_MAIN = "Skins";
	private static final String TITLE_THEMES = "Selecionar tema";
	private static final String TITLE_LIBRARY = "Biblioteca de skins";
	private static final List<Integer> SLOTS = Arrays.asList(10, 11, 12, 13, 14, 15, 16);

	public void openMenuSkins(Player p) {
		Inventory inv = Bukkit.createInventory(null, 3 * 9, TITLE_MAIN);
		inv.setItem(12, new ItemBuilder(Material.NAME_TAG).name("§aSkin Customizada")
		    .lore("§7Escolha uma outra skin.", "§7Que não está em nosso catálogo.").build());

		inv.setItem(14, new ItemBuilder(Material.BOOKSHELF).name("§aBiblioteca de Skins")
		    .lore("§7Selecione uma skin em nosso catálogo.").build());

		p.openInventory(inv);
	}

	public void openBibliotecaMenu(Player p) {
		Inventory inv = Bukkit.createInventory(null, 3 * 9, TITLE_THEMES);
		SkinCategory[] categories = SkinCategory.values();

		for (int i = 0; i < Math.min(SLOTS.size(), categories.length); i++) {
			SkinCategory category = categories[i];
			inv.setItem(SLOTS.get(i), createSkull(category.getName(), category.getNick()));
		}

		p.openInventory(inv);
	}

	public void openInventory(Player p, SkinCategory categoria) {
	    Inventory inv = Bukkit.createInventory(null, 3 * 9, TITLE_LIBRARY);
	    List<SkinType> skins = SkinLibrary.getByCategory(categoria);

	    for (int i = 0; i < Math.min(SLOTS.size(), skins.size()); i++) {
	        SkinType skin = skins.get(i);
	        ItemStack skull = new ItemBuilder(Material.SKULL_ITEM)
	                .durability(3)
	                .name("§a" + skin.getName())
	                .skinValue(skin.getValue()) 
	                .lore("§7Nick: " + skin.getNick(), "", "§e§lCLIQUE PARA SELECIONAR")
	                .build();

	        inv.setItem(SLOTS.get(i), skull);
	    }

	    p.openInventory(inv);
	}

	private ItemStack createSkull(String displayName, String owner) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta meta = (SkullMeta) skull.getItemMeta();

		if (meta != null) {
			meta.setOwner(owner);
			meta.setDisplayName("§a" + displayName);
			meta.setLore(Arrays.asList("", "§e§lCLIQUE AQUI"));
			skull.setItemMeta(meta);
		}
		return skull;
	}
}
package me.r1ver.skin.bukkit;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public class ItemBuilder {

	private Material material = Material.STONE;
	private int amount = 1;
	private short durability = 0;
	private String displayName;
	private List<String> lore = new ArrayList<>();
	private Map<Enchantment, Integer> enchantments = new HashMap<>();
	private Color color;
	private String skinOwner;
	private String skinValue;
	private boolean hideAttributes = false;
	private boolean unbreakable = false;

	public ItemBuilder(Material material) {
		this.material = material;
	}
	
	public ItemBuilder durability(int durability) {
	    this.durability = (short) durability;
	    return this;
	}

	public ItemBuilder(ItemStack stack) {
		this.material = stack.getType();
		this.amount = stack.getAmount();
		this.durability = stack.getDurability();
		if (stack.hasItemMeta()) {
			ItemMeta meta = stack.getItemMeta();
			this.displayName = meta.getDisplayName();
			this.lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
			this.unbreakable = meta.spigot().isUnbreakable();
		}
	}

	public ItemBuilder type(Material material) {
		this.material = material;
		return this;
	}

	public ItemBuilder name(String name) {
		this.displayName = ChatColor.translateAlternateColorCodes('&', name);
		return this;
	}

	public ItemBuilder lore(String... lines) {
		for (String line : lines) {
			this.lore.add(ChatColor.translateAlternateColorCodes('&', line));
		}
		return this;
	}

	public ItemBuilder skin(String owner) {
		this.skinOwner = owner;
		return this;
	}

	public ItemBuilder skinValue(String value) {
		this.skinValue = value;
		return this;
	}

	public ItemBuilder color(Color color) {
		this.color = color;
		return this;
	}

	public ItemBuilder hideAttributes() {
		this.hideAttributes = true;
		return this;
	}

	public ItemStack build() {
		ItemStack item = new ItemStack(material, amount, durability);
		ItemMeta meta = item.getItemMeta();

		if (meta != null) {
			if (displayName != null)
				meta.setDisplayName(displayName);
			if (!lore.isEmpty())
				meta.setLore(lore);

			if (hideAttributes)
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
			meta.spigot().setUnbreakable(unbreakable);
			if (color != null && meta instanceof LeatherArmorMeta) {
				((LeatherArmorMeta) meta).setColor(color);
			}
			if (meta instanceof SkullMeta) {
				applySkullMeta((SkullMeta) meta);
			}

			item.setItemMeta(meta);
		}

		enchantments.forEach(item::addUnsafeEnchantment);
		return item;
	}

	private void applySkullMeta(SkullMeta meta) {
		if (skinValue != null) {
			GameProfile profile = new GameProfile(UUID.randomUUID(), null);
			profile.getProperties().put("textures", new Property("textures", skinValue));
			try {
				Field field = meta.getClass().getDeclaredField("profile");
				field.setAccessible(true);
				field.set(meta, profile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (skinOwner != null) {
			meta.setOwner(skinOwner);
		}
	}
}
package me.r1ver.skin.bukkit.skin;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum SkinCategory {

    HEROIS("Heróis", "r1ver"),
    FUTEBOL("Futebol", "r1ver"),
    TERROR("Terror", "r1ver"),
    FILME("Filmes", "r1ver"),
    DESENHO("Desenhos", "r1ver");

    private final String name;
    private final String nick;

    private static final Map<String, SkinCategory> BY_NAME = new HashMap<>();

    static {
        for (SkinCategory category : values()) {
            BY_NAME.put(category.name.toLowerCase(), category);
        }
    }

    public static Optional<SkinCategory> getByName(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(BY_NAME.get(categoryName.toLowerCase()));
    }
}
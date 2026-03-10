package me.r1ver.skin.bukkit.skin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SkinType {

	private final String name;
	private final String nick;
	private final String value;
	private final String signature;
	private final SkinCategory categoria;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SkinType skinType = (SkinType) o;
		return name.equalsIgnoreCase(skinType.name);
	}

	@Override
	public int hashCode() {
		return name.toLowerCase().hashCode();
	}
}
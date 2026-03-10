# 🎭 SkinChanger | Advanced Skin Management System

![Version](https://img.shields.io/badge/version-1.0.0-green)
![Platform](https://img.shields.io/badge/platform-Spigot-gold)
![Java](https://img.shields.io/badge/java-8-orange)
![Database](https://img.shields.io/badge/database-MySQL-blue)

**SkinChanger** é uma solução robusta e de alta performance para gerenciamento de skins em servidores de Minecraft (v1.8.8). Desenvolvido com foco em escalabilidade, o plugin utiliza manipulação de pacotes NMS e texturas em Base64 para garantir trocas de skin instantâneas e persistência de dados via MySQL de forma assíncrona.

---

## 🚀 Diferenciais Técnicos

* **Zero-Lag GUI:** Menus carregados via texturas Base64 (Skin Value), eliminando as requisições bloqueantes à API da Mojang ao abrir inventários.
* **Asynchronous Database:** Integração com **HikariCP** para garantir que as operações de banco de dados não afetem o TPS do servidor.
* **Packet-Level Refresh:** Utiliza pacotes `PacketPlayOutRespawn` e `PacketPlayOutPlayerInfo` para atualizar a skin do jogador em tempo real para todos ao redor.
* **Hybrid Storage:** Sistema flexível que combina um catálogo de skins via **YAML** com armazenamento persistente em **MySQL**.

---

## 🛠 Stack Tecnológica

* **Build Tool:** Maven 3.8+
* **Core API:** Spigot 1.8.8-R0.1-SNAPSHOT (v1_8_R3)
* **Connection Pool:** HikariCP (Máxima performance em conexões SQL)
* **Boilerplate Reduction:** Lombok
* **JSON Engine:** Google GSON

---

## 📁 Arquitetura do Sistema

O projeto segue padrões de código limpo e injeção de dependência:

* `me.r1ver.skin.bukkit.skin`: Gerenciamento da `SkinLibrary` e lógica do `SkinSelector` (Menus).
* `me.r1ver.skin.database`: Camada de persistência MySQL rodando em modo **Async**.
* `me.r1ver.skin.bukkit.utils`: Utilitários sêniores como `ItemBuilder` e a `SkinAPI` (NMS).
* `me.r1ver.skin.bukkit.listener`: Handlers de eventos para integração de menus e entrada de jogadores.

---

🗄 Persistência MySQL
Ideal para redes de servidores (BungeeCord/Velocity):
```yaml
MySQL:
  host: '127.0.0.1'
  port: 3306
  database: 'skin_system'
  user: 'root'
  password: ''
````
## ⚙️ Funcionalidades & Configuração

### 📦 Catálogo de Skins (`skins.yml`)
Adicione skins personalizadas que aparecem automaticamente no menu de temas:
```yaml
skins:
  Neymar:
    nick: "NeymarJR"
    category: "Futebol"
    value: "eyJ0ZXh0dXJlcyI6eyJTS0lO..." # Base64 da textura
    signature: "..." # Assinatura oficial da Mojang
```
🏗 Como Compilar
Certifique-se de ter o Maven instalado.
Clone o repositório.
Execute o comando:
```yaml
mvn clean package
```

Desenvolvido por r1ver 🚀

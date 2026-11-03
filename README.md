# ShopHS-Java-mc-plugin-spigot-1.20.4-
free Simple GUI item shop plugin for Spigot/Paper with Vault economy support.
# ShopHs

A simple GUI item shop plugin for **Spigot / Paper** (1.20.x) with **Vault** economy support.

> Created by ib-FF77

## Features

- GUI shop menu: click an item to buy it
- Add the item in your main hand with a custom price (`/shophs add <price>`)
- Remove items by row number (`/shophs remove <row>`)
- Persistent storage in `config.yml` (survives restarts)
- Vault economy integration
- Checks balance and inventory space before purchase

## Commands

| Command | Description | Permission |
|---|---|---|
| `/shophs` | Open the shop menu | `shophs.use` (everyone) |
| `/shophs add <price>` | Add the item in your main hand (full stack) | `shophs.admin` (OP) |
| `/shophs remove <row>` | Remove an item by its row number | `shophs.admin` (OP) |
| `/shophs info` | Show plugin author | `shophs.use` (everyone) |

## Requirements

- Spigot or Paper 1.20.x
- Java 17
- [Vault](https://www.spigotmc.org/resources/vault.34315/) and an economy plugin (e.g. EssentialsX)

Without Vault, the plugin still loads but purchasing is disabled.

## Installation

1. Download `ShopHs-1.0.0.jar` from the Releases page, or build it yourself.
2. Put the jar in your server's `plugins` folder.
3. Restart the server.

## Building from source

```bash
mvn clean package
```

The output jar will be in `target/ShopHs-1.0.0.jar`.

## Limitations

- Maximum of 54 items (one large chest GUI). Pagination is not implemented yet.

## License

MIT. See [LICENSE](LICENSE) for details.
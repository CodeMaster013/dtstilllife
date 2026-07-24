# Dynamic Trees - Still Life

A [Dynamic Trees](https://github.com/DynamicTreesTeam/DynamicTreesNeoForge) addon that adds procedural tree generation for biomes from the [Still Life](https://www.curseforge.com/minecraft/mc-mods/still-life) biome mod.

## Features

- Integrates 35+ tree species with Still Life's 90+ biomes
- Custom growth logic for willow, redwood, juniper, cypress, baobab, palm, poplar, and shrub trees
- Custom cell kits for realistic leaf and branch behavior
- Feature cancellation to prevent vanilla tree placement in Dynamic Trees areas
- Forest soil generation for proper terrain integration

## Dependencies

| Mod | Version | Status |
|-----|---------|--------|
| [Minecraft](https://www.minecraft.net) | 1.21.1 | Required |
| [NeoForge](https://neoforged.net) | 21.1+ | Required |
| [Dynamic Trees](https://github.com/DynamicTreesTeam/DynamicTreesNeoForge) | 1.7.0+ | Required |
| [Dynamic Trees Plus](https://github.com/DynamicTreesTeam/DynamicTreesPlusNeoForge) | 1.3.2+ | Required |
| [Still Life](https://www.curseforge.com/minecraft/mc-mods/still-life) | 0.1.1+ | Required |

## Installation

1. Install [NeoForge](https://neoforged.net) for Minecraft 1.21.1
2. Install the required mods listed above
3. Place the `dtstilllife` JAR file in your `mods` folder

## Building from Source

### Requirements

- Java 21
- Git

### Steps

```bash
git clone https://github.com/CodeMaster013/dtstilllife.git
cd dtstilllife
./gradlew build
```

The built JAR will be in `build/libs/`.

## License

[MIT](LICENSE)

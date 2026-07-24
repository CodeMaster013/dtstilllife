# Contributing to Dynamic Trees - Still Life

Thanks for your interest in contributing!

## Getting Started

1. Fork the repository
2. Clone your fork
3. Create a branch for your changes
4. Make your changes
5. Test in-game with Minecraft 1.21.1 + NeoForge + Dynamic Trees + Still Life
6. Submit a pull request

## Development Setup

### Requirements

- Java 21
- Git
- An IDE (IntelliJ IDEA recommended)
- Minecraft 1.21.1 with NeoForge development workspace

### Building

```bash
./gradlew build
```

The output JAR is in `build/libs/`.

### Running the Client

```bash
./gradlew runClient
```

### Running the Data Generator

```bash
./gradlew runData
```

## Project Structure

```
src/main/java/wexlabs/
  Mod.java                          # Entry point
  Registrator.java                  # Registration of all DT components
  dtstilllife/
    cancellers/                     # Feature cancellation logic
    trees/
      cells/                        # Cell kits, leaf clusters, branch cells
      features/                     # Gen features (soil, flare, replace)
      growthlogic/                  # Growth logic kits per species
      species/                      # Custom species and family types
```

## Code Guidelines

- Follow the existing code style (naming conventions, indentation)
- Use `com.dtteam.dynamictrees` imports (not the old `com.ferreusveritas` package)
- Verify API usage against the actual Dynamic Trees JAR if unsure
- Keep biome references in `default.json` in sync with Still Life's actual biome registry IDs
- Test your changes in-game before submitting

## Biome Data

Biome IDs in `default.json` must match Still Life's actual registry. Before adding biomes:

1. Check the Still Life mod's biome registry (use `/locate biome still_life:biome_name` in-game)
2. Add only biomes that actually exist in the current Still Life version
3. Assign appropriate tree species based on the biome's climate and vegetation

## Pull Requests

- Keep PRs focused on a single change
- Include a description of what you changed and why
- Reference any related issues
- Make sure the project builds without errors (`./gradlew build`)

## Reporting Issues

- Use GitHub Issues for bug reports
- Include your Minecraft version, NeoForge version, Dynamic Trees version, and Still Life version
- Describe steps to reproduce the issue
- Include crash logs if applicable (use a paste service like [Gist](https://gist.github.com))

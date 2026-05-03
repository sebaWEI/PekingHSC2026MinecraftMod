# SynBio Crafter

[![Minecraft](https://img.shields.io/badge/Minecraft-26.1.2-brightgreen)](https://minecraft.net)
[![Fabric](https://img.shields.io/badge/Fabric%20Loader-%3E%3D0.18.6-orange)](https://fabricmc.net)
[![Java](https://img.shields.io/badge/Java-%3E%3D25-blue)](https://adoptium.net)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

An educational Minecraft mod that teaches **synthetic biology** through hands-on gameplay. Built for the **iGEM 2026** competition by Peking HSC.

> 🧬 Extract DNA → 🧪 Assemble plasmids → 🦠 Transform bacteria → 💊 Express proteins → ⚔️ Bio-enhance your gear

---

## Gameplay Loop

| Step | What You Do | Real Science |
|------|-------------|-------------|
| 1. **Collect** | Kill mobs to obtain `Unknown Biological Tissue` | Sample collection from nature |
| 2. **Identify** | Place tissue in crafting table to discover DNA parts | Gene sequencing & annotation |
| 3. **Assemble** | Use the Plasmid Assembler to combine regulatory parts + CDS | Molecular cloning |
| 4. **Transform** | Combine competent *E. coli* with engineered plasmid | Bacterial transformation |
| 5. **Express** | Culture engineered bacteria with nutrient broth in Incubator | Protein expression |
| 6. **Enhance** | Use purified proteins at the Smithing Table to upgrade gear | Protein engineering |

---

## Features

### 5 DNA Regulatory Part Types (4 Rarity Tiers each)

| Part | Symbol    | Real-World Role |
|------|-----------|----------------|
| **Promoter** | → (arrow) | Initiates transcription |
| **UTR5** | 5′        | Ribosome binding site |
| **UTR3** | 3‘        | Poly-A tail / terminator |
| **Enhancer** | ✦ (star)  | Boosts expression |
| **SINEB2** | S         | Translation-enhancing retrotransposon |

Rarities: Green (65%) → Blue (25%) → Purple (8%) → Gold (2%) — higher rarity = stronger expression multiplier.

### 8 CDS Genes (Coding Sequences)

| Gene | Loot Source | Protein Effect (Smithing) |
|------|------------|--------------------------|
| **GFP** | Glow Squid | Diagnostic tool — scan plasmids |
| **Keratin** | Zombie | Armor toughness + knockback resist |
| **Myosin** | Cow | Sword damage / Boot speed |
| **Telomerase** | Enderman | Unbreaking → Mending → Unbreakable |
| **Spider Silk** | Spider | Bow/Crossbow Power enchantment |
| **Luciferase** | Blaze | Helmet night vision |
| **PETase** | Drowned | Block recycler (glass→sand, wool→string) |
| **Ice Nucleation** | Stray | Boots Frost Walker |

### Blocks & Machines

- **Plasmid Assembler** — 9-slot crafting station for plasmid assembly (1 core + 8 part slots)
- **Incubator** — Cultivate bacteria with graded nutrient broth to express proteins

### 4-Tier Nutrient Broth System

| Tier | Medium | Required For |
|------|--------|-------------|
| Basic (Green) | Iron + Wheat + Sugar | Tier I proteins |
| Advanced (Blue) | Prismarine / Glow Berries | Tier II proteins |
| High-end (Purple) | Ender Pearl / Chorus / Blaze | Tier III proteins |
| Ultimate (Gold) | Golden Apple | Tier IV proteins |

### Biosafety

Expressing proteins produces **Waste Culture** as a byproduct. Accumulating ≥10 waste items near you causes Poison; ≥30 adds Nausea. Dispose of waste by smelting it into Bone Meal — a nod to real-world autoclave sterilization.

---

## Installation

### Requirements

- **Minecraft** 26.1.2
- **Fabric Loader** ≥ 0.18.6
- **Fabric API** (latest for 26.1.2)
- **Java** ≥ 25

### Steps

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 26.1.2
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) and place it in `.minecraft/mods/`
3. Download `synbio-1.0.0.jar` from [Releases](https://github.com/sebaWEI/PekingHSC2026MinecraftMod/releases)
4. Place `synbio-1.0.0.jar` in `.minecraft/mods/`
5. Launch Minecraft with the Fabric profile

---

## Building from Source

```bash
git clone https://github.com/sebaWEI/PekingHSC2026MinecraftMod.git
cd PekingHSC2026MinecraftMod
./gradlew build
```

Output: `build/libs/synbio-1.0.0.jar`

Requires JDK 25+ and Gradle (wrapper included).

---

## Getting Started in Survival

1. Kill mobs to collect `Unknown Biological Tissue` (25% drop, 34% from monsters)
2. Place a single tissue in your inventory/crafting grid to identify its hidden DNA part
3. Collect 9 same-rarity regulatory parts → upgrade rarity via crafting (9→1)
4. Kill specific mobs for CDS drops (see table above)
5. Craft a `Plasmid Assembler` (8 iron ingots around a crafting table)
6. Assemble: place `Blank Plasmid` in center + DNA parts around it
7. Craft `Calcium Chloride` (stone + water bucket), combine with wild *E. coli* → competent cells
8. Combine competent *E. coli* + engineered plasmid → engineered bacteria
9. Craft nutrient broth, culture in `Incubator` → proteins
10. Use proteins at Smithing Table → enhanced equipment

## Project Structure

```
src/main/java/name/modid/
├── IgemMod.java              # Main entry point
├── block/                    # Blocks & block entities
├── component/                # Data components & effects
├── item/                     # Items & custom item classes
├── loot/                     # Loot table modifications
├── recipe/                   # Custom recipes
└── screen/                   # GUI handlers
```

---

## Credits

- **Developer**: Ziheng Wei (Sebastian Wei) — Peking HSC, iGEM 2026
- **Built with**: [Fabric Loom](https://fabricmc.net/wiki/documentation:fabric_loom), Minecraft 26.1.2 (Mojang Mappings)
- **Special thanks**: The iGEM community and the Human Practices principle of responsible science

## License

MIT — feel free to learn from, modify, and share this project. Attribution appreciated.

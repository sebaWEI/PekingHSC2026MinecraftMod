@SynBio Crafter Status Please read the current status document. The technical foundation is excellent, but we have a "survival loop gap" (Section 4). I want to implement Phase 5 to make the mod fully playable in Survival mode.

Please implement the following:

**Task 1: Starter E. coli Recipe**
Generate the JSON recipe `craft_e_coli_wild.json`: Shapeless recipe using Dirt (or Mud), Glass Bottle, and Sugar to output `e_coli_wild`. Add this to the TODO list.

**Task 2: Mob-Specific CDS Drops (Loot Injection)**
Update the `ModLoot` event class. Instead of randomly dropping CDS, inject specific CDS items into specific vanilla entity loot tables:
- `minecraft:glow_squid` -> `dna_part_cds_gfp` (50% chance)
- `minecraft:spider` or `minecraft:cave_spider` -> `dna_part_cds_spider_silk` (If you have this CDS, else skip or use a relevant one)
- `minecraft:zombie` -> `dna_part_cds_keratin` (if exists)
Keep the `unknown_biological_tissue` dropping globally, but slightly increase its chance for hostile mobs (`EntityCategory.MONSTER`).

**Task 3: Tissue Discovery Polish**
Ensure the `TissueDiscoveryRecipe` (converting tissue to regulatory parts) is working. No code needed if it's already functional, just confirm.

**Task 4: Assembler GUI Quick-Fix**
In the `PlasmidAssemblerScreen` and `PlasmidAssemblerScreenHandler`, change the background texture from the Crafting Table to the Dispenser texture (`textures/gui/container/dispenser.png`). Adjust the slot X,Y coordinates to fit the Dispenser's 3x3 grid (Slot 0 in the absolute center, Slots 1-8 forming a ring around it). Update the `GeneticDesign` constraint to only allow 8 regulatory parts maximum if you haven't already.

Please provide the updated Java classes for these tasks.
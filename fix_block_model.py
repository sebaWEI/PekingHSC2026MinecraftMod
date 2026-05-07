#!/usr/bin/env python3
"""
Patch Blockbench-exported block model JSONs:
  1. Add gui_light: "side"
  2. Add standard display transforms (gui, ground, fixed, hands)
  3. Fix texture reference namespace

Usage: python3 fix_block_model.py <model.json> <texture_name>
  e.g. python3 fix_block_model.py models/block/incubator.json incubator
"""
import json, sys, os

if len(sys.argv) < 3:
    print("Usage: fix_block_model.py <model.json> <texture_name>")
    print("  texture_name: e.g. 'incubator' → references synbio:block/incubator")
    sys.exit(1)

path = sys.argv[1]
tex_name = sys.argv[2]
tex_ref = f"synbio:block/{tex_name}"

with open(path) as f:
    model = json.load(f)

# Fix texture references
if "textures" in model:
    for k in model["textures"]:
        if model["textures"][k].startswith("synbio:item/") or "texture" in model["textures"][k]:
            model["textures"][k] = tex_ref

# Add gui_light
if "gui_light" not in model:
    model["gui_light"] = "side"

# Add display transforms
model["display"] = {
    "gui": {
        "rotation": [30, 225, 0],
        "translation": [0, 0, 0],
        "scale": [0.625, 0.625, 0.625]
    },
    "ground": {
        "rotation": [0, 0, 0],
        "translation": [0, 3, 0],
        "scale": [0.25, 0.25, 0.25]
    },
    "fixed": {
        "rotation": [0, 180, 0],
        "scale": [0.5, 0.5, 0.5]
    },
    "thirdperson_righthand": {
        "rotation": [75, 45, 0],
        "translation": [0, 2.5, 0],
        "scale": [0.375, 0.375, 0.375]
    },
    "firstperson_righthand": {
        "rotation": [0, 45, 0],
        "scale": [0.4, 0.4, 0.4]
    }
}

with open(path, 'w') as f:
    json.dump(model, f, indent='\t')

# Also delete stale models/item/<name>.json if items/<name>.json points directly to block
item_path = path.replace("models/block/", "items/")
item_model_path = path.replace("models/block/", "models/item/")
if os.path.exists(item_path):
    with open(item_path) as f:
        item_def = json.load(f)
    if item_def.get("model", {}).get("model", "").startswith("synbio:block/"):
        if os.path.exists(item_model_path):
            os.remove(item_model_path)
            print(f"  Removed redundant: {item_model_path}")

print(f"Patched: {path}")
print(f"  texture → {tex_ref}")
print(f"  added display transforms + gui_light")

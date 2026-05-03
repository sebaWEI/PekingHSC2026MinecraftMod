#!/usr/bin/env python3
"""Generate pixel-art textures for SynBio Crafter mod items."""
import math
from PIL import Image, ImageDraw

OUT = "/Users/weiziheng/Documents/igem_minecraft_mod/igem-mod-template-26.1.2/src/main/resources/assets/synbio/textures/item"
S = 16  # 16x16 Minecraft item texture

def new_icon():
    return Image.new("RGBA", (S, S), (0, 0, 0, 0))

def fill_rect(draw, x, y, w, h, color):
    draw.rectangle([x, y, x + w - 1, y + h - 1], fill=color)

def circle_mask(size, cx, cy, r):
    """Return a mask Image for a circle."""
    mask = Image.new("L", (S, S), 0)
    md = ImageDraw.Draw(mask)
    for dx in range(-r, r + 1):
        for dy in range(-r, r + 1):
            if dx * dx + dy * dy <= r * r:
                px, py = cx + dx, cy + dy
                if 0 <= px < S and 0 <= py < S:
                    md.point((px, py), 255)
    return mask

def draw_circle(draw, cx, cy, r, color):
    for dx in range(-r, r + 1):
        for dy in range(-r, r + 1):
            if dx * dx + dy * dy <= r * r:
                px, py = cx + dx, cy + dy
                if 0 <= px < S and 0 <= py < S:
                    draw.point((px, py), color)

def draw_helix(draw, x, y, color, dark):
    """Draw a simple DNA helix symbol in a 6x12 area."""
    for row in range(12):
        offset = int(2 * math.sin(row * 0.8))
        for col in range(6):
            px = x + col
            py = y + row
            # Two strands
            if col == 1 + offset or col == 4 - offset:
                draw.point((px, py), color)
            # Rungs
            elif (1 + offset) < col < (4 - offset) and row % 3 == 1:
                draw.point((px, py), dark)

def draw_star(draw, cx, cy, r, color):
    """Simple 4-pointed star."""
    for d in range(-r, r + 1):
        draw.point((cx + d, cy), color)
        draw.point((cx, cy + d), color)
        if abs(d) <= r // 2:
            draw.point((cx + d, cy + d), color)
            draw.point((cx + d, cy - d), color)

def draw_flask_outline(draw, color):
    """E. coli / bacteria: simple flask shape."""
    # bottom
    fill_rect(draw, 5, 9, 6, 5, color)
    # neck
    fill_rect(draw, 6, 6, 4, 3, color)
    # top
    fill_rect(draw, 5, 3, 6, 3, color)
    # highlight
    fill_rect(draw, 7, 10, 1, 3, (*color[:3], 100))

# ══════════════════════════════════════════════════════════
# 1. MISSING TEXTURES (4 files)
# ══════════════════════════════════════════════════════════

# calcium_chloride — white crystalline powder
img = new_icon(); d = ImageDraw.Draw(img)
fill_rect(d, 4, 4, 8, 8, (220, 220, 230, 255))
# crystal highlights
for px, py in [(5, 5), (9, 5), (5, 9), (6, 6)]:
    d.point((px, py), (255, 255, 255, 255))
for px, py in [(10, 7), (8, 11), (4, 8)]:
    d.point((px, py), (180, 180, 200, 255))
img.save(f"{OUT}/calcium_chloride.png")

# dna_part_utr3 — green UTR segment (3' end)
img = new_icon(); d = ImageDraw.Draw(img)
fill_rect(d, 3, 2, 10, 12, (60, 180, 80, 255))  # green body
fill_rect(d, 3, 2, 10, 3, (30, 140, 50, 255))    # cap
fill_rect(d, 3, 11, 10, 3, (30, 140, 50, 255))   # tail
d.text((4, 5), "3", fill=(0, 80, 20, 255))
img.save(f"{OUT}/dna_part_utr3.png")

# dna_part_utr5 — blue UTR segment (5' end)
img = new_icon(); d = ImageDraw.Draw(img)
fill_rect(d, 3, 2, 10, 12, (80, 140, 220, 255))  # blue body
fill_rect(d, 3, 2, 10, 3, (40, 80, 180, 255))
fill_rect(d, 3, 11, 10, 3, (40, 80, 180, 255))
d.text((4, 5), "5", fill=(20, 40, 100, 255))
img.save(f"{OUT}/dna_part_utr5.png")

# e_coli_competent — E. coli with Ca2+ glow
img = new_icon(); d = ImageDraw.Draw(img)
# cell body
draw_circle(d, 8, 8, 6, (220, 200, 230, 255))
# inner detail
draw_circle(d, 8, 8, 4, (200, 180, 210, 255))
# Ca2+ sparkles
for ox, oy in [(-3, -2), (3, -3), (-2, 4), (4, 3)]:
    draw_circle(d, 8 + ox, 8 + oy, 1, (255, 255, 100, 255))
img.save(f"{OUT}/e_coli_competent.png")

print("✅ 4 missing textures done.")

# ══════════════════════════════════════════════════════════
# 2. PROTEIN TEXTURES — 8 types × 4 tiers
# ══════════════════════════════════════════════════════════

PROTEINS = {
    "gfp":          (76, 210, 80),     # green fluorescent
    "keratin":      (200, 170, 120),   # tan/keratin
    "myosin":       (220, 60, 50),     # muscle red
    "telomerase":   (130, 80, 220),    # purple enzyme
    "spider_silk":  (230, 230, 230),   # silver white
    "luciferase":   (240, 220, 50),    # golden glow
    "petase":       (50, 180, 160),    # teal enzyme
    "ice_nucleation": (140, 200, 240), # ice blue
}

# Tier colors (glow/border intensity)
TIER_GLOW = {1: 0, 2: 1, 3: 2, 4: 3}

def make_protein_texture(base_color, tier, name):
    img = new_icon(); d = ImageDraw.Draw(img)
    r, g, b = base_color

    # Main protein blob/crystal shape
    if name == "gfp":
        # GFP: barrel shape (octagon-like)
        pts = [(8, 3), (12, 5), (13, 9), (12, 13), (8, 14), (4, 13), (3, 9), (4, 5)]
        d.polygon(pts, fill=(r, g, b, 255))
        # inner glow
        draw_circle(d, 8, 8, 3, (min(255, r + 60), min(255, g + 60), min(255, b + 30), 180))
    elif name == "keratin":
        # Keratin: layered plates
        fill_rect(d, 3, 3, 10, 10, (r, g, b, 255))
        fill_rect(d, 3, 3, 10, 3, (min(255, r+40), min(255, g+40), min(255, b+40), 255))
        fill_rect(d, 3, 10, 10, 3, (max(0, r-30), max(0, g-30), max(0, b-30), 255))
        # keratin fiber lines
        for ly in [5, 8]:
            fill_rect(d, 4, ly, 8, 1, (max(0, r-20), max(0, g-20), max(0, b-20), 150))
    elif name == "myosin":
        # Myosin: muscle fiber / dumbell shape
        fill_rect(d, 3, 4, 10, 8, (r, g, b, 255))
        # head domains
        draw_circle(d, 5, 5, 3, (min(255, r+50), g, b, 255))
        draw_circle(d, 11, 11, 3, (min(255, r+50), g, b, 255))
        # coiled-coil tail
        fill_rect(d, 6, 7, 4, 2, (max(0, r-40), g, max(0, b-40), 255))
    elif name == "telomerase":
        # Telomerase: chromosome cap
        fill_rect(d, 4, 2, 8, 12, (r, g, b, 255))
        # telomere repeat pattern
        for row in range(3, 12, 2):
            fill_rect(d, 4, row, 8, 1, (min(255, r+40), min(255, g+40), min(255, b+40), 200))
        # cap
        draw_circle(d, 8, 2, 3, (min(255, r+50), min(255, g+30), min(255, b+60), 255))
    elif name == "spider_silk":
        # Spider silk: thread/spindle
        fill_rect(d, 7, 2, 2, 12, (r, g, b, 255))
        # shimmer
        fill_rect(d, 8, 4, 1, 3, (255, 255, 255, 200))
        fill_rect(d, 8, 9, 1, 3, (255, 255, 255, 200))
    elif name == "luciferase":
        # Luciferase: glowing orb with rays
        draw_circle(d, 8, 8, 5, (r, g, b, 255))
        draw_circle(d, 8, 8, 3, (255, 255, 180, 220))
        # light rays
        for angle in [0, 45, 90, 135, 180, 225, 270, 315]:
            rad = math.radians(angle)
            ex = int(8 + 7 * math.cos(rad))
            ey = int(8 + 7 * math.sin(rad))
            if 0 <= ex < S and 0 <= ey < S:
                d.point((ex, ey), (255, 255, 200, 200))
    elif name == "petase":
        # PETase: enzyme with substrate
        fill_rect(d, 3, 3, 10, 10, (r, g, b, 255))
        # active site cleft
        fill_rect(d, 5, 6, 6, 4, (max(0, r-30), max(0, g-30), max(0, b-30), 255))
        # substrate fragment
        fill_rect(d, 8, 5, 3, 6, (180, 180, 180, 200))
    elif name == "ice_nucleation":
        # Ice nucleation: ice crystal
        # hexagon
        pts = []
        for i in range(6):
            rad_p = math.radians(60 * i - 30)
            pts.append((int(8 + 6 * math.cos(rad_p)), int(8 + 6 * math.sin(rad_p))))
        d.polygon(pts, fill=(r, g, b, 255))
        # center
        draw_circle(d, 8, 8, 3, (200, 230, 255, 200))

    # Tier indicator: small corner dot or glow
    tier_colors = {
        1: (180, 180, 180),  # no glow
        2: (100, 180, 255),  # blue glow
        3: (200, 140, 255),  # purple glow
        4: (255, 200, 50),   # gold glow
    }
    glow = tier_colors[tier]
    if tier >= 2:
        draw_circle(d, 13, 13, 2, (*glow, 220))
    if tier >= 3:
        draw_circle(d, 2, 13, 2, (*glow, 180))
    if tier >= 4:
        draw_circle(d, 13, 2, 2, (*glow, 220))
        draw_circle(d, 2, 2, 2, (*glow, 180))

    filename = f"{OUT}/protein_{name}_tier{tier}.png"
    img.save(filename)

for name, color in PROTEINS.items():
    for tier in range(1, 5):
        make_protein_texture(color, tier, name)

print("✅ 32 protein textures done.")

# ══════════════════════════════════════════════════════════
# 3. DNA PART TEXTURES — regulatory parts by category + rarity
# ══════════════════════════════════════════════════════════

RARITY_COLORS = {
    "":       (120, 180, 120),  # green (default)
    "_blue":  (80, 140, 220),   # blue
    "_purple":(180, 100, 220),  # purple
    "_gold":  (240, 200, 50),   # gold
}

def make_part_texture(category, rarity_suffix, base_color):
    img = new_icon(); d = ImageDraw.Draw(img)
    r, g, b = base_color

    if category == "promoter":
        # Arrow pointing right (promoter = transcription start)
        pts = [(2, 8), (8, 4), (8, 6), (14, 6), (14, 10), (8, 10), (8, 12)]
        d.polygon(pts, fill=(r, g, b, 255))
        # TATA box
        fill_rect(d, 4, 7, 3, 2, (max(0, r-40), max(0, g-40), max(0, b-40), 255))
    elif category == "enhancer":
        # Star burst (enhancer = boosts expression)
        draw_star(d, 8, 8, 6, (r, g, b, 255))
        draw_circle(d, 8, 8, 2, (min(255, r+60), min(255, g+60), min(255, b+40), 255))
    elif category == "sineb2":
        # S-shape (SINE element)
        fill_rect(d, 4, 4, 8, 8, (r, g, b, 255))
        fill_rect(d, 5, 5, 6, 6, (max(0, r-30), max(0, g-30), max(0, b-30), 255))
        d.text((5, 5), "S", fill=(40, 40, 40, 255))
    else:
        # generic segment
        fill_rect(d, 3, 2, 10, 12, (r, g, b, 255))
        fill_rect(d, 3, 2, 10, 3, (min(255, r+40), min(255, g+40), min(255, b+40), 255))
        fill_rect(d, 3, 11, 10, 3, (max(0, r-30), max(0, g-30), max(0, b-30), 255))

    filename = f"{OUT}/dna_part_{category}{rarity_suffix}.png"
    img.save(filename)

for suffix, color in RARITY_COLORS.items():
    for cat in ["promoter", "enhancer", "sineb2"]:
        make_part_texture(cat, suffix, color)

print("✅ 12 DNA regulatory part textures done.")

# ══════════════════════════════════════════════════════════
# 4. DNA CDS TEXTURES — 8 CDS types
# ══════════════════════════════════════════════════════════

CDS_COLORS = {
    "gfp":              (76, 210, 80),
    "cds_keratin":      (200, 170, 120),
    "cds_myosin":       (220, 60, 50),
    "cds_telomerase":   (130, 80, 220),
    "cds_spider_silk":  (230, 230, 230),
    "cds_luciferase":   (240, 220, 50),
    "cds_petase":       (50, 180, 160),
    "cds_ice_nucleation":(140, 200, 240),
}

for cds, color in CDS_COLORS.items():
    img = new_icon(); d = ImageDraw.Draw(img)
    r, g, b = color
    # CDS = coding region, represented as a gene segment with ATG...TGA
    fill_rect(d, 3, 2, 10, 12, (r, g, b, 255))
    # Start codon (ATG)
    fill_rect(d, 3, 2, 10, 2, (min(255, r+50), min(255, g+50), min(255, b+50), 255))
    # Stop codon
    fill_rect(d, 3, 12, 10, 2, (max(0, r-40), max(0, g-40), max(0, b-40), 255))
    # CDS label
    d.text((4, 5), "CDS", fill=(40, 40, 40, 200))
    d.text((3, 9), "GEN", fill=(40, 40, 40, 180))

    img.save(f"{OUT}/dna_part_{cds}.png")

print("✅ 8 CDS textures done.")

# ══════════════════════════════════════════════════════════
# 5. OTHER ITEMS
# ══════════════════════════════════════════════════════════

# waste_culture — hazardous bio-waste
img = new_icon(); d = ImageDraw.Draw(img)
fill_rect(d, 4, 3, 8, 10, (120, 100, 80, 255))  # brown sludge
fill_rect(d, 5, 4, 6, 8, (140, 120, 60, 180))   # inner
# biohazard symbol hint
draw_circle(d, 8, 8, 2, (255, 200, 50, 200))
d.point((8, 5), (255, 50, 50, 255))
d.point((5, 10), (255, 50, 50, 255))
d.point((11, 10), (255, 50, 50, 255))
img.save(f"{OUT}/waste_culture.png")

# blank_plasmid — circular plasmid
img = new_icon(); d = ImageDraw.Draw(img)
draw_circle(d, 8, 8, 6, (200, 200, 220, 255))
draw_circle(d, 8, 8, 4, (160, 160, 180, 255))
# MCS (multiple cloning site)
fill_rect(d, 6, 6, 4, 2, (100, 160, 100, 200))
fill_rect(d, 7, 8, 2, 3, (100, 160, 100, 200))
img.save(f"{OUT}/blank_plasmid.png")

# engineered_plasmid — plasmid with insert (glowing)
img = new_icon(); d = ImageDraw.Draw(img)
draw_circle(d, 8, 8, 6, (200, 200, 220, 255))
draw_circle(d, 8, 8, 4, (160, 160, 180, 255))
# inserted gene (colored segment)
fill_rect(d, 5, 5, 6, 6, (80, 220, 120, 200))
draw_circle(d, 8, 8, 2, (100, 255, 140, 220))
img.save(f"{OUT}/engineered_plasmid.png")

# e_coli_wild — normal E. coli
img = new_icon(); d = ImageDraw.Draw(img)
draw_circle(d, 8, 8, 6, (220, 200, 180, 255))
draw_circle(d, 8, 8, 4, (200, 180, 160, 255))
img.save(f"{OUT}/e_coli_wild.png")

# e_coli_engineered — E. coli with plasmid glow
img = new_icon(); d = ImageDraw.Draw(img)
draw_circle(d, 8, 8, 6, (220, 200, 180, 255))
draw_circle(d, 8, 8, 4, (200, 180, 160, 255))
draw_circle(d, 8, 8, 2, (80, 255, 120, 220))  # GFP glow
img.save(f"{OUT}/e_coli_engineered.png")

# gfp_extract — green liquid in vial
img = new_icon(); d = ImageDraw.Draw(img)
fill_rect(d, 5, 3, 6, 10, (200, 200, 220, 100))  # glass
fill_rect(d, 6, 5, 4, 7, (80, 240, 100, 220))    # green liquid
fill_rect(d, 5, 3, 6, 2, (180, 180, 200, 200))   # cap
img.save(f"{OUT}/gfp_extract.png")

# nutrient_broth variants
BROTH_COLORS = {
    "":              (180, 160, 80),
    "_blue":         (80, 140, 200),
    "_purple":       (160, 80, 200),
    "_gold":         (240, 200, 50),
}
for suffix, color in BROTH_COLORS.items():
    img = new_icon(); d = ImageDraw.Draw(img)
    r, g, b = color
    fill_rect(d, 4, 3, 8, 10, (200, 200, 200, 80))  # glass/beaker
    fill_rect(d, 5, 5, 6, 7, (r, g, b, 220))         # liquid
    fill_rect(d, 5, 5, 6, 2, (min(255, r+40), min(255, g+40), min(255, b+40), 200))
    img.save(f"{OUT}/nutrient_broth{suffix}.png")

# calcium_chloride already done above

# unknown_biological_tissue
img = new_icon(); d = ImageDraw.Draw(img)
# amorphous blob
for _ in range(20):
    import random
    px = random.randint(4, 11)
    py = random.randint(3, 12)
    draw_circle(d, px, py, random.randint(1, 2), (180, 140, 200, random.randint(100, 200)))
# question mark
d.text((5, 6), "?", fill=(255, 255, 255, 200))
img.save(f"{OUT}/unknown_biological_tissue.png")

# incubator block
img = new_icon(); d = ImageDraw.Draw(img)
fill_rect(d, 2, 2, 12, 12, (120, 120, 140, 255))  # metal
fill_rect(d, 3, 3, 10, 6, (200, 180, 80, 200))     # warm interior
fill_rect(d, 3, 3, 10, 2, (220, 200, 100, 255))    # top glow
fill_rect(d, 4, 5, 3, 2, (80, 200, 100, 180))       # sample vial
img.save(f"{OUT}/incubator.png")

# plasmid_assembler block
img = new_icon(); d = ImageDraw.Draw(img)
fill_rect(d, 2, 2, 12, 12, (100, 100, 120, 255))  # dark chassis
draw_circle(d, 8, 6, 4, (200, 200, 220, 255))      # plasmid icon
draw_circle(d, 8, 6, 2, (140, 180, 140, 220))      # insert
fill_rect(d, 7, 9, 2, 4, (180, 160, 80, 255))      # arm/robot
img.save(f"{OUT}/plasmid_assembler.png")

print("✅ All additional item textures done.")
print(f"📁 All textures saved to: {OUT}/")

#!/bin/bash
# Verify recipe files are loaded by checking the JAR
JAR="/Users/weiziheng/Documents/igem_minecraft_mod/igem-mod-template-26.1.2/build/libs/synbio-1.0.0.jar"

echo "=== Recipe files in JAR ==="
unzip -l "$JAR" 2>/dev/null | grep "data/synbio/recipe/" | awk '{print "  " $4 " (" $1 " bytes)"}'

echo ""
echo "=== nutrient_broth_green.json content ==="
unzip -p "$JAR" data/synbio/recipe/nutrient_broth_green.json 2>/dev/null | python3 -m json.tool

echo ""
echo "=== e_coli_competent.json content ==="
unzip -p "$JAR" data/synbio/recipe/e_coli_competent.json 2>/dev/null | python3 -m json.tool

echo ""
echo "=== In-game test command ==="
echo "Run this in Minecraft chat to check if recipe is registered:"
echo "  /recipe give @s synbio:nutrient_broth"
echo "  (If this works, recipes are loaded — the issue is something else)"

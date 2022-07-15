#!/bin/bash

MOD_ID='fantasyfurniture'
MODELS_PATH="assets/${MOD_ID}/models/block"

SOLID='solid'
CUTOUT_MIPPED='cutout_mipped'
CUTOUT='cutout'
TRANSLUCENT='translucent'
TRANSLUCENT_MOVING_BLOCK='translucent_moving_block'
TRANSLUCENT_NO_CRUMBLING='translucent_no_crumbling'
LEASH='leash'
WATER_MASK='water_mask'
ARMOR_GLINT='armor_glint'
ARMOR_ENTITY_GLINT='armor_entity_glint'
GLINT_TRANSLUCENT='glint_translucent'
GLINT='glint'
GLINT_DIRECT='glint_direct'
ENTITY_GLINT='entity_glint'
ENTITY_GLINT_DIRECT='entity_glint_direct'
LIGHTNING='lightning'
TRIPWIRE='tripwire'
END_PORTAL='end_portal'
END_GATEWAY='end_gateway'
LINES='lines'
LINE_STRIP='line_strip'

declare -A RENDER_TYPES=(
)

INPUT_DIRS="src/generated/resources/${MODELS_PATH} src/main/resources/${MODELS_PATH} test/main/resources/${MODELS_PATH}"

for dir in $(echo "$INPUT_DIRS"); do
    if [[ -d "$dir" ]]; then
        for file in $(find "${dir}" -name *json); do
            fileName="${file%.*}" 
            fileName=$(echo $fileName | rev | cut -d/ -f1 | rev)
            if [[ ${RENDER_TYPES[$fileName]+true} ]]; then
                
                found=$(jq '.render_type' $file)
                if [[ "$found" == "null" ]]; then
                    echo "Processing Model: '${file}'..."
                    renderType=${RENDER_TYPES[$fileName]}
                    echo "$(cat $file | jq -r --arg renderType "$renderType" '.render_type = $renderType')" > $file
                else
                    echo "Skipping: '${file}'..."
                fi
            fi
        done
    fi
done
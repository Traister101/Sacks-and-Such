from typing import List, Sequence

from mcresources import ResourceManager, utils, RecipeContext
from mcresources.type_definitions import Json, ResourceIdentifier


def generate(rm: ResourceManager):
    rm.crafting_shaped("crafting/reinforced_fiber", ["JJJ", "SSS", "JJJ"],
                       {"J": "tfc:jute_fiber", "S": "#forge:string"},
                       "sns:reinforced_fiber").with_advancement("tfc:jute_fiber")

    rm.crafting_shaped("crafting/pack_frame", ["RRR", "R R", "RRR"],
                       {"R": "#forge:rods/steel"},
                       "sns:pack_frame").with_advancement("#forge:ingots/steel")

    # Sack recipes
    damage_shaped(rm, "crafting/thatch_basket", ["JJJ", "T T", " TK"],
                  {"J": "tfc:jute_fiber", "T": "tfc:thatch", "K": "#tfc:knives"},
                  "sns:thatch_basket").with_advancement("tfc:jute_fiber")

    damage_shaped(rm, "crafting/leather_sack", ["JJJ", "LUL", " LN"],
                  {"J": "tfc:jute_fiber", "U": "sns:unfinished_leather_sack", "L": "#forge:leather",
                   "N": "#tfc:sewing_needles"},
                  "sns:leather_sack").with_advancement("sns:unfinished_leather_sack")

    damage_shaped(rm, "crafting/burlap_sack", ["JJJ", "B B", " BN"],
                  {"J": "tfc:jute_fiber", "B": "tfc:burlap_cloth", "N": "#tfc:sewing_needles"},
                  "sns:burlap_sack").with_advancement("tfc:burlap_cloth")

    damage_shaped(rm, "crafting/seed_pouch", ["SSS", "WUW", "  N"],
                  {"S": "#forge:string", "W": "tfc:wool_cloth", "U": "sns:burlap_sack",
                   "N": "#tfc:sewing_needles"},
                  "sns:seed_pouch").with_advancement("sns:burlap_sack")

    damage_shaped(rm, "crafting/ore_sack", ["RRR", "LUL", " LN"],
                  {"R": "sns:reinforced_fiber", "L": "#forge:leather", "U": "sns:burlap_sack",
                   "N": "#tfc:sewing_needles"},
                  "sns:ore_sack").with_advancement("sns:unfinished_leather_sack")

    damage_shaped(rm, "crafting/frame_pack", ["RFR", "LLL", " FN"],
                  {"R": "sns:reinforced_fiber", "F": "sns:pack_frame", "L": "sns:unfinished_leather_sack", "N": "#tfc:sewing_needles"},
                  "sns:frame_pack").with_advancement("sns:pack_frame")

    # Leather knapping recipes
    leather_knapping(rm, "knapping/leather/unfinished_leather_sack", [" XXX ", "XXXXX", "XXXXX", "XXXXX", " XXX "],
                     "sns:unfinished_leather_sack")

    # Loom recipes
    loom_recipe(rm, "loom/reinforced_fabric", "16 sns:reinforced_fiber", "sns:reinforced_fabric", 16,
                "sns:loom/reinforced_fabric")


def leather_knapping(rm: ResourceManager, name_parts: ResourceIdentifier, pattern: List[str], result: Json,
                     outside_slot_required: bool = None):
    knapping_recipe(rm, name_parts, "tfc:leather", pattern, result, None, outside_slot_required)


def knapping_recipe(rm: ResourceManager, name_parts: ResourceIdentifier, knap_type: str, pattern: List[str],
                    result: Json, ingredient: Json, outside_slot_required: bool):
    for part in pattern:
        assert 0 < len(part) < 6, "Incorrect length: %s" % part
    rm.recipe((knap_type.split(":")[1] + "_knapping", name_parts), "tfc:knapping", {
        "knapping_type": knap_type,
        "outside_slot_required": outside_slot_required,
        "pattern": pattern,
        "ingredient": None if ingredient is None else utils.ingredient(ingredient),
        "result": utils.item_stack(result)
    })


def loom_recipe(rm: ResourceManager, name: utils.ResourceIdentifier, ingredient: Json, result: Json, steps: int,
                in_progress_texture: str):
    return rm.recipe(("loom", name), "tfc:loom", {
        "ingredient": item_stack_ingredient(ingredient),
        "result": utils.item_stack(result),
        "steps_required": steps,
        "in_progress_texture": in_progress_texture
    })


def item_stack_ingredient(data_in: Json):
    if isinstance(data_in, dict):
        if "type" in data_in:
            return item_stack_ingredient({"ingredient": data_in})
        return {
            "ingredient": utils.ingredient(data_in["ingredient"]),
            "count": data_in["count"] if data_in.get("count") is not None else None
        }
    if pair := utils.maybe_unordered_pair(data_in, int, object):
        count, item = pair
        return {"ingredient": fluid_ingredient(item), "count": count}
    item, tag, count, _ = utils.parse_item_stack(data_in, False)
    if tag:
        return {"ingredient": {"tag": item}, "count": count}
    else:
        return {"ingredient": {"item": item}, "count": count}


def fluid_ingredient(data_in: Json) -> Json:
    if isinstance(data_in, dict):
        return data_in
    elif isinstance(data_in, List):
        return [*utils.flatten_list([fluid_ingredient(e) for e in data_in])]
    else:
        fluid, tag, amount, _ = utils.parse_item_stack(data_in, False)
        if tag:
            return {"tag": fluid}
        else:
            return fluid


def damage_shapeless(rm: ResourceManager, name_parts: ResourceIdentifier, ingredients: Json, result: Json,
                     group: str = None, conditions: utils.Json = None) -> RecipeContext:
    return delegate_recipe(rm, name_parts, "tfc:damage_inputs_shapeless_crafting", {
        "type": "minecraft:crafting_shapeless",
        "group": group,
        "ingredients": utils.item_stack_list(ingredients),
        "result": utils.item_stack(result),
        "conditions": utils.recipe_condition(conditions)
    })


def damage_shaped(rm: ResourceManager, name_parts: ResourceIdentifier, pattern: Sequence[str], ingredients: Json,
                  result: Json, group: str = None, conditions: Json | None = None) -> RecipeContext:
    return delegate_recipe(rm, name_parts, "tfc:damage_inputs_shaped_crafting", {
        "type": "minecraft:crafting_shaped",
        "group": group,
        "pattern": pattern,
        "key": utils.item_stack_dict(ingredients, "".join(pattern)[0]),
        "result": utils.item_stack(result),
        "conditions": utils.recipe_condition(conditions)
    })


def delegate_recipe(rm: ResourceManager, name_parts: ResourceIdentifier, recipe_type: str, delegate: Json,
                    data: Json = {}) -> RecipeContext:
    return write_crafting_recipe(rm, name_parts, {
        "type": recipe_type,
        **data,
        "recipe": delegate,
    })


def write_crafting_recipe(rm: ResourceManager, name_parts: ResourceIdentifier, data: Json) -> RecipeContext:
    res = utils.resource_location(rm.domain, name_parts)
    rm.write((*rm.resource_dir, "data", res.domain, "recipes", res.path), data)
    return RecipeContext(rm, res)

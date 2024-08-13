from enum import Enum, auto

from mcresources import ResourceManager, utils
from mcresources.type_definitions import ResourceIdentifier

import constants


class Size(Enum):
    tiny = auto()
    very_small = auto()
    small = auto()
    normal = auto()
    large = auto()
    very_large = auto()
    huge = auto()


class Weight(Enum):
    very_light = auto()
    light = auto()
    medium = auto()
    heavy = auto()
    very_heavy = auto()


def generate(rm: ResourceManager):
    item_size(rm, "unfinished_leather_sack", "sns:unfinished_leather_sack", Size.small, Weight.medium)
    item_size(rm, "reinforced_fiber", "sns:reinforced_fiber", Size.small, Weight.very_light)
    item_size(rm, "reinforced_fabric", "sns:reinforced_fabric", Size.small, Weight.very_light)
    item_size(rm, "pack_frame", "sns:pack_frame", Size.large, Weight.medium)

    rm.item_tag("curios:belt", *constants.SACKS)

    rm.item_tag("curios:back", *constants.PACKS)

    # rm.data("curios/slots/belt", {})
    rm.data("curios/entities/belt", {
        "entities": ["minecraft:player"],
        "slots": ["belt"]
    })


def item_size(rm: ResourceManager, name_parts: ResourceIdentifier, ingredient: utils.Json, size: Size,
              weight: Weight):
    rm.data(("tfc", "item_sizes", name_parts), {
        "ingredient": utils.ingredient(ingredient),
        "size": size.name,
        "weight": weight.name
    })

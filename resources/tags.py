from mcresources import ResourceManager

import constants


def generate(rm: ResourceManager):
    rm.item_tag("prevented_in_sacks", *constants.CONTAINERS)
    rm.item_tag("allowed_in_seed_pouch", "#tfc:seeds")

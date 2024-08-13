from mcresources import ResourceManager

import constants
import util


def generate(rm: ResourceManager) -> None:
    rm.item("unfinished_leather_sack").with_lang("Unfinished Leather Sack").with_item_model()
    rm.item("reinforced_fiber").with_lang("Reinforced Fiber").with_item_model()
    rm.item("reinforced_fabric").with_lang("Reinforced Fabric").with_item_model()
    rm.item("pack_frame").with_lang("Pack Frame").with_item_model()

    # Sacks
    for sack in constants.SACKS:
        rm.item(sack).with_lang(util.langify(sack)).with_item_model()

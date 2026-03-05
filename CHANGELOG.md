### New

- Chinese translations by LLLCYL
- Russian Translations by Petr211071
- Ukrainian Translations by Starman

### Fixed

- Void slots not properly syncing to the client. In particular the GUI was very confusing as it just didn't work at all
- Plant slowdown even with Boots Equipped when jumping (was caused by code that handles block effects through small
  blocks like carpet)
- Crossbows not pulling ammo from the Quiver
- The Lunchbox Preserved Trait persisting in moved stacks in certain contexts (fixed by my Extended Slot Capacity lib as
  the issue is more fundamental to menu behavior)

### Changed

- New tag `sns:boots_prevent_slowdown` used by boots for which blocks should be considered for slowdown prevention
### New

- Japanese translations by PExPE3
- Steps per damage is per Boot/Horseshoe. Old "global" config has been removed. Defaults to 500 - 1,500 steps per damage
  depending on tier (as good as previous default or better)

### Fixed

- Quivers duplicating arrows
- Lacking curios causing a mixin crash on startup (mixin class loads all referenced classes which makes sense but was
  unexpected)

### Changed

- Frame Pack slot cap config being ignored (was intentional but not worth the needed refactor to avoid confusion about
  it)
# Etheris development progress

## Current milestone

`M1 — vertical slice foundation`

The branch proves the first playable resource loop described by the GDD: obtain Etherium, charge a personal focus, and cast a meaningful spell. The implementation is deliberately small enough to review and extend without locking in the full endgame architecture.

### Implemented

- NeoForge project for Minecraft 1.21.1 and Java 21.
- Mod metadata, creative tab, Portuguese and English localization.
- Persistent interior Ether value from 0 to 100 with slow regeneration.
- Etherium Fragment item and Aether Focus item.
- Astral Spark cast: costs 8 Ether, has cooldown, particles and nearby damage.
- Ether Collector interaction: consumes one fragment and restores 15 Ether.
- Four registered foundation blocks with recipes/assets using vanilla placeholder textures.

### Not yet complete

- Client HUD and Codex discovery UI.
- Environmental Ether and condensed Ether as separate networked resources.
- Real machine inventories, processing queues and explicit network links.
- First ruin/world feature and discovery-based progression.
- The remaining seven vertical-slice spells.
- Tests in a real game client/server and generated data validation.
- Final textures, models, animations and sound design.

## Milestones

1. **M1 — Foundation:** personal Ether, focus, one spell, four blocks. In progress on this branch.
2. **M2 — Discovery:** Codex data, first ruin, five disciplines, eight spells, loop target 60–90 minutes.
3. **M3 — Processing:** recipes and real queues for the four machines; first stable condensed-Ether buffer.
4. **M4 — Network:** explicit nodes/channels, stability and diagnostics; one multiblock.
5. **M5 — MVP:** progression tiers I–III, 20 spells, 10 machines, two lacaio families, first armor line.
6. **M6 — Endgame:** tiers IV–VI, seven megamachines, Regalia and non-boss-rush proof challenges.

## Verification notes

The source and resource tree have been reviewed locally. A full Gradle build still depends on fetching the Gradle wrapper and NeoForge artifacts in the execution environment; its result will be recorded in the pull request once available.

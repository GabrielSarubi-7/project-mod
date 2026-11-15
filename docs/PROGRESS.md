# Etheris development progress

## Checkpoint 0.3.0-alpha — 2026-11-15

Supersedes the 0.2 functionality matrix where noted. Seven machines are now implemented: the original four plus Prismatic Crusher, Crystal Refinery and Distribution Node. Automated processing has persistent input/output slots, a NeoForge item capability, hopper integration, bounded scheduled work, and atomic energy/reagent/result exchange. Unpowered or blocked machines preserve their input. Removal drops inventory; energy is intentionally discarded.

The node stores four explicit same-dimension links and alternates the first consumer each cycle with a total budget of 8 Ether. Repeating an existing node link removes it. No item routing, forced chunk loading, ownership or configurable priorities yet.

Progression adds six native advancements and a refined-crystal requirement for the last three spells. Eight spells remain total. The Codex has 12 pages; the personal focus shows status periodically. Seven authored block models use vanilla materials; visual client QA and animated art remain outstanding.

Recipe values are provisional balance choices. Sintonization here is an initial material/learning gate, not completion of the GDD's full tier II. Gênese, minions, armor, remaining machines, advanced tiers and all megamachines remain unimplemented. Do not equate content counts with completed GDD acceptance.

Regression suite adds real hopper transport, blocked-output atomicity, inventory save/load, capability simulation/rejection, node distribution and crystal-gated learning. Prior 0.2 CI succeeded on GitHub. Final 0.3 validation: `build runGameTestServer --no-daemon --offline` passed on Java 21.0.13 / NeoForge 21.1.172; the log reports **All 12 required tests passed**. These are actual headless Minecraft tests, including physical hoppers; no visual client or two-player session has been validated.

## Checkpoint 0.2.0-alpha — 2026-11-15

This section supersedes the historical 0.1 notes below. Source now implements eight spells across Astra/Vita/Materia/Fluxo/Fulgor, persistent player learning/selection, four stateful machines, manual reagent exchanges, a native written-book Codex, a bounded directed energy link and an Overworld ruin feature with loot. All visuals reuse vanilla textures explicitly as development placeholders.

The original 0.1 compilation did not establish playability: it lacked BlockItems and used obsolete plural recipe folders. Both are corrected. Its area spell also hit passive entities; the replacement uses a line-of-sight ray and hostile targets only.

### Remaining GDD acceptance work

Verification: `gradlew.bat build runGameTestServer --no-daemon --offline` passed on Java 21.0.13 with NeoForge 21.1.172. The server loaded 1298 recipes and reported **All 7 required tests passed**: storage bounds/persistence, collection under sky, transfer conservation, powered/unpowered forge exchange, healing/no-op/cooldown, Codex book conversion, and linking-wand interaction routing. These are headless tests, not a full player playthrough. No custom recipe-loading errors appeared. Existing vanilla command/schema warnings and a test-world 0.1→0.2 version warning remain.

| Area | Implemented now | Still required |
| --- | --- | --- |
| Vertical slice | 8 spells, 5 schools, 4 machines, ruin, guide | Client playthrough, discovery proofs, 60–90-minute balancing |
| Magic | Costs, cooldowns, basic learning | Mastery, hand casting, hybrids, 22 further spells, 5 further schools |
| Ether | Personal pool, local collection, condensed items, storage, links | Regional concentration, depletion, overload, instability, priorities |
| Machines | 4 manual devices | Queues, GUIs, inventories, automation, 6 other machines, multiblock |
| Progression | Early learning sequence | Tier gates II–VI, nonrepetitive discoveries and preparation |
| Life | None | 5 families, AI, contracts, modules, ownership, persistence |
| Equipment | Personal focus | All 5 armor lines, transformations and maintenance |
| Endgame | None | All 7 megamachines, activation proofs, Ascension and rewards |
| Art | Vanilla placeholders, spell particles | Original textures, editable Blockbench sources, animation, sound and VFX settings |
| Quality | Headless GameTests | Visual client QA, 2-player persistence, crash recovery, profiling and balancing |

Machine data uses NBT and scheduled updates. Links are public and same-dimension, maximum 8 blocks, 8 Ether per 100 ticks. No chunk forcing. Breaking a machine deliberately discards its stored energy for now. The ruin is a feature rather than a registered locate-able structure; natural frequency has not been sampled.

Spell behaviors not specified precisely by the GDD are provisional design decisions: Liga de Éter refines raw iron; Semente Quimérica advances one crop stage; Impulso grants temporary movement effects. These names do not imply their final art or full late-game behavior.

Technical references: [block entities](https://docs.neoforged.net/docs/1.21.1/blockentities/) and [GameTests](https://docs.neoforged.net/docs/1.21.1/misc/gametest/). Runtime APIs also verified against the compiled NeoForge 21.1.172 environment.

Next priority: validate the complete survival loop in a client, add discovery-based progression and automated machine queues, then expand tiers II–III. Do not call the GDD complete based on registration counts.

## Historical 0.1 checkpoint (superseded)

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

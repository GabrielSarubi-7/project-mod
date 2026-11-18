# Etheris development progress

## Checkpoint 0.7.0-preview.5 — 2026-11-18

User requested a less blocky, more detailed Vazio. Replaced the broad cuboid surfaces with authored polygons: an octagonal helmet shell, curved tapered crown blades and faceted floating crystals, bevelled breastplate/pauldron/gauntlet/greave profiles, continuous torus eclipse rings, fine edge channels and radial markings. Cape/stole/side cloth now has folded triangular surfaces and a continuous violet gradient; the hem is irregular rather than coarse rectangular steps. Existing 39-bone layout, four items, command, balance and cloth dynamics remain; Astra is untouched.

The game now reads `armor_mesh/vazio.mesh.json` (197 pieces / 6,297 faces / 8,818 triangles) through `ArmorMeshLoader`. It produces GeoQuad surfaces on the existing Gecko bones, with X reflection, reversed winding, normalized UVs and outward normals. The skeleton `.geo.json` deliberately has no cubes; surfaces attach once per cached skeleton and reload with it. This preserves Gecko slot visibility, motion and the existing emissive pass. Surface loading resolves all bones before mutating the cache and is idempotent. The editable Blockbench source is now Generic Model with welded mesh positions, not a Bedrock cube approximation. Manual mesh edits require updating the surface resource; default Bedrock export alone is insufficient.

Validation: `build runGameTestServer --no-daemon --offline` passed on Java 21 / NeoForge 21.1.172 / GeckoLib 4.9.3. All **30 required GameTests** passed. New `checkArmorMeshes` exercises the actual runtime baker over all shipped faces: winding/normals/UVs, eight slot roots, repeat attachment, invalid-bone atomicity and a fresh skeleton reload. Existing cloth stability checks passed. The asset validator checks hierarchy, mesh editing source, face/triangle budget, atlas, animation names, localizations and emission. Front/back offline renders of the actual polygon resources were inspected; a coplanar boot seam was corrected before final build. This is not an in-game visual approval or a performance benchmark with multiple players.

GeckoLib 4.9.3's bundled source was used to verify GeoCube/GeoQuad/GeoVertex and GeoModel lifecycle. Official background: [Geo Models](https://github.com/bernie-g/geckolib/wiki/Geo-Models-(Geckolib4)); Blockbench's [Generic Model format](https://github.com/JannisX11/blockbench/blob/master/js/formats/generic.ts) supports polygon meshes and animation. No new runtime dependency is required. The GDD, spell visual revisions and observatory rehearsal remain unfinished.

## Checkpoint 0.7.0-preview.4 — 2026-11-16

After approving Astra's motion, the user supplied a distinct Vazio reference and requested equivalent quality. Added four separate Vazio ArmorItems, own material/registration/localization/icons, `/etheris vazio` and a GeckoLib renderer/model/emissive layer. Astra's source, geometry, texture and movement tuning are unchanged.

Vazio uses 314 cuboids and 39 bones with eight equipment roots. Identity: angular pale-violet metal over obsidian, a featureless black face with a luminous four-point slit, a fractured crown with three floating shards, a black eclipse disk with rotating broken inner ring, angular pauldrons and a torn violet mantle. Twelve cape segments, three-part side cloth and front stole reuse the tested secondary-motion solver. Cape amplitude is 88% of Astra's; detached metal bracer fins use 35%, while cloth keeps its bend/settle behavior. Only the appropriate atlas tiles emit; the eclipse center remains opaque black. A 12-second authored loop handles crown levitation, iris rotation and core/face breathing.

Validation: Java 21 build and **all 30 required GameTests passed** with GeckoLib loaded on the dedicated test server. The new test checks Vazio item/damage serialization, distinct slots, full-set defense, crystal repair and mixed Astra/Vazio equipment. The existing cloth regression suite passed, including 30/60/144 FPS and 2,400 stress frames. Asset validation checks bone hierarchy, animated targets, thin cape geometry, atlas bounds/parity, localizations and exact emissive material selection. Final geometry corrections were followed by asset validation and a successful build; server logic did not change after its test run.

The user stopped Computer Use with physical Esc while opening Blockbench, so all subsequent work used source files and offline validation. `tools/preview_geo.mjs` renders the actual generated geometry without driving apps; front/back inspection caught and corrected coplanar toe caps and cloth seams. Its image approximates lighting and is **not a game screenshot**. In-game animation, equipment visibility, shader/elytra/cosmetic-cape interaction and multiplayer visual behavior remain unverified for Vazio. No claim that the client was opened or visually approved.

Delivery: preview.4 JAR, Vazio Blockbench package, guide and offline geometry preview. No survival recipes, school bonuses, new spell mechanics or GDD completion. Current spell/observatory work remains pending. Commit/PR checkpoint only, no merge.

## Checkpoint 0.7.0-preview.3 — 2026-11-16

Current focus is the user's Astra reference and softer movement, rather than expanding systems. Astra is a separate four-piece GeckoLib armor set: ivory hood, faceless porcelain mask, orbit/meridian halos, hanging gold-framed violet talismans, chest astrolabe, lunar stoles and a new violet cape with ivory/gold edging. `/etheris astra` supplies the set without replacing equipped items. No survival recipe or school bonus is claimed.

GeckoLib NeoForge 1.21.1 4.9.3 is a required external dependency on client and server, resolved from its official Maven repository. Geometry, atlas, glow mask, item icons and an editable Blockbench source are generated by `tools/astra_armor.mjs`. Current model: 277 cuboids, 40 bones, eight isolated equipment roots. The cape has three neighboring columns of four hinges; side robes and stoles each have three thin segments. Sleeve tips and talismans have separate pivots. The animated halo and chest emblem retain their 12-second loop and emissive layer.

`AstraClothMotion` drives cosmetic joints from interpolated displacement, body/head rotation and walking phase. It uses damped springs with <=1/120-second substeps, independent weakly held state per wearer, bounded angles, wrapped yaw and reset after teleport/long absence/clock rewind. Segment orientations become local GeckoLib rotations, so the lower cloth bends and trails instead of swinging as one plate. No player movement statistics or vanilla animation replacements are introduced. This is joint-based secondary motion, not a cloth mesh collision solver; crouching, slim skins, swimming, elytra/cosmetic cape overlap and shader packs still need client review.

Validation: Java 21 `build runGameTestServer --no-daemon --offline` succeeded, with **All 29 required tests passed**. Gradle check also runs a dependency-free dynamics regression: running lift/drape, settling, repeated render/pause, teleport and clock reset, yaw wrapping, wearer isolation and 2,400 extreme-motion frames. Maximum measured angular deviation between 30/60/144 FPS was 0.00307 radians. Asset checks passed for Astra (slot hierarchy, cloth seams, animation targets, UVs, embedded atlas) and the prior 11 Firmament models. Server tests do not evaluate this armor's visual quality. At the user's request, interactive gameplay testing is left to them; do not label it visually approved.

The preceding art checkpoint also includes eight original machine models, three handheld instruments and an original six-bone Vigia. Sources are editable in Blockbench; machines remain static with full-block collision. The three spell revisions, observatory rehearsal, Firmament accessibility/localization and full GDD remain unfinished. Preview.2 was an intermediate local Astra build; preview.3 is the packaged animated-cape revision.

## Checkpoint 0.7.0-preview.1 — 2026-11-16

Current priority overrides the historical expansion plans below: build the visual slice before adding more systems. First Firmament menu and first Eclipse Veil armor are implemented; the three spell revisions and observatory rehearsal remain pending.

The armor uses four registered ArmorItems, its own material, client-only HumanoidModel geometry, a 256×32 palette atlas and original item icons. Crown, dark visor, lunar eyes, asymmetric pauldrons, chest inlay and three rigid mantle panels establish the silhouette. Pose/slot visibility is inherited through NeoForge's armor model hooks. The set has 20 total defense, durability multiplier 30, toughness 2 per piece and stable-crystal repair. These are preview balance values, not a completed GDD equipment progression. No survival recipes, emissive layer, cloth simulation or editable Blockbench source are included. Texture/icon authoring source is `tools/eclipse_palette.ps1`; geometry source is `EclipseArmorModel.java`.

Operator command `/etheris atelier` supplies the four pieces and a focus through inventory insertion, dropping overflow rather than replacing existing equipment. It was exercised in the development client; all pieces were equipped in previously empty armor slots, and front/back and inventory rendering were visually inspected. Native Minecraft screenshots were saved for the delivery. No client asset-loading errors appeared. Movement, crouching, slim-arm skins, shader compatibility and human multiplayer remain unverified.

Validation: `build runGameTestServer --no-daemon --offline` passed with **All 29 required tests passed** on Java 21.0.13 / NeoForge 21.1.172. The added armor test checks item/damage serialization, equipment assignment, total material defense and repair ingredient. This does not prove combat balance or animation quality. Version metadata was subsequently updated to 0.7.0-preview.1 and the distributable rebuilt.

Next: reimagine Astral Spark, Vital Touch and Flux Impulse with anticipation, a readable release and controlled decay, then integrate a short optional test scenario. Firmament keyboard support, small-screen layout and full localization are still pending. The full GDD and requested visual slice are not complete.

## Checkpoint 0.6.0-alpha — 2026-11-15

First minion family: Ether Sentinel/Vigia. A crafted contract requires the Chamber's completed-operation advancement, a solid floor with unoccupied dry space, and 60 inner Ether in survival. Successful creation consumes one contract; creative bypasses proof/resources but not capacity. The world-wide SavedData ledger reserves one entity UUID per owner across dimensions and unloaded chunks. Entity NBT stores owner, anchor, order and fuel. Death/destruction releases only the matching UUID; unloading does not. Sneak-use a contract in air explicitly releases an abandoned bond without refund, and invalidated entities stop working/discard when loaded.

Owner-only orders are follow, guard (set current anchor), return (walk to anchor then pause), and pause. AI decisions run every ten ticks, maintenance once per second. An active unit requires a living non-spectator owner in the same dimension within 24 blocks and a matching ledger record. Combat targets nearby Monsters within eight blocks of owner/guard anchor and ten blocks of the unit, excluding creepers, allied entities and owned creatures. Attacks require close range and line of sight; four base damage, one-second cadence. No vanilla village-defence or revenge goals are installed. Local pathfinding is preceded by a loaded-chunk envelope check; no recall teleport or explicit chunk forcing is used.

Initial reserve: 300 active seconds, maximum 600. A fragment adds 120 seconds and heals four health only when the full fuel amount fits. Pause/absent owner/empty reserve disables work; following/guarding consumes reserve even while standing still. Thirty max health. A small vanilla iron-golem model/texture is an explicit placeholder. Entity renderer is client-only; dedicated server loading succeeds. No original model, advanced modules, item logistics or full tier-III acceptance is claimed.

Validation: `build runGameTestServer --no-daemon --offline` on Java 21.0.13 / NeoForge 21.1.172 completed; **All 28 required tests passed**. Seven additional tests exercise proof/one-time costs/duplicate rejection, blocked placement and insufficient energy, unload-safe capacity and stale-release protection, owner commands/refueling/NBT roundtrip, forbidden combat targets, actual server combat and paused upkeep, and real follow/return navigation. The survival fixtures use NeoForge FakePlayer because vanilla's server mock hardcodes creative mode; prerequisite progress is seeded directly because NeoForge intentionally suppresses advancement awards for FakePlayer. Actual proof awarding remains covered by the separate Chamber test. This is headless validation, not a human multiplayer session or a full process-restart/crash-recovery test.

The Codex now has sixteen pages and the contract has localized usage/release tooltips. Completing the Chamber proof reveals its crafting recipe for new completions; existing completed advancements do not replay recipe rewards, so the exact pattern is documented in README. The 0.5 GitHub CI run 43757372070 completed successfully. Existing vanilla log/schema and deprecated API warnings remain; no recipe-loading errors were observed (1303 recipes / 1409 advancements).

Remaining: visual client QA for the Vigia, long-session balance, multi-user play, network profiling, first lacaio modules and second family, the Berçário/Oficina, armor, more spells, regional Ether and all megamachines. Counts remain eight spells/eight machines/one multiblock, plus one initial family with four orders. Next concrete milestone: a bounded utility lacaio and first modules, connected to the Gênese production machines. This checkpoint does not complete the GDD.

## Checkpoint 0.5.0-alpha — 2026-11-15

The Transmutation Chamber is the eighth machine and first functional multiblock. Its 3×3×2 assembly has eight polished-deepslate base blocks, four amethyst corners and four cut-copper sides around the controller. The base center is deliberately unrestricted for an output hopper; input can enter from above. All oxidation/wax variants are accepted through the `etheris:chamber_conductors` block tag. This assembly and its two recipes are provisional implementation decisions, not exact specifications supplied by the GDD.

Four stable crystals plus 160 Ether yield one ender pearl; eight quartz plus 80 Ether yield one amethyst shard. Recipes now specify batch input quantities. Structure validation occurs before committing resources, respects loaded chunks, and never scans beyond sixteen required support positions. Broken, paused, underpowered or output-blocked machines preserve input and energy. Queued chambers check at 100-tick intervals so diagonal repairs resume work; idle unlinked chambers have no scheduled polling. An open panel caches structure diagnostics for 20 ticks, but production always revalidates immediately.

The chamber supports the existing menu, redstone controls, explicit energy links and physical hoppers. Two advancements distinguish obtaining a controller from using an operated chamber; a successful cycle records a persisted proof. Players can register that public discovery by opening the operated machine or collecting its output in the menu. Owning natural pearls alone does not award the proof. Stable crystals unlock the controller's crafting recipe in the recipe book. The Codex now has fourteen pages, including the exact assembly.

Validation: Java 21.0.13 / NeoForge 21.1.172 `build runGameTestServer --no-daemon --offline` completed successfully; **All 21 required tests passed**. Six new tests cover every required support block, batch quantities/output stack limits, save/load of proof and products, automatic repair recovery, unloaded chunks, actual advancement award and physical hopper input/output through the assembled machine. Minecraft loaded 1302 recipes and 1409 advancements without resource-loading errors. Test-only mock-player API deprecation and existing vanilla command/schema warnings remain. The first new test-directory launch created its missing server.properties; the final run no longer has that initial warning.

GameTests now run in `run-gametest` while the client stays in `run`. This removes the log-file collision observed when the 0.4 server tests and client were open simultaneously. CI artifact/verification paths were updated accordingly, using the documented [ModDevGradle run directory setting](https://github.com/neoforged/ModDevGradle). The previous 0.4 GitHub CI run 43756148124 completed successfully.

Limits: no visual client QA for the new chamber, survival balance session, two-player session or large-network profiling. The eight spells are unchanged. The chamber is an initial GDD tier-III machine implementation, not completion of Gênese or the higher tiers. Berçário, Oficina, minions, armor, mastery, regional instability and all megamachines remain absent. Recipes remain a Java catalogue; only the structure conductor tag is datapack-extensible. Next implementation priority: discovery progression and a bounded first lacaio family, then the remaining two machines and tier-II equipment. Do not equate one working multiblock with completion of Convergência.

## Checkpoint 0.4.0-alpha — 2026-11-15

Native machine menus now expose energy/capacity, two inventory slots, status diagnostics, manual pause/resume and outgoing-link removal. Empty-hand interaction opens the panel; sneaking preserves quick output retrieval. Shift transfer accepts only recipe inputs, rejects insertion into output, and allows players to retrieve input while hoppers still cannot extract it. Server checks distance, machine identity, interaction permission and spectator controls.

Manual pause persists in NBT. Redstone power or manual pause stops automated generation, processing and outgoing transfer; removing the signal wakes scheduled work. Incoming energy, hoppers and explicit manual exchanges remain available by design. The Codex documents these semantics.

Validation: build and **All 15 required tests passed**, adding menu transfers/input retrieval/remote-action rejection, pause persistence/link removal, and physical redstone stop/resume. The 0.3 GitHub CI also completed successfully. A development client launched successfully and the Rune Forge menu was visually observed at 856×512 with legible labels, slots and buttons. Interactive button QA was interrupted by the Computer Use stop signal; no complete survival or two-player playthrough is claimed. UI actions are covered server-side, but client interaction coverage remains limited.

This checkpoint improves usability and engineering controls; it adds no spells or machines. Counts remain eight spells and seven machines. The full GDD remains incomplete: discovery progression, regional Ether, the first multiblock, remaining machines/spells, minions, armor, higher tiers and megamachines still need implementation and validation. Next milestone: a tested first multiblock and meaningful discovery gates, followed by tiers II–III content. Historical acceptance matrices below describe their own checkpoints only.

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

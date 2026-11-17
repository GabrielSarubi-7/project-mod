# Astra — Oráculo Celestial, preview.3

Minecraft Java 1.21.1 · NeoForge 21.1.172 · Java 21 · GeckoLib NeoForge 4.9.3.

## Jogar

Na instalação normal, coloque `etheris-0.7.0-preview.3.jar` e `geckolib-neoforge-1.21.1-4.9.3.jar` na pasta `mods`. Use apenas uma versão do Etheris. No cliente de desenvolvimento, `gradlew runClient` já resolve o GeckoLib. Esta armadura é uma prévia visual, sem receita de sobrevivência ou bônus de escola.

Entre no seu mundo de teste. Com comandos habilitados, use `/etheris astra`, equipe as quatro peças e pressione F5. A capa pertence ao peitoral. O comando adiciona os itens ao inventário e derruba o excedente; não substitui o que estiver equipado. O nome do conjunto é Astra, diferente do Véu do Eclipse anterior.

## O que mudou

- Capa violeta com bordado dourado, forro de marfim e emblema celestial luminoso nas costas. Quatro articulações de altura, distribuídas em três faixas com pequenas sobreposições nas costuras.
- Laterais e estolas mais finas e divididas em três segmentos. A parte inferior se dobra com atraso em relação à superior.
- Deslocamento e viradas reais alimentam uma simulação amortecida: a capa levanta na corrida, inclina nas curvas e assenta depois de parar. A orientação é calculada por segmento, evitando uma única placa comprida.
- Talismãs acompanham a cabeça com atraso; pontas das mangas respondem ao passo. O agachamento altera gradualmente a postura das vestes.
- Halos orbitais, astrolábio pulsante, estrelas e cristais emissivos. O brilho aparece no escuro; não ilumina os blocos ao redor e não exige shader de bloom.

## Teste visual com o jogador

1. Observe a silhueta parada em F5, pela frente e pelas costas.
2. Caminhe, corra por alguns segundos e solte as teclas. A ponta da capa deve chegar depois dos ombros e continuar um pouco antes de assentar.
3. Faça curvas, ande de lado e gire a cabeça. Confira se talismãs e capa têm atrasos diferentes, sem arrancos.
4. Pule, aterrisse, agache e volte a ficar em pé. Confira interseções das estolas com as pernas e da capa com os braços.
5. Tire apenas o peitoral: a capa deve desaparecer. Tire apenas o capacete: halos e talismãs devem desaparecer.
6. Observe ao anoitecer; pause e retome o jogo. Os pontos luminosos devem pulsar sem transformar o tecido inteiro em material brilhante.

São articulações com inércia, não uma simulação completa de colisão de tecido. Não foram adicionados novos movimentos ao esqueleto vanilla do jogador. Elytra, capas cosméticas, natação, skins de braços finos e shaders precisam de revisão visual. A aprovação de aparência e sensação de movimento desta revisão fica com o usuário, conforme solicitado.

## Edição e verificação

Abra `art/blockbench/astra_oracle.bbmodel`. A prévia do editor mostra movimento ambiente; o comportamento reativo do jogo está em `AstraClothMotion.java` e `AstraArmorModel.java`. Os oito ossos `armor*` pertencem aos slots do GeckoLib; não renomeie nem remova os pais. As estrelas possuem máscara emissiva própria. Os geradores sobrescrevem os recursos: salve uma cópia de alterações manuais antes de executá-los.

`node tools/astra_armor.mjs` gera modelo, atlas, animações, ícones e projeto editável. `node tools/validate_astra.mjs` verifica hierarquia, costuras, UVs e paridade. `gradlew build runGameTestServer` executa também os testes de estabilidade das articulações. Nesta revisão, build, verificações de assets, testes de movimento e 29 GameTests passaram; isso não substitui sua avaliação visual.

Referência de integração: [GeckoLib 4 Armor](https://github.com/bernie-g/geckolib/wiki/Geckolib-Armor-(Geckolib4)).

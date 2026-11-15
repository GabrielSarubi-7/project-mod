package com.etheris.item;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.Level;
import java.util.List;

/** Produces a vanilla written book, using the native book interface on both sides. */
public final class CodexItem extends Item {
    public CodexItem(Properties properties) { super(properties); }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.success(player.getItemInHand(hand));
        var book = new ItemStack(Items.WRITTEN_BOOK);
        var pages = List.of(
            "ETHERIS\nEcos do Infinito\n\nDespertar\nExplore geodos. Colete ametista, cobre e madeira. O Coletor Etérico capta energia sob céu aberto: 2 de dia ou 4 à noite a cada 5 segundos.",
            "CONDENSAÇÃO\n\nUse ametista no Coletor com 20 Éter para receber um Fragmento. Fragmentos carregam as outras máquinas com 20 Éter cada. A energia da máquina é mostrada ao interagir.",
            "RUNAS E MAGIAS\n\nCobre na Forja com 20 Éter produz runas. No Foco de Vinculação, aprenda as quatro magias seguintes usando runas e 40 Éter cada. As últimas três exigem Cristais Estáveis e 40 Éter cada.",
            "FOCO PESSOAL\n\nUse no ar para conjurar. Agache e use para selecionar. Use no Foco de Vinculação para recarregar. Éter regenera 1 a cada 2 segundos. Cura sem ferimentos não gasta energia.",
            "DISCIPLINAS\n\nAstra: projétil e visão noturna.\nVita: cura e crescimento.\nMateria: pedra e refino de ferro.\nFluxo: velocidade e salto.\nFulgor: ataque de alcance maior.",
            "REAGENTES\n\nTransmutar Fragmento: pedregulho na mão secundária vira pedra.\nLiga de Éter: ferro bruto na mão secundária vira lingote.\nSemente Quimérica: aponte uma plantação imatura a até 5 blocos.",
            "REDE INICIAL\n\nVara: origem e destino a até 8 blocos. Máquinas têm uma saída; o Nó aceita quatro e alterna destinos, com orçamento total de 8 Éter por 5 segundos. Repita um enlace do Nó para removê-lo.",
            "EXPLORAÇÃO\n\nRuínas Astrais aparecem em terreno plano no Overworld em chunks novos. Contêm ametista, fragmentos e runas. São uma rota alternativa de materiais. A raridade ainda precisa de balanceamento.",
            "AUTOMAÇÃO\n\nFunis inserem reagentes e retiram produtos do Coletor, Forja, Triturador e Refinaria. Falta de energia ou saída cheia pausa sem consumir nada. Um ciclo leva 5 segundos. Use mão vazia para retirar produtos.",
            "CADEIA PRISMÁTICA\n\nTriturador: ferro bruto vira 2 pós de ferro por 30 Éter; ametista vira 2 pós de cristal por 20. Refinaria: pó de ferro vira lingote por 20; pó de cristal vira Cristal Estável por 40.",
            "SINTONIZAÇÃO\n\nCristais Estáveis abrem as magias finais desta etapa e o Nó de Distribuição. Consulte a aba Etheris nos avanços para acompanhar os próximos objetivos. Avanços indicam conquistas, não garantem duração de gameplay.",
            "LIMITES DA VERSÃO\n\nMáquinas são públicas. Quebrar uma máquina derruba seu inventário e perde a energia armazenada. Redes não carregam chunks. Tiers avançados, armaduras e megamáquinas ainda não estão disponíveis."
        ).stream().map(text -> Filterable.<Component>passThrough(Component.literal(text))).toList();
        book.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(Filterable.passThrough("Códice de Etheris"), "Etheris", 0, pages, true));
        player.setItemInHand(hand, book);
        return InteractionResultHolder.success(book);
    }
}

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
            "RUNAS E MAGIAS\n\nCarregue a Forja de Runas. Use cobre para criar uma runa por 20 Éter. Uma runa no Foco de Vinculação com 40 Éter aprende a próxima magia. São oito nesta versão.",
            "FOCO PESSOAL\n\nUse no ar para conjurar. Agache e use para selecionar. Use no Foco de Vinculação para recarregar. Éter regenera 1 a cada 2 segundos. Cura sem ferimentos não gasta energia.",
            "DISCIPLINAS\n\nAstra: projétil e visão noturna.\nVita: cura e crescimento.\nMateria: pedra e refino de ferro.\nFluxo: velocidade e salto.\nFulgor: ataque de alcance maior.",
            "REAGENTES\n\nTransmutar Fragmento: pedregulho na mão secundária vira pedra.\nLiga de Éter: ferro bruto na mão secundária vira lingote.\nSemente Quimérica: aponte uma plantação imatura a até 5 blocos.",
            "REDE INICIAL\n\nVara de Enlace: clique na origem e depois no destino, a até 8 blocos. Cada origem tem uma saída. Agache para trocar a origem marcada. Transferência: até 8 Éter a cada 5 segundos.",
            "EXPLORAÇÃO\n\nRuínas Astrais aparecem em terreno plano no Overworld em chunks novos. Contêm ametista, fragmentos e runas. São uma rota alternativa de materiais. A raridade ainda precisa de balanceamento.",
            "LIMITES DA VERSÃO\n\nMáquinas são públicas. Quebrar uma máquina perde a energia armazenada. Redes não carregam chunks. Arte é provisória. Tiers avançados, armaduras e megamáquinas ainda não estão disponíveis."
        ).stream().map(text -> Filterable.<Component>passThrough(Component.literal(text))).toList();
        book.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(Filterable.passThrough("Códice de Etheris"), "Etheris", 0, pages, true));
        player.setItemInHand(hand, book);
        return InteractionResultHolder.success(book);
    }
}

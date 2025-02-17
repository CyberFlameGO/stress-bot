package dev.sim0n.stressbot.bot.internal.listener.play;

import dev.sim0n.stressbot.bot.Bot;
import dev.sim0n.stressbot.packet.PacketRepository;
import dev.sim0n.stressbot.packet.internal.play.clientbound.SConfirmTransaction;
import dev.sim0n.stressbot.packet.internal.play.serverbound.CConfirmTransaction;
import dev.sim0n.stressbot.packet.internal.listener.PlayEnsuredFilteredPacketListener;
import dev.sim0n.stressbot.util.NettyUtil;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author sim0n
 */
public class ConfirmTransactionListener extends PlayEnsuredFilteredPacketListener<SConfirmTransaction> {

    @Override
    public void onPacketReceive(ChannelHandlerContext ctx, Bot bot, SConfirmTransaction packet) {
        CConfirmTransaction clientTransaction = PacketRepository.PLAY.makePacket(CConfirmTransaction.class);
        {
            clientTransaction.setWindowId(packet.getWindowId());
            clientTransaction.setUid(packet.getUid());
            clientTransaction.setAccepted(packet.isAccepted());
        }
        NettyUtil.sendPacket(ctx, clientTransaction);
    }
}

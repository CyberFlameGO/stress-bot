package dev.sim0n.stressbot.bot.internal.task;

import dev.sim0n.stressbot.bot.Bot;
import dev.sim0n.stressbot.bot.task.Task;
import dev.sim0n.stressbot.packet.PacketRepository;
import dev.sim0n.stressbot.packet.internal.play.serverbound.CAnimation;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author sim0n
 *
 * This task will make the bot randomly swing
 */
public class SwingTask extends Task {
    public SwingTask() {
        super(20, true);
    }

    @Override
    public void run(ChannelHandlerContext ctx, Bot bot) {
        ctx.writeAndFlush(PacketRepository.PLAY.makePacket(CAnimation.class).toBuffer(ctx));
    }
}

package dev.sim0n.stressbot.bot;

import dev.sim0n.stressbot.network.PacketDecoder;
import dev.sim0n.stressbot.network.PacketEncoder;
import dev.sim0n.stressbot.network.PacketHandler;
import dev.sim0n.stressbot.util.PacketBuffer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author sim0n
 */
@RequiredArgsConstructor
public class SimpleBotController<Buf extends ByteBuf> implements BotController<Buf> {
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private final BotRepository repo = new BotRepository();

    private final String address;
    private final int port;
    private final BotFactory<Buf> botFactory;
    @Override
    public void makeBot(Consumer<ChannelHandlerContext> connectAction, Consumer<ChannelHandlerContext> disconnectAction) {
        Bot bot = this.botFactory.makeBot(connectAction, disconnectAction);

        this.repo.getBots().add(bot);

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast("timeout", new ReadTimeoutHandler(30))
                                    .addLast("decoder", new PacketDecoder())
                                    .addLast("encoder", new PacketEncoder())
                                    .addLast("packet_handler", new PacketHandler(bot));
                        }
                    });

            bootstrap.connect(address, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Bot> getBots() {
        return this.repo.getBots();
    }

    public static AddAddress builder() {
        return address -> port -> factory -> new SimpleBotController<>(address, port, factory);
    }

    public interface AddAddress {
        AddPort address(String address);
    }

    public interface AddPort {
        AddFactory<PacketBuffer> port(int port);
    }

    public interface AddFactory<Buf extends ByteBuf> {
        BotController<Buf> factory(BotFactory<Buf> factory);
    }
}

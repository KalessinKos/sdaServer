import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class Server{
    ServerSocketChannel serverSocket;
    Selector selector;

    public Server(int port) {

        {
            try {
                serverSocket = ServerSocketChannel.open();
                serverSocket.configureBlocking(false);
                serverSocket.bind(new InetSocketAddress("0.0.0.0", 14449));
                selector = Selector.open();
                serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() throws IOException {
        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            for (SelectionKey key : selectedKeys) {
                if (key.isAcceptable()) {
                    SocketChannel clientSocket = serverSocket.accept();
                    clientSocket.configureBlocking(false);
                    clientSocket.register(selector, SelectionKey.OP_READ);
                }
                if (key.isReadable()) {
                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    SocketChannel client = (SocketChannel) key.channel();
                    try {
                        client.read(buffer);
                    } catch (IOException e) {
                        key.cancel();
                    }
                    String message = new String(buffer.array());
                    Set<SelectionKey> keys = selector.keys();
                    for (SelectionKey keyy : keys) {
                        if (keyy.isValid() && !keyy.isAcceptable()) {
                            System.out.println(keyy);
                            SocketChannel tosend = (SocketChannel) keyy.channel();
                            tosend.write(ByteBuffer.wrap(message.getBytes()));
                        }
                    }
                }
            }
selectedKeys.clear();
        }

    }
}
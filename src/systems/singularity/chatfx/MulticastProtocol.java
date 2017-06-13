package systems.singularity.chatfx;

/**
 * Created by pedro on 6/8/17.
 */
public final class MulticastProtocol {
    private MulticastProtocol() {
        // Avoid class instantiation
    }

    public static final class Payload {
        public static final int length = 22;
        String ipv4Address;
        int chatPort;
        int filePort;

        public Payload(String ipv4Address, int chatPort, int filePort) {
            this.ipv4Address = ipv4Address;
            this.chatPort = chatPort;
            this.filePort = filePort;
        }

        @Override
        public String toString() {
            String[] ipv4Parts = this.ipv4Address.split("\\.");
            return String.format("%3s%3s%3s%3s%05d%05d", ipv4Parts[0], ipv4Parts[1], ipv4Parts[2], ipv4Parts[3], this.chatPort, this.filePort).replace(' ', '0');
        }

        public Payload parse(String s) {
            return new Payload(
                    String.format("%d.%d.%d.%d", Integer.parseInt(s.substring(0, 3)), Integer.parseInt(s.substring(3, 6)), Integer.parseInt(s.substring(6, 9)), Integer.parseInt(s.substring(9, 12))),
                    Integer.parseInt(s.substring(12, 17)),
                    Integer.parseInt(s.substring(17, 22))
            );
        }
    }
}

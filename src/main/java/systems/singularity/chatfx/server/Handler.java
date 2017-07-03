package systems.singularity.chatfx.server;

import systems.singularity.chatfx.util.Protocol;
import systems.singularity.chatfx.util.RDT;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.Map;

/**
 * Created by pedro on 7/2/17.
 */
public class Handler extends Thread implements Protocol.Receiver {
    public Handler(int port) throws SocketException {
        RDT.getReceiver(port).setOnReceiveListener(null, this);
    }

    @Override
    public void onReceive(InetAddress address, int port, Map<String, String> headers, String message) {
        System.out.printf("\t\t\t\t%s\t%s\t%s\t%s\n", address, port, headers, message);

//        String[] basic = new String(Base64.getDecoder().decode(headers.get("Authorization").split(" ")[1])).split(":");
//        String[] pragma = headers.get("Pragma").split(";");
//
//        synchronized (UserRepository.getInstance()) {
//            try {
//                switch (pragma[0]) {
//                    case "login":
//                        User user = new User();
//                        user.setUsername(basic[0]);
//                        user = UserRepository.getInstance().get(user);
//
//                        if (user == null) {
//                            user = new User();
//                            user.setUsername(basic[0]);
//                            user.setPassword(basic[1]);
//
//                            switch (pragma[1]) {
//                                case "chat":
//                                    user.setPort_chat(port);
//                                    break;
//
//                                case "file":
//                                    user.setPort_file(port);
//                                    break;
//
//                                case "rtt":
//                                    user.setPort_rtt(port);
//                                    break;
//
//                                default:
//                                    break;
//                            }
//
//                            UserRepository.getInstance().insert(user);
//
//                            try {
//                                Map<String, String> map = new HashMap<>();
//                                map.put("Pragma", "login;201");
//
//                                Protocol.Sender.sendMessage(RDT.getSender(address, port), map, "");
//                            } catch (SocketException | InterruptedException | UnknownHostException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            if (user.getPassword().equals(basic[1])) {
//                                switch (pragma[1]) {
//                                    case "chat":
//                                        user.setPort_chat(port);
//                                        break;
//
//                                    case "file":
//                                        user.setPort_file(port);
//                                        break;
//
//                                    case "rtt":
//                                        user.setPort_rtt(port);
//                                        break;
//
//                                    default:
//                                        break;
//                                }
//
//                                UserRepository.getInstance().update(user);
//
//                                try {
//                                    Map<String, String> map = new HashMap<>();
//                                    map.put("Pragma", "login;200");
//
//                                    Protocol.Sender.sendMessage(RDT.getSender(address, port), map, "");
//                                } catch (SocketException | InterruptedException | UnknownHostException e) {
//                                    e.printStackTrace();
//                                }
//                            } else
//                                try {
//                                    Map<String, String> map = new HashMap<>();
//                                    map.put("Pragma", "login;401");
//
//                                    Protocol.Sender.sendMessage(RDT.getSender(address, port), map, "");
//                                } catch (SocketException | InterruptedException | UnknownHostException e) {
//                                    e.printStackTrace();
//                                }
//                        }
//
//                        break;
//
//                    default:
//                        break;
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
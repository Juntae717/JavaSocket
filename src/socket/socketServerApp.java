package socket;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class socketServerApp {
    private static final Logger logger = Logger.getLogger(socketServerApp.class.getName());

    /**
     * LINE :: 서버 포트 설정
     */
    private static final int PORT_NUMBER = 4432;


    /**
     * FUNCTION :: socketServerApp 메인 메소드
     */
    public static void main(String[] args) {
        logger.info(":::                                                :::");
        logger.info(":::       Socket Application  Process Start        :::");
        logger.info(":::                                                :::");

        try(ServerSocket server = new ServerSocket(PORT_NUMBER)){
            HashMap<String, Object> hm = new HashMap<String, Object>();
            while(true){
                Socket connection = server.accept();
                Thread task = new socketServerProcessThread(connection, hm);
                task.start();
            }
        }catch(Exception e){
            logger.log(Level.SEVERE, "Error", e.toString());
        }
    }
}

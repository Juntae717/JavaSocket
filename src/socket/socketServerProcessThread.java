package socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class socketServerProcessThread extends Thread {

    private static final Logger logger = Logger.getLogger(socketServerProcessThread.class.getName());

    private Socket socket;
    private String ip;
    private BufferedReader br = null;
    private PrintWriter pw = null;
    private HashMap<String, Object> hm;

    SimpleDateFormat format = new SimpleDateFormat ( "yyyy년MM월dd일 HH:mm:ss");

    /**
     * FUNCTION :: 소켓서버 멀티스레드
     */
    public socketServerProcessThread(Socket socket, HashMap<String, Object> hm) {
        this.socket = socket;
        this.hm = hm;
        try{
            /**
             * LINE :: 현재 시간 설정
             */
            Date time = new Date();
            String strTime = format.format(time);

            /**
             * LINE :: 클라이언트 IP 가져오기
             */
            String connIp = socket.getInetAddress().getHostAddress();
            ip = connIp;

            /**
             * LINE :: 접속 메시지 클라이언트로 전송
             */
            broadcast("["+ strTime +"]\n" + ip + "님이 접속하셨습니다.");

            /**
             * LINE :: 접속 메시지 서버 콘솔 및 로그에 작성
             */
            logger.info("["+ strTime +"]\n" + "접속한 사용자의 IP : " + ip);
            System.out.println("["+ strTime +"]\n" + "접속한 사용자의 IP : " + ip);

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            /**
             * LINE :: 스레드 동기화(접속 클라이언트)
             */
            synchronized (hm) {
                hm.put(this.ip, pw);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "ERROR", e.toString());
        }
    }

    /**
     * FUNCTION :: 기본 실행 메소드
     */
    public void run() {
        try {
            String line = null;
            while((line = br.readLine()) != null) {
                /**
                 * LINE :: 현재 시간 설정
                 */
                Date time = new Date();
                String strTime = format.format(time);
                /**
                 * LINE :: "/quit" 입력시 접속해제 => Finally
                 */
                if(line.equals("/quit")) {
                    /**
                     * LINE :: 접속 종료 메시지 서버 콘솔 및 로그에 작성
                     */
                    logger.info("["+ strTime +"]\n" + "접속해제한 사용자의 IP : "+ ip);
                    System.out.println("["+ strTime +"]\n" + "접속해제한 사용자의 IP : "+ ip);
                    break;
                }
                /**
                 * LINE :: "/quit" 이외의 메시지 클라이언트에 전송
                 */
                else {
                    System.out.println("["+ strTime +"]\n" + ip + " : "+line);
                    broadcast("["+ strTime +"]\n" + ip + " : "+line);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "ERROR", e.toString());
        } finally {
            /**
             * LINE :: 현재 시간 설정
             */
            Date time = new Date();
            String strTime = format.format(time);
            synchronized (hm) {
                hm.remove(ip);
            }
            /**
             * LINE :: 접속 종료 메시지 클라이언트로 전송
             */
            broadcast("["+ strTime +"]\n" + ip + "님이 접속을 종료했습니다.");
            try {
                if(socket != null) socket.close();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "ERROR", e.toString());
            }
        }
    }

    /**
     * FUNCTION :: 클라이언트로 메시지 전송
     */
    public void broadcast(String msg) {
        synchronized (hm) {
            Collection<Object> collection = hm.values();
            Iterator<?> iter = collection.iterator();
            while(iter.hasNext()) {
                PrintWriter pw = (PrintWriter)iter.next();
                pw.println(msg);
                pw.flush();
            }
        }
    }
}

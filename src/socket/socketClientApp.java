package socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class socketClientApp {
    /**
     * FUNCTION :: socketClientApp 메인 메소드
     */
    public static void main(String[] args){
        Socket socket = null;
        BufferedReader br = null;
        PrintWriter pw = null;

        try {
            /**
             * LINE :: 소켓 서버 HOST IP 및 PORT 설정
             */
            socket = new Socket("192.168.0.104", 4432);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

            /**
             * LINE :: 멀티 스레드로 Socket, BufferedReader(서버에서 요청 받을 메시지) 파라미터 전송
             */
            socketClientProcessThread scpt = new socketClientProcessThread(socket, br);
            scpt.start();
            String line = null;
            while((line = keyboard.readLine()) != null) {
                /**
                 * LINE :: 서버로 메시지 전송
                 */
                pw.println(line);
                pw.flush();
                /**
                 * LINE :: "/quit" 입력 시 while문 종료 => finally
                 */
                if(line.equals("/quit")) {
                    break;
                }
            }
            System.out.println("클라이언트 접속 종료");
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            /**
             * LINE :: 소켓 연결 해제
             */
            try {
                if(br != null) {
                    br.close();
                }
                if(pw != null) {
                    pw.close();
                }
                if(socket != null) {
                    socket.close();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

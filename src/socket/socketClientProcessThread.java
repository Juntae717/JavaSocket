package socket;

import java.io.BufferedReader;
import java.net.Socket;

public class socketClientProcessThread extends Thread{
    private Socket socket = null;
    private BufferedReader br = null;

    public socketClientProcessThread(Socket socket,BufferedReader br) {
        this.socket = socket;
        this.br = br;
    }

    public void run() {
        try {
            /**
             * LINE :: 서버에서 받은 메시지 출력
             */
            String line = null;
            while((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(br != null) {
                    br.close();
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

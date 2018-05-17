import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String arg[]) {
		
		Socket socket = null;	//client와 통신하기 위한 socket
		ServerSocket server_socket = null;	//서버 생성을 위한 ServerSocket
		BufferedReader in = null;	//Client로 부터 데이터를 읽어들이기 위한 입력 스트림
		PrintWriter out = null;		//Clinet로 부터 데이터를 내보내기 위한 출력 스트림
		
		try {
			server_socket = new ServerSocket(특정 포트 입력);
			
		}catch(IOException e){
			
			System.out.println("해당 포트가 열려있습니다.");
		}
		try {
			
			System.out.println("서버 오픈!!");
			socket = server_socket.accept();
			
			 
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));    //입력스트림 생성
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))); //출력스트림 생성
            
            String str = null;
            str = in.readLine();                //Client로부터 데이터를 읽어옴
 
            System.out.println("Client로 부터 온 메세지 : " + str);
            
            out.write(str);
            out.flush();
            socket.close();
        }catch(IOException e){}
    }
}
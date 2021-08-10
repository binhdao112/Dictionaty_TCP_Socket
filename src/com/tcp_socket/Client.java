package com.tcp_socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Client {
	Scanner scan= new Scanner(System.in);
	ServerSocket svsocket=null;
	Socket socket=null;
	DataInputStream dis= null;
	DataOutputStream dos= null;
	@SuppressWarnings("deprecation")
	public Client(int port) throws InterruptedException  {
		try {
			socket= new Socket("localhost", port);
			System.out.println("Connected server");
			dis= new DataInputStream(socket.getInputStream() );
			dos= new DataOutputStream(socket.getOutputStream());
			String a="";
			while(true) {
				System.out.println("Nhap tu can tim");
				a=scan.nextLine();
				dos.writeUTF(a);
				dos.flush();
				String repone=dis.readUTF();
				if(!repone.equals("end")) {
					System.out.println(repone);
				}else {
					break;
				}
			}
		}catch (Exception e) {
		}
		System.out.println("Kết thúc");
		
		
		
	}
	public void close() throws IOException {
		if(svsocket!=null)svsocket.close();
		if(socket!=null)socket.close();
		if(dis!=null)dis.close();
		if(dos!=null)dos.close();
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		Scanner scan= new Scanner(System.in);
		Client cl= new Client(5000);
		cl.close();
		
		
		 
	}
	
}

package com.tcp_socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

public class Sever {
	ServerSocket svsocket=null;
	Socket socket=null;
	DataInputStream dis= null;
	DataOutputStream dos= null;
	BufferedReader bur=null;
	public Sever(int port, Path path) {
		try {
			svsocket= new ServerSocket(port);
			System.out.println("Server start.....");
			System.out.println("Waiting client");
			socket=svsocket.accept();
			System.out.println("client is accepted");
			dis= new DataInputStream(socket.getInputStream() );
			dos= new DataOutputStream(socket.getOutputStream());
			bur=new BufferedReader(Files.newBufferedReader(path));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void taoFile(Path path) throws IOException {
		if (!Files.exists(path)) {
			Files.createFile(path);
		} else {
			System.out.println("file exist");
		}
	}
	public void setHash(HashMap<String, String> hash) throws IOException {
		String line="";
		while((line=bur.readLine())!=null) {
			String[] tmp=line.split(";");
			hash.put(tmp[0], tmp[1]);
		}
	}
	public String addDictionaty(String eng, String viet, HashMap<String, String> hash) throws IOException {
		Set<String> key= hash.keySet();
		for(String khoa: key) {
			if(eng.equals(khoa)) {
				return "từ đã có trong từ điển";
			}
		}
		hash.put(eng, viet);
		return "Thêm thành công";
	}
	public String searchDictionaty(String txt, HashMap<String, String> hash) {
		String result="Không có từ đó";
		Set<String> key= hash.keySet();
		for(String key1: key) {
			if(key1.equals(txt)) {
				result=txt+" có nghĩa là: "+hash.get(key1);
			}else if(txt.equals(hash.get(key1).toString())) {
				result=txt+" dịch là: "+key1;
			}
		}
		return result;
	}
	public void saveAll(HashMap<String, String> hash,Path path ) throws IOException {
		Set<String> key=hash.keySet();
		DataOutputStream dosf2= new DataOutputStream(Files.newOutputStream(path));
		BufferedReader br=new BufferedReader(Files.newBufferedReader(path));
		byte[] data=null;
		for(String khoa: key) {
		String txt1="";
		if(br.readLine()!=null) {
			txt1="\n"+khoa+";"+hash.get(khoa);
		}else {
			txt1=khoa+";"+hash.get(khoa);
		}
		data=txt1.getBytes();
		dosf2.write(data);
		dosf2.flush();
	}
		if(br!=null)br.close();
		if(dosf2!=null)dosf2.close();
	}
	public String deleteWord(String txt,HashMap<String, String> hash) throws IOException {
		Set<String> key=hash.keySet();
		int status = 0;
		for(String khoa: key) {
			if(txt.equals(khoa)) {
				status=1;
			}
		}
		if(status==0) {
			return "Không có từ trong từ điển để xóa";
		}
		hash.remove(txt);
		
		return "Đã xóa thành cồng";
	}
	public void close() throws IOException {
		if(svsocket!=null)svsocket.close();
		if(socket!=null)socket.close();
		if(dis!=null)dis.close();
		if(dos!=null)dos.close();
		if(bur!=null)bur.close();
	}
	
	public static void main(String[] args) throws IOException {
		Pattern word = Pattern.compile("^[\\w &&[^0-9]]+$", Pattern.UNICODE_CHARACTER_CLASS);
		Pattern statementadd=Pattern.compile("^ADD(;)[\\w &&[^0-9]]+(;)[\\w &&[^0-9]]+$", Pattern.UNICODE_CHARACTER_CLASS);
		Pattern statementdel=Pattern.compile("^DEL(;)[\\w &&[^0-9]]+");
		String link = ".\\src\\dictionary.txt";
		Path path = Paths.get(link);
		taoFile(path);
		Sever sv= new Sever(5000, path);	
		HashMap<String, String> hash= new HashMap<String, String>();
		sv.setHash(hash);
		while(true) {
			String[] tmp=null;
			String txt=sv.dis.readUTF();
			if(txt.equals("")) {
				sv.dos.writeUTF("Chuỗi trỗng!! Các cú pháp \n -Nhập từ tiếng anh hoặc tiếng việt để xem nghĩa tiếng việt hoặc tiếng anh của nó \n -ADD;eng;viet: để thêm từ vào từ điển \n -DEL;eng: xóa một từ khỏi từ điển \n -bye: để kết thúc chương trình");
				sv.dos.flush();
			}else if(word.matcher(txt).matches()==true||statementadd.matcher(txt).matches()==true||statementdel.matcher(txt).matches()==true) {
				tmp=txt.split(";");
				if(tmp[0].equals("bye")) {
					sv.saveAll(hash, path);
					break;
				}
				else if(tmp[0].equals("ADD")) {
					String rs=sv.addDictionaty(tmp[1].toLowerCase().trim(), tmp[2].toLowerCase().trim(), hash);
					sv.dos.writeUTF(rs);
					sv.dos.flush();
				}else if(tmp[0].equals("DEL")) {
					String rs=sv.deleteWord(tmp[1].toLowerCase().trim(), hash);
					sv.dos.writeUTF(rs);
					sv.dos.flush();
				}
				else {
					String rs=sv.searchDictionaty(tmp[0].toLowerCase().trim(), hash);
					sv.dos.writeUTF(rs);
					sv.dos.flush();
				}
			}else {
				sv.dos.writeUTF("Cú pháp sai!! Kiểm tra lại cú pháp \n -Nhập từ tiếng anh hoặc tiếng việt để xem nghĩa tiếng việt hoặc tiếng anh của nó \n -ADD;eng;viet: để thêm từ vào từ điển \n -DEL;eng: xóa một từ khỏi từ điển \n -bye: để kết thúc chương trình");
				sv.dos.flush();
			}
			
			
		}
		sv.dos.writeUTF("end");
		sv.dos.flush();
		System.out.println("END");
		sv.close();
		
	}
		
}

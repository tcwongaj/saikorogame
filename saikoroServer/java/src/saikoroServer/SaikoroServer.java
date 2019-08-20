package saikoroServer;

import java.net.InetSocketAddress;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaikoroServer extends WebSocketServer {
	
	
	public SaikoroServer(InetSocketAddress address) {
		super(address);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		//conn.send("Welcome to the server!"); //This method sends a message to the new client
		//broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected
		System.out.println("new connection to " + conn.getRemoteSocketAddress());
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		System.out.println("received message from "	+ conn.getRemoteSocketAddress() + ": " + message);

		String[] cmd = message.split(",");
		String sql = "";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		
		switch (Integer.valueOf(cmd[0])) {
		
			case 1: //login, check password correct/exist or not
				sql = "SELECT * FROM userprofile WHERE user_name = '" + cmd[1] + "';";
				String[] result = sql_query(1, sql);
				if (result == null) {
					//System.out.println("no such user");
					conn.send("0");
				} else if (result != null) {
					//System.out.println(cmd[0] + " " + cmd[1] + " " + cmd[2] + " " + result[0] + " " + result[1]);
					if (cmd[2].trim().equals(result[1].trim())) {
						//System.out.println("password correct");
						conn.send("1");
						
					} else {
						//System.out.println("wrong password");
						conn.send("2");
					}
				}
				break;
				
			case 2: //register, assuming user have gone through case 1, and input data will not be duplicated with data in userprofile
				sql = "INSERT INTO userprofile (user_name, user_password, date_create, date_lastlogin) VALUES ('"+cmd[1]+"','"+cmd[2]+"','"+LocalDate.now()+"','"+LocalDate.now()+"');";
				sql_exec(sql);
				conn.send("1");
				break;
				
			case 3:
				LocalDateTime now = LocalDateTime.now();
				String formatDateTime = now.format(formatter);
				sql = "INSERT INTO usersession (user_name, game_status, time_start) VALUES ('" + cmd[1] + "',1,'" + formatDateTime + "')"; //game_status=1 during game session
				sql_exec(sql);
				sql = "SELECT * FROM userprofile WHERE user_name = '" + cmd[1] + "';";  // get highest score
				String[] hresult = sql_query(2, sql);
				conn.send("3,"+hresult[1]);
				break;
				
			case 4: //processing dice rolling and score calculation
				int pd1 = (int)(Math.random() * 6 + 1);
				int pd2 = (int)(Math.random() * 6 + 1);
				int cd1 = (int)(Math.random() * 6 + 1);
				int cd2 = (int)(Math.random() * 6 + 1);
				int pds = pd1 + pd2;
				int cds = cd1 + cd2;
				int winner;
				if (pds == cds) {
					winner = 0;  //draw, same score
				} else if (pds > cds) {
					winner = 1;  //player win
				} else {
					winner = 2;  //CPU win
				}
				
				sql = "SELECT session_id FROM usersession WHERE session_id = (SELECT MAX(session_id) FROM usersession WHERE user_name = '" + cmd[1] + "' AND game_status = 1)"; //get session ID
				String[] sessionid = sql_query(3, sql);
				LocalDateTime now2 = LocalDateTime.now();
				String formatDateTime2 = now2.format(formatter);
				sql = "UPDATE usersession SET game_status=0, time_end='" + formatDateTime2 + "', score_player=" + pds + ", score_cpu=" + cds + ", winner=" + winner + " WHERE session_id = " + sessionid[0] + ";";  // update session record
				sql_exec(sql);
				
				
				sql = "SELECT * FROM userprofile WHERE user_name = '" + cmd[1] + "';";  // get highest score
				String[] hresult2 = sql_query(2, sql);

				if (pds >= Integer.valueOf(hresult2[1])) {
					sql = "UPDATE userprofile SET high_score = " + pds + " WHERE user_name='" + cmd[1] + "';";
					sql_exec(sql);
					
				}
				
				conn.send("4," + pd1 + "," + pd2 + "," + cd1 + "," + cd2 + "," + pds + "," + cds + "," + winner);
				
				
				break;
				
				
				
			case 5:
				System.out.println("5");
				sql = "SELECT session_id FROM usersession WHERE session_id = (SELECT MAX(session_id) FROM usersession WHERE user_name = '" + cmd[1] + "' AND game_status = 1)";  //close
				String[] sessionid2 = sql_query(3, sql);
				LocalDateTime now3 = LocalDateTime.now();
				String formatDateTime3 = now3.format(formatter);
				sql = "UPDATE usersession SET game_status=0, time_end='" + formatDateTime3 + "' WHERE session_id = " + sessionid2[0] + ";";
				sql_exec(sql);

				
				break;
		}
		
		
	}



	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.err.println("an error occured on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
	}
	
	@Override
	public void onStart() {
		System.out.println("server started successfully");		
	}
		
	public void	sql_exec(String sql) {
		String sql_ = sql;
        PreparedStatement sql_exec;
		try {
			Connection connDB = dbUtil.getConnection();
			sql_exec = connDB.prepareStatement(sql_);
	        sql_exec.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String[] sql_query(int task, String sql) {
		String sql_ = sql;
		String result_ = null;
		String[] result = null;
        PreparedStatement sql_euery;
		try {
			Connection connDB = dbUtil.getConnection();
			sql_euery = connDB.prepareStatement(sql_);
	        ResultSet rs = sql_euery.executeQuery();
	        while(rs.next()){
	        	switch (task) {
	        	case 1 :
	        		result_ = rs.getString(2)+","+rs.getString(3);
	        		break;
	        	case 2:
	        		result_ = rs.getString(2)+","+rs.getInt(6);
	        		break;
	        	case 3:
	        		result_ = rs.getString(1);
	        		break;
	        	}
	        	result = result_.split(",");
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) throws SQLException {
		String host = "localhost";
		int port = 8887;
		WebSocketServer server = new SaikoroServer(new InetSocketAddress(host, port));
		server.run();
	}

}


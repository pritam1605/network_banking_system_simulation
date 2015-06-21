/*	Author:Pritam N. Bohra
	Date: 26th Oct,2012
	Program Name: Bank Server
*/

import java.io.*;
import java.util.*;
import java.net.*;
import java.sql.*;

class Global
{
	static public BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
}

class OnlyPositiveException extends Exception
{
	OnlyPositiveException(String msg)
	{
		super(msg);
	}
	
	OnlyPositiveException()
	{
		super();
		System.out.println("\nOnly positive numbers expected");
	}
}

class Bank_Server
{
	static private ServerSocket ss=null;
	static private Socket sock=null;
	static int count=0;
		
	public static void main(String args[]) throws Exception
	{
		try
		{
			InetAddress server_ip=InetAddress.getLocalHost();								
			ss=new ServerSocket(1234);
			System.out.println("\n*************************************WELCOME TO ATM BANKING*************************************");
			System.out.println("\nATM Server is waiting for client request................");
			System.out.println("\nATM Server IP Address:\t\t\t\t"+server_ip+"\nATM Server running on Port Number:\t\t1234");
		}
		catch(IOException e)
		{
			System.out.println("\nProblem occured in establishing connection");
			System.exit(1);
		}
		
		do
		{
			sock=ss.accept();									//waiting for client connection
			count++;
			System.out.println("\nClient "+count+" connected");
			
			ClientHandler handler =new ClientHandler(sock);
			handler.start();
		}while(true);
	}
}
			
class ClientHandler extends Thread		
{
	
	private String name,address,contact_no,email_id;
	private String password;
	private int acc_no;
	private double account_balance;
	private ServerSocket ss=null;
	private Socket sock=null;
	private String sql;
	
	private String to_client="";
	
	int count=0;
	Thread t;
	
	Scanner in;
	PrintWriter pw; 
	
	String driver = "sun.jdbc.odbc.JdbcOdbcDriver";															//Database Connectivity																							
	String url = "jdbc:odbc:NetworkBankingSystem";																
	String database_username = "";
	String database_password = "";
	ResultSet rs;
	Statement st;
	Connection con;
	
	
	public ClientHandler(Socket client)
	{
		//Set up reference to associated socket...
		sock = client;
		try
		{
			in=new Scanner(sock.getInputStream());
			pw =new PrintWriter(sock.getOutputStream(),true);
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
		}
	}	
		
	public void run()
	{
		boolean flag=false;

		
		while(!flag)
		{
			try
			{			
				String from_client=in.nextLine();
				int operation;
				String temp[]=from_client.split("#");
				
				operation=Integer.parseInt(temp[0]);
			
				switch(operation)
				{
					case 1:
							check_login(temp);
							break;
					case 2:
							check_details(temp);
							break;
					case 3:
							perform_deposite(temp);
							break;
					case 4:
							perform_withdraw(temp);
							break;
					case 5:
							perform_transfer(temp);
							break;
					case 6:
							flag=log_out();
							break;							
				}
			}
			catch(Exception e)
			{
				System.out.println("\nProblem occured during data transfer");
				e.printStackTrace();
				flag=true;
			}
		}
	}
	
	void check_login(String temp[])
	{
		boolean flag=false,check_status=true;
		int account_no_login=Integer.parseInt(temp[1]);
		String password_login=temp[2];
			
		//System.out.println("\nValues received from client");
		to_client="";
		
		try
		{
			Class.forName(driver);
			con=DriverManager.getConnection(url,database_username,database_password);
			st=con.createStatement();
			
			sql="select Status from Client_Info where Account_number="+account_no_login;
			rs=st.executeQuery(sql);
			
			while(rs.next())
			{
				check_status=rs.getBoolean(1);
			}
						
			sql="select Account_number,Customer_Password from Client_Info where Account_number="+account_no_login;
			rs=st.executeQuery(sql);
						 
			while(rs.next()) 
			{		
				flag=true;
				int acc_no_dB=rs.getInt(1);
				String pass_dB=rs.getString(2);
				
				if((account_no_login==acc_no_dB)&&(pass_dB.equals(password_login)))
				{	
					if(!check_status)
					{
						sql="select Customer_Name,Address,Contact_Number,Email_ID,Account_Balance from Client_Info where Account_number="+account_no_login;
						rs=st.executeQuery(sql);
						acc_no=acc_no_dB;
						System.out.println("\nAccount number "+ acc_no+" successfully logged in");
					
						while(rs.next()) 															//Login Successfully
						{	
							name=rs.getString(1);
							address=rs.getString(2);
							contact_no=rs.getString(3);
							email_id=rs.getString(4);
							account_balance=rs.getInt(5);
						}				
						to_client="1"+"#"+name+"#"+address+"#"+contact_no+"#"+email_id+"#"+account_balance+"#";
						pw.println(to_client);
					}
					else
					{
						to_client="4#";
						pw.println(to_client);
					}
				}
				else
				{
					to_client="2#";																	//Account Number and Password do not match
					pw.println(to_client);
				}
			}
			if(!flag)
			{
				to_client="3#";																		//Account Number Not Found
				pw.println(to_client);										
			}
			boolean status=true;																// to set that the user is logged in(in database)
			sql="update Client_Info set Status="+status+" where Account_number="+account_no_login;
			int check=st.executeUpdate(sql);
		}
		
		catch(SQLException e)
		{
			//System.out.println("\nProblem retrieving data from database");
			e.printStackTrace();
		}
		catch(ClassNotFoundException e1)
		{
			System.out.println("\nProblem retrieving data from database11111111111111");
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	void check_details(String temp[])
	{
		to_client="";
		System.out.println("\nAccount number "+ acc_no+" is currently checking his account details");		
		try
		{
			Class.forName(driver);
			con=DriverManager.getConnection(url,database_username,database_password);
			st=con.createStatement();
			
			sql="select Customer_Name,Address,Contact_Number,Email_ID,Account_Balance from Client_Info where Account_number="+acc_no;
			rs=st.executeQuery(sql);
			
			while(rs.next()) 															
			{	
				name=rs.getString(1);
				address=rs.getString(2);
				contact_no=rs.getString(3);
				email_id=rs.getString(4);
				account_balance=rs.getInt(5);
			}				
			to_client="1"+"#"+name+"#"+address+"#"+contact_no+"#"+email_id+"#"+account_balance+"#";
			pw.println(to_client);
						 
		}
		catch(SQLException e)
		{
			System.out.println("\nProblem retrieving data from database");
		}
		catch(ClassNotFoundException e1)
		{
			System.out.println("\nProblem retrieving data from database11111111111111");
		}
		catch(Exception e)
		{
			System.out.println("\nProblem occured while sending data");
		}
	}	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	void perform_deposite(String temp[])
	{
		to_client="";		
		int check;
		double amount_to_deposit=Double.parseDouble(temp[1]);
		
		System.out.println("\nAccount number "+ acc_no+" requested to deposite Rs. "+ amount_to_deposit +" into his account");			
		
		try
		{
			Class.forName(driver);
			con=DriverManager.getConnection(url,database_username,database_password);
			st=con.createStatement();
			
			sql="select Account_Balance from Client_Info where Account_number="+acc_no;
			rs=st.executeQuery(sql);
			double current_balance,new_account_balance=0;	 
			while(rs.next()) 
			{		
				current_balance=rs.getInt(1);
				new_account_balance=amount_to_deposit+current_balance;
			}
																					
			sql="update Client_Info set Account_Balance="+new_account_balance+" where Account_number="+acc_no;
			check=st.executeUpdate(sql);
			
			if(check!=0)
			{
				to_client="1#";															//Amount Successfully Deposited
				System.out.println("\nAccount number "+ acc_no+" successfully deposited Rs. "+ amount_to_deposit +" into his account");		
			}
			else
			{
				to_client="2#";															//Amount Deposition Unsuccessful	
				System.out.println("\nAccount number "+ acc_no+" request for deposition of Rs. "+ amount_to_deposit +" turned down");	
			}				
			pw.println(to_client);
			
			
		}
		catch(SQLException e)
		{
			System.out.println("\nProblem retrieving data from database");
			//e.printStackTrace();
		}
		catch(ClassNotFoundException e1)
		{
			System.out.println("\nProblem retrieving data from database11111111111111");
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
		
	void perform_withdraw(String temp[])
	{
		to_client="";		
		int check;
		double amount_to_withdraw=Double.parseDouble(temp[1]);
		
		System.out.println("\nAccount number "+ acc_no+" requested to withdraw Rs. "+ amount_to_withdraw +" from his account");			
		
		try
		{
			Class.forName(driver);
			con=DriverManager.getConnection(url,database_username,database_password);
			st=con.createStatement();
			
			sql="select Account_Balance from Client_Info where Account_number="+acc_no;
			rs=st.executeQuery(sql);
			double current_balance,new_account_balance=0;	 
			while(rs.next()) 
			{					
				current_balance=rs.getInt(1);
				new_account_balance=current_balance-amount_to_withdraw;
			}
			if(new_account_balance<500)
			{
				to_client="2#";																//Min balance to be maintained is Rs. 500
				System.out.println("\nAccount number "+ acc_no+" request for withdrawal of Rs. "+ amount_to_withdraw +" turned down as minimum balance of Rs. 500/- should be maintained");
			}
			else
			{
				sql="update Client_Info set Account_Balance="+new_account_balance+" where Account_number="+acc_no;
				check=st.executeUpdate(sql);
				
				if(check!=0)
				{
					to_client="1#";															//Amount Successfully withdrawn
					System.out.println("\nAccount number "+ acc_no+" successfully withdrew Rs. "+ amount_to_withdraw +" from his account");		
				}
				else
				{
					to_client="3#";															//Amount Withdrawal Unsuccessful	
					System.out.println("\nAccount number "+ acc_no+" request for withdrawal of Rs. "+ amount_to_withdraw +" turned down");
				}
			}
			pw.println(to_client);
			
			
		}
		catch(SQLException e)
		{
			System.out.println("\nProblem retrieving data from database");
			//e.printStackTrace();
		}
		catch(ClassNotFoundException e1)
		{
			System.out.println("\nProblem retrieving data from database11111111111111");
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	
	void perform_transfer(String temp[])
	{
		to_client="";		
		int check;
		int no_of_records=0;
		
		double sender_current_balance,sender_new_account_balance=0;	
		double receiver_current_balance,receiver_new_account_balance=0;	
			
		double amount_to_transfer=Double.parseDouble(temp[1]);
		int to_account_no=Integer.parseInt(temp[2]);		
		
		System.out.println("\nAccount number "+ acc_no+" requested to transfer Rs. "+ amount_to_transfer +" from his account into Account number "+to_account_no);			
		
		if(to_account_no==acc_no)
		{
			to_client="4#";																	//Can not transfer into our own account
			System.out.println("\nAccount number "+ acc_no+" request for transfer of Rs. "+ amount_to_transfer +" turned down as transferring amount into self account is not permitted");
			pw.println(to_client);
		}
		else
		{
			try
			{
				Class.forName(driver);															
				con=DriverManager.getConnection(url,database_username,database_password);
				st=con.createStatement();
		
				sql="Select count(*)from Client_Info where Account_number="+to_account_no;						//checking if receiver's account exist
				rs=st.executeQuery(sql);
		
				while(rs.next()) 
					no_of_records = rs.getInt(1);
      	
				if(no_of_records!=0)
				{
					sql="select Account_Balance from Client_Info where Account_number="+acc_no;					//extracting the current balance
					rs=st.executeQuery(sql);
						
					while(rs.next()) 																			//extracting sender's current balance													
					{					
						sender_current_balance=rs.getInt(1);
						sender_new_account_balance=sender_current_balance-amount_to_transfer;
					}
					if(sender_new_account_balance<500)
					{
						to_client="2#";																	//Min balance to be maintained is Rs. 500
						System.out.println("\nAccount number "+ acc_no+" request for transfer of Rs. "+ amount_to_transfer +" turned down as minimum balance of Rs. 500/- should be maintained");
					}
					else
					{
						sql="select Account_Balance from Client_Info where Account_number="+to_account_no;		//extracting the receiver's current balance
						rs=st.executeQuery(sql);				
						while(rs.next()) 																																								
						{					
							receiver_current_balance=rs.getInt(1);
							receiver_new_account_balance=receiver_current_balance+amount_to_transfer;
						}
				
						sql="update Client_Info set Account_Balance="+sender_new_account_balance+" where Account_number="+acc_no;	//Updating sender's balance
						check=st.executeUpdate(sql);
				
						if(check!=0)
						{
							to_client="1#";															//Amount Successfully withdrawn
							System.out.println("\nAccount number "+ acc_no+" successfully transferred Rs. "+ amount_to_transfer +" from his account into Account number"+to_account_no);		
						}
						else
						{
							to_client="3#";															//Amount Withdrawal Unsuccessful	
							System.out.println("\nAccount number "+ acc_no+" request for transfer of Rs. "+ amount_to_transfer +" from his account into Account number"+to_account_no+" turned down");
						}
						sql="update Client_Info set Account_Balance="+receiver_new_account_balance+" where Account_number="+to_account_no;		//Updating receiver's balance
						st.executeUpdate(sql);
					}
				}
				else
				{
					to_client="5#";																	//Receiver's account number not found
					System.out.println("\nAccount number "+ acc_no+" request for transfer of Rs. "+ amount_to_transfer +" turned down as receiver's account does not exist");
				}
				pw.println(to_client);
			}
			catch(SQLException e)
			{
				System.out.println("\nProblem retrieving data from database");
				//e.printStackTrace();
			}
			catch(ClassNotFoundException e1)
			{
				System.out.println("\nProblem retrieving data from database11111111111111");
			}
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	boolean log_out()
	{
		try
		{	
			boolean status=false;
			sql="update Client_Info set Status="+status+" where Account_number="+acc_no;
			int check=st.executeUpdate(sql);
		
			System.out.println("\nAccount Number "+acc_no+" logged out");
			return (true);
		}
		catch(Exception e)
		{
			System.out.println("\nError occured while logging out");
		}
		return (true);
	}
}
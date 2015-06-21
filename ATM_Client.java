/*	Author:Pritam N. Bohra
	Date: 26th Oct,2012
	Program Name: Bank Administrator
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

class Customer
{
	private String name,address,contact_no,email_id;
	private String password;
	private int acc_no;
	private double account_balance,amount;
	private Socket sock=null;
	String server_response="",to_server="";
	
	Scanner in;
	PrintWriter pw;
	
	
	Customer()																	//Default Constructor	
	{
		try
		{
			sock=new Socket(InetAddress.getLocalHost(),1234);
			System.out.println("\nConnection with ATM machine established");
			
			in =new Scanner(sock.getInputStream());
			pw=new PrintWriter(sock.getOutputStream(),true);
		}
		catch(IOException e)
		{
			System.out.println("\nServer not found\nContact Administratior");
			System.exit(1);
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
	void logIn()
	{
		while(true)
		{
			int from_server=-99;			
			
			while(from_server!=1)
			{
				try
				{
					System.out.println("\nPlease Enter Login Details");
			
					System.out.println("\nAccount Number:\t");
					acc_no=Integer.parseInt(Global.br.readLine());
					
					System.out.println("\nPassword:\t");
					password=Global.br.readLine();
				
					to_server="1"+"#"+acc_no+"#"+password+"#";
					pw.println(to_server);
			
					//System.out.println("\nUsername and Password sent to the server");
			
					server_response=in.nextLine();
					String temp[]=server_response.split("#");
					from_server=Integer.parseInt(temp[0]);
							
					if(from_server==1)
					{	
						System.out.println("\nLogin Successfull!!!!");	
						name=temp[1];
						address=temp[2];
						contact_no=temp[3];
						email_id=temp[4];
						account_balance=Double.parseDouble(temp[5]);
					}
					else if(from_server==2)
						System.out.println("\nAccount number and Password do not match\nTry Again");
					else if(from_server==3)
						System.out.println("\nAccount number not found\nTry Again");
					else if(from_server==4)
						System.out.println("\nClient already logged in from different machine");
				}
				catch(NumberFormatException e1)
				{
					System.out.println("\nPleaseenter the account number properly");
				}
				catch(Exception e)
				{
					System.out.println("\nProblem occured while connection with server");
				}
			}
			break;
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	void view_details()
	{
		try
		{
			to_server="2#";
			pw.println(to_server);
				
			server_response=in.nextLine();
			String temp[]=server_response.split("#");
				
			name=temp[1];
			address=temp[2];
			contact_no=temp[3];
			email_id=temp[4];
			account_balance=Double.parseDouble(temp[5]);
		}
		catch(Exception e)
		{
			System.out.println("\nProblem occured while connection with server");
		}
			
		System.out.println("\nAccount Number			:\t"+acc_no);
		System.out.println("\nCustomer Name			:\t"+name);
		System.out.println("\nAddress				:\t"+address);
		System.out.println("\nContact Number			:\t"+contact_no);
		System.out.println("\nEmail ID			:\t"+email_id);
		System.out.println("\nAccount Balance(Rs.)		:\t"+account_balance+"/-");
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
	void deposite() throws IOException
	{	
		int from_server=-99;			
			
		try
		{
			System.out.println("\nPlease enter the amount to be deposited");
			double deposit_amt=Double.parseDouble(Global.br.readLine());
			
			if(deposit_amt<=0)
				throw new OnlyPositiveException();
							
			to_server="3"+"#"+deposit_amt+"#";
			pw.println(to_server);
			
			server_response=in.nextLine();
			String temp[]=server_response.split("#");
			from_server=Integer.parseInt(temp[0]);
						
			if(from_server==1)
				System.out.println("\nAmount Successfully Deposited!!!");	
			else if(from_server==2)
				System.out.println("\nAmount Deposition Unsuccessful!!!");
		}
		catch(NumberFormatException e1)
		{
			System.out.println("\nPlease enter the account number properly");
		}
		catch(OnlyPositiveException e)
		{
			System.out.println(e.getMessage());
		}
		catch(Exception e)
		{
			System.out.println("\nProblem occured while connection with server");
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	void withdraw() throws IOException
	{
		int from_server=-99;			
		
		try
		{
			System.out.println("\nPlease enter the amount to be withdrawn");
			double withdraw_amt=Double.parseDouble(Global.br.readLine());
			
			if(withdraw_amt<=0)
				throw new OnlyPositiveException();
							
			to_server="4"+"#"+withdraw_amt+"#";
			pw.println(to_server);
			
			server_response=in.nextLine();
			String temp[]=server_response.split("#");
			from_server=Integer.parseInt(temp[0]);
						
			if(from_server==1)
				System.out.println("\nAmount Successfully withdrawn!!!");	
			else if(from_server==2)
				System.out.println("\nMin balance to be maintained is Rs. 500");
			else
				System.out.println("\nAmount Withdrawal Unsuccessful!!");
		}
		catch(NumberFormatException e1)
		{
			System.out.println("\nPlease enter the amount properly");
		}
		catch(OnlyPositiveException e)
		{
			System.out.println(e.getMessage());
		}
		catch(Exception e)
		{
			System.out.println("\nProblem occured while connection with server");
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
		
	
	void transfer() throws IOException
	{
		int from_server=-99;			
		
		try
		{
			System.out.println("\nPlease enter the amount to be transferred");
			double transfer_amt=Double.parseDouble(Global.br.readLine());
			
			if(transfer_amt<=0)
				throw new OnlyPositiveException();
								
			System.out.println("\nPlease enter the receiver's account number");
			int to_acc_no=Integer.parseInt(Global.br.readLine());
			
			if(to_acc_no<=0)
				throw new OnlyPositiveException();
			
			to_server="5"+"#"+transfer_amt+"#"+to_acc_no+"#";
			pw.println(to_server);
			
			server_response=in.nextLine();
			String temp[]=server_response.split("#");
			from_server=Integer.parseInt(temp[0]);
						
			if(from_server==1)
				System.out.println("\nAmount Successfully transferred!!!");	
			else if(from_server==2)
				System.out.println("\nMin balance to be maintained is Rs. 500");
			else if(from_server==3)
				System.out.println("\nAmount Transfer Unsuccessful!!");		
			else if(from_server==4)
				System.out.println("\nSender and receiver account can not be same");		
			else
				System.out.println("\nReceiver's account not found!!!");
		}
		catch(NumberFormatException e)
		{
			System.out.println("\nPlease enter the account number properly");
		}
		catch(OnlyPositiveException e)
		{
			System.out.println(e.getMessage());
		}
		catch(Exception e)
		{
			System.out.println("\nProblem occured while connection with server");
		}
			
	}
	
	void close_connection()
	{
		try
		{
			to_server="6#";
			pw.println(to_server);

			in.close();
			pw.close();
			sock.close();
			System.exit(1);
		}
		catch(IOException e)
		{
			System.out.println("\nProblem occured while closing the connection");
		}
	}
}
class ATM_Client
{
	public static void main(String args[]) throws IOException
	{
		
		int acount_no=0,to_account_no=0,n=0;
		boolean found1=false,found=false,chk_n=false;
		
		Customer c=new Customer();
		
		c.logIn();
		while(true)
		{	
			try
			{
				System.out.println("\nSelect appropriate option to do the following\n");
				System.out.println("\n1. Display Details\n2. Deposite Amount\n3. Withdraw Amount\n4. Transfer Amount\n5. Log out");
				n=Integer.parseInt(Global.br.readLine());
			}
			catch(NumberFormatException e)
			{
				System.out.println("\nAccount numbner should be numeric");				
			}
			switch(n)
			{
				case 1:
						c.view_details();
						break;
				case 2:
						c.deposite();
						break;
				case 3:
						c.withdraw(); 
						break;
				case 4:
						c.transfer(); 
						break;
				case 5:
						c.close_connection();
						break;
				default:
						System.out.println("\nPlease select correct option:\t");
						break;
			}
		}
	}
}
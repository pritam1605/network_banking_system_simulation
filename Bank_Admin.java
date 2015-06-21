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

interface Admin_Operations
{
	void create_Account() throws Exception;
	void delete_Account() throws Exception;
	void update_Account() throws Exception;
	void view_Account() throws Exception;
	void logout();
}

class Administrator implements Admin_Operations
{
	private String name,address,contact_no,email_id;
	private String password;
	private int acc_no;
	private double account_balance;
	private ServerSocket ss=null;
	private Socket sock=null;
	private String sql;

	String driver = "sun.jdbc.odbc.JdbcOdbcDriver";															//Database Connectivity																							
	String url = "jdbc:odbc:NetworkBankingSystem";																
	String database_username = "";
	String database_password = "";
	ResultSet rs;
	Statement st;
	Connection con;
	
	public void create_Account() throws Exception
	{
			
			int max_acc_no=0;
			/////////////////////////////////////////////////////////////////////////////////////////////Database Connection
			Class.forName(driver);
			con=DriverManager.getConnection(url,database_username,database_password);
			st=con.createStatement();
			
			sql="Select max(Account_number) from Client_Info";
			rs=st.executeQuery(sql);
			if(rs.next())
				max_acc_no= rs.getInt(1);
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if(max_acc_no!=0)
				acc_no=max_acc_no+1;
			else
				acc_no=1000;
				
			System.out.println("\nEnter account details");
			
			System.out.println("\nAccount Number:\t"+acc_no);
			
			System.out.print("\nCustomer Name:\t");
			name=Global.br.readLine();
			
			System.out.print("\nCustomer Address:\t");
			address=Global.br.readLine();
			
			System.out.print("\nCustomer Contact Number:\t");
			contact_no=Global.br.readLine();
			
			System.out.print("\nCustomer Email ID:\t");
			email_id=Global.br.readLine();
			
			account_balance=5000;
			System.out.print("\nAccount Balance:\tRs. "+account_balance+"/-");
					
			//Random Number Generator	
			Random r=new Random();
			int num=r.nextInt();
			num%=100000;
			num=Math.abs(num);
			password=String.valueOf(num);
		
			for(int i=0;i<5;i++)
			{
				do
				{
					num=r.nextInt();
					num%=100;
					num=Math.abs(num);
				}while((num<65 || num>90));
				char b=(char)num;	
				password+=String.valueOf(b);
			}
						
			System.out.println("\n\nPassword:	\t"+password);
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
						
			sql="Insert Into Client_Info(Account_number,Customer_Name,Address,Contact_Number,Email_ID,Account_Balance,Customer_Password) Values("+acc_no+",'"+name+"','"+address+"','"+contact_no+"','"+email_id+"',"+account_balance+",'"+password+"')";
			int i=st.executeUpdate(sql);
			if(i!=0)
				System.out.println("\nRecord Successfully Entered");
			else
				System.out.println("\nRecord Not Entered");

			st.close();
			con.close();
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	}
	
	public void delete_Account() throws Exception
	{	
		int acc_no,check,no_of_records=0;
		Class.forName(driver);
		con=DriverManager.getConnection(url,database_username,database_password);
		st=con.createStatement();
		
		try
		{
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sql="Select count(*)from Client_Info";														//to check if database contains any records
			rs=st.executeQuery(sql);
			
					 
    		if (rs.next()) 
				no_of_records = rs.getInt(1);
      		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			if(no_of_records==0)
				System.out.println("\nNo record to delete");
			else
			{
				System.out.println("\nEnter the account number to be deleted:\t");
				acc_no=Integer.parseInt(Global.br.readLine());
				sql="Select count(*) from Client_Info where Account_number="+acc_no;					//checking if the account number existss
				rs=st.executeQuery(sql);
				no_of_records=0;				 
				if (rs.next()) 
					no_of_records = rs.getInt(1);
				if(no_of_records==1)
				{
					sql="Delete from Client_Info where Account_number="+acc_no;
					check=st.executeUpdate(sql);
					if(check>0)
						System.out.println("\nAccount Successfully Deleted!!!");
					else
						System.out.println("\nAccount Not Deleted!!!");
				}
				else
					System.out.println("\nAccount Number does not exist");
			}
			st.close();
			con.close();
		}
		catch(Exception e)
		{
			System.out.println("\nException Occured\nAccount Not Deleted!!!");
		}
	}
	
	public void update_Account() throws Exception
	{
		int acc_no,check,no_of_records=0;
		Class.forName(driver);
		con=DriverManager.getConnection(url,database_username,database_password);
		st=con.createStatement();
		
		try
		{
			System.out.println("\n----------------------------------------------ENTER NEW ACCOUNT DETAILS----------------------------------------------");
			
			System.out.println("\nEnter the account number to be updated:\t");
			acc_no=Integer.parseInt(Global.br.readLine());
			
			sql="Select count(*) from Client_Info where Account_number="+acc_no;					//checking if the account number existss
			rs=st.executeQuery(sql);
			no_of_records=0;				 
			if (rs.next()) 
				no_of_records = rs.getInt(1);
			if(no_of_records==1)
			{
				System.out.println("\nAccount Number:\t"+acc_no);
				System.out.print("\nCustomer Name:\t");
				name=Global.br.readLine();			
				System.out.print("\nCustomer Address:\t");
				address=Global.br.readLine();			
				System.out.print("\nCustomer Contact Number:\t");
				contact_no=Global.br.readLine();			
				System.out.print("\nCustomer Email ID:\t");
				email_id=Global.br.readLine();	

				sql="Update Client_Info set Customer_Name='"+name+"',Address='"+address+"',Contact_Number='"+contact_no+"',Email_ID='"+email_id+"' where Account_number="+acc_no;
				check=st.executeUpdate(sql);
				if(check>0)
					System.out.println("\nAccount Successfully Updated!!!");
				else
					System.out.println("\nAccount Not Updated!!!");
			}
			else
					System.out.println("\nAccount Number does not exist");
					
			st.close();
			con.close();
		}
		catch(Exception e)
		{
			System.out.println("\nException Occured\nAccount Not Updated!!!");
		}
	}
			
	public void view_Account() throws Exception
	{	
		int acc_no,check,flag=0,count=0,no_of_records;
		Class.forName(driver);
		con=DriverManager.getConnection(url,database_username,database_password);
		st=con.createStatement();
			
		System.out.println("\nPress 1 to view individual record\nPress 2 to view all records");
		check=Integer.parseInt(Global.br.readLine());
		
		try
		{
			switch(check)
			{
				case 1:
						System.out.println("\nEnter the account number to be viewed:\t");
						acc_no=Integer.parseInt(Global.br.readLine());
						sql="Select count(*) from Client_Info where Account_number="+acc_no;					//checking if the account number existss
						rs=st.executeQuery(sql);
						no_of_records=0;				 
						if (rs.next()) 
							no_of_records = rs.getInt(1);
						if(no_of_records==1)
						{
							sql="Select * from Client_Info where Account_number="+acc_no;
							rs=st.executeQuery(sql);
							while(rs.next())
							{
								System.out.println("\n---------------------------------------------------------------------------------");
								System.out.println("\nAccount Number		:\t"+rs.getInt(1));
								System.out.println("\nCustomer Name		:\t"+rs.getString(2));
								System.out.println("\nAddress			:\t"+rs.getString(3));
								System.out.println("\nContact Number		:\t"+rs.getString(4));
								System.out.println("\nEmail ID		:\t"+rs.getString(5));
								System.out.println("\nAccount Balance(Rs.)	:\t"+rs.getInt(6)+"/-");
								System.out.println("\n---------------------------------------------------------------------------------");
							}
						}
						else
						System.out.println("\nAccount Number does not exist");
						break;
				case 2:
						sql="select * from Client_Info";
						rs=st.executeQuery(sql);
									
						System.out.println("\n----------------------------------------------CLIENT ACCOUNT DETAILS----------------------------------------------");
		
						while(rs.next())
						{
							flag=1;
							System.out.println("\n---------------------------------------------------------------------------------");
							System.out.println("\nAccount Number		:\t"+rs.getInt(1));
							System.out.println("\nCustomer Name		:\t"+rs.getString(2));
							System.out.println("\nAddress			:\t"+rs.getString(3));
							System.out.println("\nContact Number		:\t"+rs.getString(4));
							System.out.println("\nEmail ID		:\t"+rs.getString(5));
							System.out.println("\nAccount Balance(Rs.)	:\t"+rs.getInt(6)+"/-");
							System.out.println("\n---------------------------------------------------------------------------------");
							count++;
						}
						if(flag==0)
							System.out.println("NO Records");
						System.out.println("\n"+count+" records displayed");
									
						break;
				default:
						System.out.println("\nPlease select proper option");
			}
		}
		catch(NumberFormatException e1)
		{
				System.out.println("\nSelect appropritae option");
		}
	}
		
	public void logout() 
	{
		try
		{	
			/*rs.close();
			st.close();
			con.close();*/
			System.out.println("\nAdmin Logged Out");
		}
		catch(Exception e)
		{
			System.out.println("\nProblem occured can not log out");
		}
	}
}
	
	
class Bank_Admin
{
	static void admin_login() throws IOException
	{	
		String user_name,password;
		while(true)																			
		{
			System.out.println("\nPlease provide ADMIN's 'Username' and 'Password'");
			System.out.println("\nUsername:\t");
			user_name=Global.br.readLine();
					
			System.out.println("\nPassword:\t");
			password=Global.br.readLine();
			
			if((user_name.equals("Admin")) && (password.equals("Admin")))
			{
				System.out.println("\nLogin Successfully for ADMIN");
				break;
			}
			else
				System.out.println("\nUseraname/Password don't match\nTry again\n");
		}
	}
	
	public static void main(String args[]) throws Exception
	{
		int n;
		boolean show_menu=true;
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		Administrator admin=new Administrator();
		
		while(true)
		{
			admin_login();														// Admin Login
			show_menu=true;			
			while(show_menu)
			{	
				
				try														//Providing the Administrator with finctions
				{
					System.out.println("\nSelect an appropritae option to do the following\n1.\tCreate Account\n2.\tDelete Account\n3.\tUpdate Account\n4.\tView Account\n5.\tLog out\n");
					n=Integer.parseInt(br.readLine());
		
					switch(n)
					{
						case 1:
								admin.create_Account();
								break;
						case 2:
								admin.delete_Account();
								break;
						case 3:
								admin.update_Account();
								break;
						case 4:
								admin.view_Account() ;
								break;
						case 5:
								admin.logout() ;
								show_menu=false;
								break;
						default:
							System.out.println("\nWrong option selected!!!!!");
					}
				}
				catch(NumberFormatException e)
				{
					System.out.println("\nPlease select proper number");
				}
			}
		}
	}
}
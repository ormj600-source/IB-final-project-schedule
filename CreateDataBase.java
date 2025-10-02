import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.*;
import javax.swing.*;

public class CreateDataBase {
    private Connection con;
    private Statement st;
    private ResultSet rs;
    
    // constructor method
    
     public CreateDataBase()
     {
    try{
        
        Class.forName("com.mysql.jdbc.Driver");
        con=DriverManager.getConnection("jdbc:mysql://localhost:3306/Schedule6","root","");
        st =con.createStatement();
         }catch(ClassNotFoundException | SQLException ex)
         {
            
            JOptionPane.showMessageDialog(null,"error:"+ex);
            
            }
  }
  // for is hard, very hard, hard, average, easy, very easy
  public void insertData( String HigherLevel,
String StandardLevel ,
String TestUpcoming ,
int CurrentGrade ,
int GradeWanted ,
int PriorityNum,
String IsHard, double TimeToStudy )
{
    String query;
 try{
    query="insert into ScheduleValue1(HigherLevel,StandardLevel,TestUpcoming,CurrentGrade ,GradeWanted,IsHard,TimeToStudy,PriorityNum) value("+StandardLevel+",'"+StandardLevel+",'"+TestUpcoming+",'"+CurrentGrade+",'"+GradeWanted+",'"+IsHard+",'"+TimeToStudy+",'"+PriorityNum+")";
    st.executeUpdate(query);
     } catch(SQLException ex)  
     {
       JOptionPane.showMessageDialog(null,"error:"+ex); 
        }


}
  
  
  
  
  public static void main(String args[])
   {
    
     CreateDataBase cdb = new CreateDataBase();  
     //cdb.insertData()  
       
    }
    

}

/*higher level
standard level
test upcoming
current grade
is it hard

priority_num


 public static void main(String[] args)
  {
    try
    {
        for reference
      // create a mysql database connection

      Class.forName(myDriver);
      Connection conn = DriverManager.getConnection(myUrl, "root", "");

      // create a sql date object so we can use it in our INSERT statement
      Calendar calendar = Calendar.getInstance();
      java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());

      // the mysql insert statement
      String query = " insert into users (first_name, last_name, date_created, is_admin, num_points)"
        + " values (?, ?, ?, ?, ?)";

      // create the mysql insert preparedstatement
      PreparedStatement preparedStmt = conn.prepareStatement(query);
      preparedStmt.setString (1, "Barney");
      preparedStmt.setString (2, "Rubble");
      preparedStmt.setDate   (3, startDate);
      preparedStmt.setBoolean(4, false);
      preparedStmt.setInt    (5, 5000);

      // execute the preparedstatement
      preparedStmt.execute();

      conn.close();
    }
    catch (Exception e)
    {
      System.err.println("Got an exception!");
      System.err.println(e.getMessage());
    }
  }
}

*/


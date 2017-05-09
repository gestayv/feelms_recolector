package cl.feelms.twitter.streaming;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class KeywordRetrieval {

public void conn()
{
    try {
        //  Apertura de words.dat
        String ruta = "./src/main/resources/words.dat";
        BufferedWriter bw = new BufferedWriter(new FileWriter(ruta));
        
        //  Conexión a SQL
        Class.forName("com.mysql.cj.jdbc.Driver");
        String urlConn = "jdbc:mysql://localhost:3306/feelms_sql?serverTimezone=UTC";
        String usr = "root", pwd = "root";
        String query = "SELECT term FROM key_terms";
        Connection con = DriverManager.getConnection(urlConn, usr, pwd);
        Statement stmt = con.createStatement();
        ResultSet rset = stmt.executeQuery(query);
        
        //  Se itera sobre los resultados y se imprimen en words.dat
        while(rset.next())
        {
            bw.write(rset.getString(1));
            bw.newLine();
        }
        
        //  Se cierra el archivo y la conexión a la bd respectivamente.
        bw.close();
        con.close();
    } catch (Exception e) {
        System.out.println(e);
    }
    
}    

}

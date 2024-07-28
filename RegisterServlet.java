import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/expense_tracker";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "admin"; // Replace with your MySQL password

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);

            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            int rowsInserted = stmt.executeUpdate();

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><head><link rel='stylesheet' type='text/css' href='styles.css'></head><body>");
            out.println("<div class='container'>");
            if (rowsInserted > 0) {
                out.println("<h2 class='success'>User registered successfully!</h2>");
            } else {
                out.println("<h2 class='error'>Error occurred while registering user. Please try again.</h2>");
            }
            out.println("<a href='register.html'>Go back</a>");
            out.println("</div>");
            out.println("</body></html>");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><head><link rel='stylesheet' type='text/css' href='styles.css'></head><body>");
            out.println("<div class='container'>");
            out.println("<h2 class='error'>Error occurred: " + e.getMessage() + "</h2>");
            out.println("<a href='register.html'>Go back</a>");
            out.println("</div>");
            out.println("</body></html>");
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

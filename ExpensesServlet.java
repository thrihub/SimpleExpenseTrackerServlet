import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ExpensesServlet")
public class ExpensesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/expense_tracker";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "admin"; // Replace with your MySQL password

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            response.sendRedirect("login.html");
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);

            String sql = "SELECT e.description, e.amount, e.date FROM expenses e " +
                         "JOIN users u ON e.user_id = u.user_id " +
                         "WHERE u.username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Expenses Tracker</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f2f2f2; margin: 0; padding: 0; }");
            out.println(".container { width: 50%; margin: 0 auto; background-color: #ffffff; padding: 20px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); margin-top: 50px; }");
            out.println("h2 { text-align: center; color: #333333; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("table, th, td { border: 1px solid #dddddd; }");
            out.println("th, td { padding: 8px; text-align: left; }");
            out.println("th { background-color: #007BFF; color: white; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<h2>Your Expenses</h2>");
            out.println("<table>");
            out.println("<tr><th>Description</th><th>Amount</th><th>Date</th></tr>");

            while (rs.next()) {
                String description = rs.getString("description");
                double amount = rs.getDouble("amount");
                String date = rs.getDate("date").toString();
                out.println("<tr><td>" + description + "</td><td>" + amount + "</td><td>" + date + "</td></tr>");
            }

            out.println("</table>");
            out.println("<br>");
            out.println("<form method='post' action='ExpensesServlet'>");
            out.println("Description: <input type='text' name='description' required><br><br>");
            out.println("Amount: <input type='number' step='0.01' name='amount' required><br><br>");
            out.println("Date: <input type='date' name='date' required><br><br>");
            out.println("<input type='submit' value='Add Expense'>");
            out.println("</form>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Error occurred: " + e.getMessage() + "</h2>");
            out.println("<a href='login.html'>Go back</a>");
            out.println("</body></html>");
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) {
            response.sendRedirect("login.html");
            return;
        }

        String description = request.getParameter("description");
        double amount = Double.parseDouble(request.getParameter("amount"));
        String date = request.getParameter("date");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);

            String sql = "SELECT user_id FROM users WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            int userId = 0;
            if (rs.next()) {
                userId = rs.getInt("user_id");
            }

            sql = "INSERT INTO expenses (user_id, description, amount, date) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, description);
            stmt.setDouble(3, amount);
            stmt.setDate(4, java.sql.Date.valueOf(date));
            stmt.executeUpdate();

            response.sendRedirect("ExpensesServlet");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Error occurred: " + e.getMessage() + "</h2>");
            out.println("<a href='login.html'>Go back</a>");
            out.println("</body></html>");
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

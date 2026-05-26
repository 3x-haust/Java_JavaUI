package examples.login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import io.github._3xhaust.core.JavaUIApp;
import io.github._3xhaust.core.Navigator;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.EdgeInsets;
import io.github._3xhaust.dsl.widgets.DialogWidget;
import io.github._3xhaust.dsl.widgets.SnackbarWidget;
import io.github._3xhaust.state.State;
import io.github._3xhaust.theme.ThemeData;
import io.github._3xhaust.util.Logger;

import static io.github._3xhaust.dsl.widgets.AppBarWidget.AppBar;
import static io.github._3xhaust.dsl.widgets.ButtonWidget.Button;
import static io.github._3xhaust.dsl.widgets.CardWidget.Card;
import static io.github._3xhaust.dsl.widgets.CenterWidget.Center;
import static io.github._3xhaust.dsl.widgets.ColumnWidget.Column;
import static io.github._3xhaust.dsl.widgets.FormFieldWidget.FormField;
import static io.github._3xhaust.dsl.widgets.InputWidget.Input;
import static io.github._3xhaust.dsl.widgets.PaddingWidget.Padding;
import static io.github._3xhaust.dsl.widgets.RowWidget.Row;
import static io.github._3xhaust.dsl.widgets.ScaffoldWidget.Scaffold;
import static io.github._3xhaust.dsl.widgets.SizedBoxWidget.SizedBox;
import static io.github._3xhaust.dsl.widgets.TextWidget.Text;

/**
 * Login example backed by a PostgreSQL database. Override the JDBC URL/credentials
 * via {@code PG_URL}, {@code PG_USER}, {@code PG_PASS} environment variables.
 *
 * <p>The example uses the new builder-style routing API and demonstrates
 * {@link FormField}-based validation, dialogs and snackbars.</p>
 */
public class Main {
    private static final Logger LOG = Logger.get(Main.class);
    private static String currentUser;

    static class DB {
        static String url() { return getenvOr("PG_URL", "jdbc:postgresql://localhost:5432/javaui"); }
        static String user() { return getenvOr("PG_USER", "postgres"); }
        static String pass() { return getenvOr("PG_PASS", "postgres"); }

        static String getenvOr(String k, String d) {
            String v = System.getenv(k);
            return v != null && !v.isBlank() ? v : d;
        }

        static Connection conn() throws SQLException {
            return DriverManager.getConnection(url(), user(), pass());
        }

        static void init() {
            try (Connection c = conn(); Statement st = c.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                        "id SERIAL PRIMARY KEY, " +
                        "username VARCHAR(255) UNIQUE NOT NULL, " +
                        "password VARCHAR(255) NOT NULL" +
                        ")");
            } catch (SQLException e) {
                LOG.warn("DB init failed (is PostgreSQL running?): " + e.getMessage());
            }
        }

        static boolean register(String username, String password) {
            String sql = "INSERT INTO users(username, password) VALUES (?, ?)";
            try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                LOG.warn("register failed: " + e.getMessage());
                return false;
            }
        }

        static boolean login(String username, String password) {
            String sql = "SELECT 1 FROM users WHERE username = ? AND password = ?";
            try (Connection c = conn(); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, password);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException e) {
                LOG.warn("login failed: " + e.getMessage());
                return false;
            }
        }
    }

    public static View loginPage() {
        State<String> username = State.of("");
        State<String> password = State.of("");

        Runnable submit = () -> {
            if (DB.login(username.get(), password.get())) {
                currentUser = username.get();
                SnackbarWidget.show("Welcome back, " + currentUser + "!", SnackbarWidget.Type.SUCCESS);
                Navigator.to("/home");
            } else {
                DialogWidget.showAlert("Login failed", "Wrong username or password.");
            }
        };

        return Scaffold()
                .appBar(AppBar("Login"))
                .body(Center(Card(Padding(EdgeInsets.all(20),
                        Column(
                                Text("Sign in to JavaUI").bold().fontSize(20),
                                SizedBox(0, 12),
                                FormField("Username", Input(username).columns(20).placeholder("alice")),
                                SizedBox(0, 8),
                                FormField("Password", Input(password).password(true).columns(20).placeholder("•••••")),
                                SizedBox(0, 16),
                                Row(
                                        Button("Sign in", submit),
                                        Button("Register", () -> Navigator.to("/register")).text()
                                ).gap(8)
                        ).gap(0)))));
    }

    public static View registerPage() {
        State<String> username = State.of("");
        State<String> password = State.of("");

        Runnable submit = () -> {
            if (username.get().isBlank() || password.get().isBlank()) {
                DialogWidget.showAlert("Validation", "Please fill in both fields.");
                return;
            }
            if (DB.register(username.get(), password.get())) {
                SnackbarWidget.show("Account created.", SnackbarWidget.Type.SUCCESS);
                Navigator.to("/");
            } else {
                DialogWidget.showAlert("Registration failed", "That username may already be taken.");
            }
        };

        return Scaffold()
                .appBar(AppBar("Register"))
                .body(Center(Card(Padding(EdgeInsets.all(20),
                        Column(
                                Text("Create an account").bold().fontSize(20),
                                SizedBox(0, 12),
                                FormField("Username", Input(username).columns(20)),
                                SizedBox(0, 8),
                                FormField("Password", Input(password).password(true).columns(20)),
                                SizedBox(0, 16),
                                Row(
                                        Button("Create", submit),
                                        Button("Back", () -> Navigator.to("/")).text()
                                ).gap(8)
                        ).gap(0)))));
    }

    public static View homePage() {
        String user = currentUser != null ? currentUser : "guest";
        return Scaffold()
                .appBar(AppBar("Home"))
                .body(Center(
                        Column(
                                Text("Welcome, " + user + "!").bold().fontSize(20),
                                SizedBox(0, 16),
                                Button("Sign out", () -> { currentUser = null; Navigator.to("/"); }).outlined()
                        ).gap(0)));
    }

    public static void main(String[] args) {
        DB.init();
        new JavaUIApp.Builder()
                .title("JavaUI Login")
                .size(520, 480)
                .theme(ThemeData.light())
                .route("/", Main::loginPage)
                .route("/register", Main::registerPage)
                .route("/home", Main::homePage)
                .build()
                .run();
    }
}

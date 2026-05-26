package examples.hello_world;

import io.github._3xhaust.core.JavaUIApp;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.widgets.DialogWidget;
import io.github._3xhaust.theme.ThemeData;

import static io.github._3xhaust.dsl.widgets.AppBarWidget.AppBar;
import static io.github._3xhaust.dsl.widgets.ButtonWidget.Button;
import static io.github._3xhaust.dsl.widgets.CenterWidget.Center;
import static io.github._3xhaust.dsl.widgets.ColumnWidget.Column;
import static io.github._3xhaust.dsl.widgets.ScaffoldWidget.Scaffold;
import static io.github._3xhaust.dsl.widgets.SizedBoxWidget.SizedBox;
import static io.github._3xhaust.dsl.widgets.TextWidget.Text;

public class Main {
    public static void main(String[] args) {
        new JavaUIApp.Builder()
                .title("Hello, JavaUI")
                .size(420, 320)
                .theme(ThemeData.light())
                .build()
                .run(home());
    }

    private static View home() {
        return Scaffold()
                .appBar(AppBar("Hello World"))
                .body(Center(
                        Column(
                                Text("Welcome to JavaUI 0.2").fontSize(20).bold(),
                                SizedBox(0, 12),
                                Text("Declarative UI for Java desktop apps."),
                                SizedBox(0, 24),
                                Button("Show dialog", () ->
                                        DialogWidget.showAlert("Hi!", "Hello from JavaUI 👋"))
                        ).gap(8)
                ));
    }
}

package examples.testapp;

import io.github._3xhaust.core.JavaUIApp;
import io.github._3xhaust.core.Navigator;
import io.github._3xhaust.core.View;
import io.github._3xhaust.tools.AssetGenerator;

import java.awt.*;
import java.util.Map;

import static io.github._3xhaust.dsl.widgets.AppBarWidget.AppBar;
import static io.github._3xhaust.dsl.widgets.ButtonWidget.Button;
import static io.github._3xhaust.dsl.widgets.CenterWidget.Center;
import static io.github._3xhaust.dsl.widgets.ColumnWidget.Column;
import static io.github._3xhaust.dsl.widgets.ImageWidget.Image;
import static io.github._3xhaust.dsl.widgets.ScaffoldWidget.Scaffold;
import static io.github._3xhaust.dsl.widgets.SingleChildScrollViewWidget.SingleChildScrollView;
import static io.github._3xhaust.dsl.widgets.TextWidget.Text;

public class Main {

    public static View HomePage() {
        return Scaffold()
            .appBar(AppBar(Text("Home").color(Color.BLACK)))
            .body(
                Column(
                    Button("Go to About Page", () -> Navigator.to("/about")).color(Color.BLUE),
                    Button("Go to Image Page", () -> Navigator.to("/image")).color(Color.RED)
                ).gap(10)
            );
    }

    public static View AboutPage() {
        return Scaffold()
            .appBar(AppBar(Text("About").color(Color.BLACK)))
            .body(
                Center(
                    Button("Go to Home Page", () -> Navigator.to("/")).color(Color.BLUE)
                )
            );
    }

    public static View ImagePage() {
        return Scaffold()
            .appBar(AppBar(Text("Images")))
            .body(
                SingleChildScrollView(
                    Column(
                        Text("Local Asset Image:"),
                        Image(Assets.google_svg).width(150).height(150),
                        Text("Local SVG Image:"),
                        Image(Assets.content_png).width(100).height(100),
                        Text("Remote Image:"),
                        Image("https://picsum.photos/150/150").width(150).height(150),
                        Button("Back to Home", () -> Navigator.to("/")).color(Color.BLUE)
                    ).gap(20)
                )
            );
    }

    public static void main(String[] args) {
        AssetGenerator.generate(Main.class);

        new JavaUIApp(
            "/",
            Map.of(
                "/", Main::HomePage,
                "/about", Main::AboutPage,
                "/image", Main::ImagePage
            )
        ).run();
    }
}

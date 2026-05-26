package examples.counter;

import io.github._3xhaust.core.JavaUIApp;
import io.github._3xhaust.core.StatefulWidget;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.enums.MainAxisAlignment;
import io.github._3xhaust.state.State;
import io.github._3xhaust.theme.ThemeData;

import static io.github._3xhaust.dsl.widgets.AppBarWidget.AppBar;
import static io.github._3xhaust.dsl.widgets.ButtonWidget.Button;
import static io.github._3xhaust.dsl.widgets.CenterWidget.Center;
import static io.github._3xhaust.dsl.widgets.ColumnWidget.Column;
import static io.github._3xhaust.dsl.widgets.FloatingActionButtonWidget.FloatingActionButton;
import static io.github._3xhaust.dsl.widgets.RowWidget.Row;
import static io.github._3xhaust.dsl.widgets.ScaffoldWidget.Scaffold;
import static io.github._3xhaust.dsl.widgets.SizedBoxWidget.SizedBox;
import static io.github._3xhaust.dsl.widgets.TextWidget.Text;

/**
 * Classic Flutter-style counter demo - showcases automatic rebuilds on state change.
 */
public class Main {
    public static void main(String[] args) {
        new JavaUIApp.Builder()
                .title("Counter")
                .size(380, 280)
                .theme(ThemeData.light())
                .build()
                .run(new CounterPage());
    }

    static final class CounterPage extends StatefulWidget {
        private final State<Integer> count = State.of(0);

        @Override
        public void initState() {
            watch(count);
        }

        @Override
        public View build() {
            return Scaffold()
                    .appBar(AppBar("Counter"))
                    .body(Center(
                            Column(
                                    Text("Count").color(150, 150, 150),
                                    Text(String.valueOf(count.get())).fontSize(64).bold(),
                                    SizedBox(0, 24),
                                    Row(
                                            Button("− 1", () -> count.update(v -> v - 1)).outlined(),
                                            Button("Reset", () -> count.set(0)).text(),
                                            Button("+ 1", () -> count.update(v -> v + 1))
                                    ).gap(8).mainAxisAlignment(MainAxisAlignment.CENTER)
                            ).gap(4)
                    ))
                    .floatingActionButton(FloatingActionButton("+",
                            () -> count.update(v -> v + 1)));
        }
    }
}

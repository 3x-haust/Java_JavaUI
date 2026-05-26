package examples.hello_world;

import io.github._3xhaust.core.App;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.enums.CrossAxisAlignment;
import io.github._3xhaust.dsl.enums.MainAxisAlignment;
import io.github._3xhaust.state.State;

import static io.github._3xhaust.dsl.Layouts.*;
import static io.github._3xhaust.dsl.Widgets.*;

public class Main {
    public static View MyApp() {
        var count = State.of(0);

        return //Center(
                Column(
                        Text("Hello, Declarative World!"),
                        Label("Counter Application"),
                        Row(
                                Button("-", () -> count.update(v -> Math.max(0, v - 1))),
                                Text(count),
                                Button("+", () -> count.set(count.get() + 1))
                        )
                ).mainAxisAlignment(MainAxisAlignment.START)
                        .crossAxisAlignment(CrossAxisAlignment.START);
//        );
    }

    public static void main(String[] args) {
        App.run(MyApp());
    }
}

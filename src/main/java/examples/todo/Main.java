package examples.todo;

import java.util.ArrayList;
import java.util.List;

import io.github._3xhaust.core.JavaUIApp;
import io.github._3xhaust.core.StatefulWidget;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.EdgeInsets;
import io.github._3xhaust.dsl.widgets.Icons;
import io.github._3xhaust.state.State;
import io.github._3xhaust.theme.Colors;
import io.github._3xhaust.theme.ThemeData;

import static io.github._3xhaust.dsl.widgets.AppBarWidget.AppBar;
import static io.github._3xhaust.dsl.widgets.ButtonWidget.Button;
import static io.github._3xhaust.dsl.widgets.CardWidget.Card;
import static io.github._3xhaust.dsl.widgets.CheckboxWidget.Checkbox;
import static io.github._3xhaust.dsl.widgets.ColumnWidget.Column;
import static io.github._3xhaust.dsl.widgets.ContainerWidget.Container;
import static io.github._3xhaust.dsl.widgets.IconButtonWidget.IconButton;
import static io.github._3xhaust.dsl.widgets.InputWidget.Input;
import static io.github._3xhaust.dsl.widgets.PaddingWidget.Padding;
import static io.github._3xhaust.dsl.widgets.RowWidget.Row;
import static io.github._3xhaust.dsl.widgets.ScaffoldWidget.Scaffold;
import static io.github._3xhaust.dsl.widgets.SingleChildScrollViewWidget.SingleChildScrollView;
import static io.github._3xhaust.dsl.widgets.SizedBoxWidget.SizedBox;
import static io.github._3xhaust.dsl.widgets.TextWidget.Text;

public class Main {
    public static void main(String[] args) {
        new JavaUIApp.Builder()
                .title("Todo")
                .size(520, 540)
                .theme(ThemeData.light())
                .build()
                .run(new TodoPage());
    }

    static final class TodoItem {
        final String text;
        boolean done;
        TodoItem(String text) { this.text = text; }
    }

    static final class TodoPage extends StatefulWidget {
        private final State<List<TodoItem>> todos = State.of(new ArrayList<>(List.of(
                seed("Read the JavaUI README", true),
                seed("Try the counter example", false),
                seed("Write a real app", false))));
        private final State<String> draft = State.of("");

        @Override
        public void initState() {
            watch(todos);
            watch(draft);
        }

        private static TodoItem seed(String t, boolean done) {
            TodoItem i = new TodoItem(t);
            i.done = done;
            return i;
        }

        private void addTodo() {
            String t = draft.get();
            if (t == null || t.isBlank()) return;
            todos.mutate(list -> list.add(new TodoItem(t.trim())));
            draft.set("");
        }

        private void remove(TodoItem item) {
            todos.mutate(list -> list.remove(item));
        }

        @Override
        public View build() {
            List<View> rows = new ArrayList<>();
            List<TodoItem> list = todos.get();
            for (int i = 0; i < list.size(); i++) {
                TodoItem item = list.get(i);
                State<Boolean> doneState = State.of(item.done);
                doneState.subscribe(v -> {
                    item.done = v;
                    todos.mutate(l -> { /* trigger listeners */ });
                });
                rows.add(Card(
                        Row(
                                Checkbox(doneState),
                                SizedBox(8, 0),
                                Text(item.text).color(item.done ? Colors.Grey500 : Colors.Grey900),
                                IconButton(Icons.TRASH, () -> remove(item)).size(28)
                        ).gap(6)
                ).elevation(0).padding(EdgeInsets.symmetric(6, 12)));
            }
            if (rows.isEmpty()) {
                rows.add(Padding(20, Text("Nothing to do — add one below.").color(Colors.Grey500)));
            }

            return Scaffold()
                    .appBar(AppBar("Todo (" + list.stream().filter(t -> !t.done).count() + ")"))
                    .body(Padding(EdgeInsets.all(16),
                            Column(
                                    Container()
                                            .child(SingleChildScrollView(Column(rows.toArray(View[]::new)).gap(8)))
                                            .height(360),
                                    SizedBox(0, 12),
                                    Row(
                                            Container()
                                                    .child(Input(draft).placeholder("What needs doing?").columns(24).onSubmit(this::addTodo))
                                                    .width(360),
                                            SizedBox(8, 0),
                                            Button("Add", this::addTodo)
                                    ).gap(0)
                            ).gap(0)));
        }
    }
}

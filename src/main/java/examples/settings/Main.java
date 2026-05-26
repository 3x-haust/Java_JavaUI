package examples.settings;

import io.github._3xhaust.core.JavaUIApp;
import io.github._3xhaust.core.StatefulWidget;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.EdgeInsets;
import io.github._3xhaust.dsl.widgets.Icons;
import io.github._3xhaust.state.State;
import io.github._3xhaust.theme.Theme;
import io.github._3xhaust.theme.ThemeData;
import io.github._3xhaust.theme.ThemeMode;

import static io.github._3xhaust.dsl.widgets.AppBarWidget.AppBar;
import static io.github._3xhaust.dsl.widgets.CardWidget.Card;
import static io.github._3xhaust.dsl.widgets.ColumnWidget.Column;
import static io.github._3xhaust.dsl.widgets.DividerWidget.Divider;
import static io.github._3xhaust.dsl.widgets.IconWidget.Icon;
import static io.github._3xhaust.dsl.widgets.PaddingWidget.Padding;
import static io.github._3xhaust.dsl.widgets.RowWidget.Row;
import static io.github._3xhaust.dsl.widgets.ScaffoldWidget.Scaffold;
import static io.github._3xhaust.dsl.widgets.SizedBoxWidget.SizedBox;
import static io.github._3xhaust.dsl.widgets.SwitchWidget.Switch;
import static io.github._3xhaust.dsl.widgets.TextWidget.Text;

/**
 * Demonstrates global theme switching and reactive UI updates.
 */
public class Main {
    public static void main(String[] args) {
        new JavaUIApp.Builder()
                .title("Settings")
                .size(540, 480)
                .theme(ThemeData.light())
                .build()
                .run(new SettingsPage());
    }

    static final class SettingsPage extends StatefulWidget {
        private final State<Boolean> dark = State.of(Theme.get().mode() == ThemeMode.DARK);
        private final State<Boolean> notifications = State.of(true);
        private final State<Boolean> sounds = State.of(false);

        @Override
        public void initState() {
            watch(dark);
            watch(notifications);
            watch(sounds);
            dark.subscribe(v -> Theme.set(v ? ThemeData.dark() : ThemeData.light()));
        }

        @Override
        public View build() {
            return Scaffold()
                    .appBar(AppBar("Settings"))
                    .body(Padding(EdgeInsets.all(20), Column(
                            Text("Appearance").bold().fontSize(14),
                            SizedBox(0, 8),
                            Card(Padding(EdgeInsets.symmetric(8, 16), Column(
                                    settingRow(Icons.MOON, "Dark mode", Switch(dark)),
                                    Divider(),
                                    settingRow(Icons.STAR, "Use Material 3 colors", Switch(State.of(true)))
                            ).gap(0))).elevation(1),

                            SizedBox(0, 20),
                            Text("Notifications").bold().fontSize(14),
                            SizedBox(0, 8),
                            Card(Padding(EdgeInsets.symmetric(8, 16), Column(
                                    settingRow(Icons.BELL, "Push notifications", Switch(notifications)),
                                    Divider(),
                                    settingRow(Icons.SUN, "Play sounds", Switch(sounds))
                            ).gap(0))).elevation(1)
                    ).gap(0)));
        }

        private View settingRow(String icon, String label, View control) {
            return Padding(EdgeInsets.symmetric(10, 0),
                    Row(
                            Icon(icon).size(18),
                            SizedBox(12, 0),
                            Text(label),
                            SizedBox(0, 0),
                            control
                    ).gap(0)
                            .mainAxisAlignment(io.github._3xhaust.dsl.enums.MainAxisAlignment.SPACE_BETWEEN));
        }
    }
}

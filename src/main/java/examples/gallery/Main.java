package examples.gallery;

import io.github._3xhaust.core.JavaUIApp;
import io.github._3xhaust.core.StatefulWidget;
import io.github._3xhaust.core.View;
import io.github._3xhaust.dsl.EdgeInsets;
import io.github._3xhaust.dsl.widgets.Icons;
import io.github._3xhaust.state.State;
import io.github._3xhaust.theme.Colors;
import io.github._3xhaust.theme.ThemeData;

import static io.github._3xhaust.dsl.widgets.AppBarWidget.AppBar;
import static io.github._3xhaust.dsl.widgets.AvatarWidget.Avatar;
import static io.github._3xhaust.dsl.widgets.BadgeWidget.Badge;
import static io.github._3xhaust.dsl.widgets.ButtonWidget.Button;
import static io.github._3xhaust.dsl.widgets.CardWidget.Card;
import static io.github._3xhaust.dsl.widgets.CheckboxWidget.Checkbox;
import static io.github._3xhaust.dsl.widgets.ChipWidget.Chip;
import static io.github._3xhaust.dsl.widgets.CircularProgressWidget.CircularProgress;
import static io.github._3xhaust.dsl.widgets.ColumnWidget.Column;
import static io.github._3xhaust.dsl.widgets.DividerWidget.Divider;
import static io.github._3xhaust.dsl.widgets.DropdownWidget.Dropdown;
import static io.github._3xhaust.dsl.widgets.IconButtonWidget.IconButton;
import static io.github._3xhaust.dsl.widgets.IconWidget.Icon;
import static io.github._3xhaust.dsl.widgets.PaddingWidget.Padding;
import static io.github._3xhaust.dsl.widgets.ProgressBarWidget.ProgressBar;
import static io.github._3xhaust.dsl.widgets.RadioWidget.Radio;
import static io.github._3xhaust.dsl.widgets.RowWidget.Row;
import static io.github._3xhaust.dsl.widgets.ScaffoldWidget.Scaffold;
import static io.github._3xhaust.dsl.widgets.SingleChildScrollViewWidget.SingleChildScrollView;
import static io.github._3xhaust.dsl.widgets.SizedBoxWidget.SizedBox;
import static io.github._3xhaust.dsl.widgets.SliderWidget.Slider;
import static io.github._3xhaust.dsl.widgets.SnackbarWidget.Snackbar;
import static io.github._3xhaust.dsl.widgets.SwitchWidget.Switch;
import static io.github._3xhaust.dsl.widgets.TabsWidget.Tabs;
import static io.github._3xhaust.dsl.widgets.TextAreaWidget.TextArea;
import static io.github._3xhaust.dsl.widgets.TextWidget.Text;
import static io.github._3xhaust.dsl.widgets.TooltipWidget.Tooltip;

public class Main {
    public static void main(String[] args) {
        new JavaUIApp.Builder()
                .title("JavaUI Widget Gallery")
                .size(960, 720)
                .theme(ThemeData.light())
                .build()
                .run(new GalleryPage());
    }

    static final class GalleryPage extends StatefulWidget {
        private final State<Boolean> checkbox = State.of(true);
        private final State<Boolean> toggle = State.of(false);
        private final State<String> radio = State.of("apple");
        private final State<Integer> slider = State.of(40);
        private final State<Integer> progress = State.of(60);
        private final State<String> dropdown = State.of("Option B");
        private final State<String> note = State.of("Try editing me — state syncs in real time.");

        @Override
        public void initState() {
            watch(checkbox); watch(toggle); watch(radio);
            watch(slider); watch(dropdown); watch(note);
        }

        @Override
        public View build() {
            return Scaffold()
                    .appBar(AppBar("Widget Gallery")
                            .action(IconButton(Icons.BELL, () -> Snackbar.show("Hello!"))))
                    .body(SingleChildScrollView(Padding(EdgeInsets.all(16),
                            Column(
                                    Tabs()
                                            .tab("Inputs", inputs())
                                            .tab("Indicators", indicators())
                                            .tab("Display", display())
                            ))));
        }

        private View inputs() {
            return Padding(EdgeInsets.all(12), Column(
                    sectionTitle("Buttons"),
                    Row(
                            Button("Filled", () -> Snackbar.show("Filled")),
                            Button("Outlined", () -> Snackbar.show("Outlined")).outlined(),
                            Button("Text", () -> Snackbar.show("Text")).text(),
                            Button("Disabled").enabled(false)
                    ).gap(10),
                    SizedBox(0, 16),
                    sectionTitle("Toggles"),
                    Row(
                            Checkbox(checkbox, "Enable feature"),
                            SizedBox(20, 0),
                            Switch(toggle),
                            SizedBox(8, 0),
                            Text(toggle.get() ? "ON" : "OFF")
                    ).gap(0),
                    SizedBox(0, 12),
                    sectionTitle("Radio + Dropdown"),
                    Row(
                            Radio(radio, "apple", "Apple"),
                            Radio(radio, "banana", "Banana"),
                            Radio(radio, "cherry", "Cherry"),
                            SizedBox(20, 0),
                            Dropdown(dropdown, "Option A", "Option B", "Option C")
                    ).gap(8),
                    SizedBox(0, 16),
                    sectionTitle("Slider"),
                    Slider(slider).range(0, 100).showTicks(true),
                    Text("Value: " + slider.get()),
                    SizedBox(0, 16),
                    sectionTitle("Text area"),
                    TextArea(note).rows(4).columns(50)
            ).gap(6));
        }

        private View indicators() {
            return Padding(EdgeInsets.all(12), Column(
                    sectionTitle("Linear progress"),
                    ProgressBar(progress).range(0, 100).showText(true),
                    SizedBox(0, 8),
                    Row(
                            Button("− 10", () -> progress.update(v -> Math.max(0, v - 10))).outlined(),
                            Button("+ 10", () -> progress.update(v -> Math.min(100, v + 10)))
                    ).gap(8),
                    SizedBox(0, 16),
                    sectionTitle("Circular progress"),
                    Row(
                            CircularProgress().size(36),
                            CircularProgress().size(48).stroke(5),
                            CircularProgress().size(60).stroke(6).color(Colors.Pink500)
                    ).gap(20)
            ).gap(6));
        }

        private View display() {
            return Padding(EdgeInsets.all(12), Column(
                    sectionTitle("Avatars + Badges"),
                    Row(
                            Avatar("JU"),
                            Avatar("AB").background(Colors.Pink500),
                            Badge(Avatar("MN").background(Colors.Green500), "3"),
                            Tooltip("With a tooltip", Avatar("?"))
                    ).gap(16),
                    SizedBox(0, 16),
                    sectionTitle("Chips"),
                    Row(
                            Chip("Default"),
                            Chip("Click me").onClick(() -> Snackbar.show("Clicked!")),
                            Chip("Primary").color(Colors.Blue500),
                            Chip("Pink").color(Colors.Pink400)
                    ).gap(8),
                    SizedBox(0, 16),
                    sectionTitle("Cards"),
                    Card(Padding(EdgeInsets.all(16), Column(
                            Row(Icon(Icons.SUCCESS).color(Colors.Green500), SizedBox(8, 0),
                                    Text("Card title").bold().fontSize(16)).gap(0),
                            Divider(),
                            SizedBox(0, 8),
                            Text("Cards have rounded corners and a soft drop shadow."),
                            SizedBox(0, 12),
                            Row(Button("Action", () -> Snackbar.show("Card action!")).text(),
                                    Button("Cancel", () -> {}).text()).gap(0)
                    ).gap(0)))
            ).gap(6));
        }

        private View sectionTitle(String s) {
            return Text(s).bold().color(Colors.Grey700);
        }
    }
}

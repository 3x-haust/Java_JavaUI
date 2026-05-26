# JavaUI

Declarative, Flutter-inspired UI framework for Java desktop apps. Backed by Swing, packaged
as a normal Gradle library.

```java
new JavaUIApp.Builder()
        .title("Hello, JavaUI")
        .size(420, 320)
        .theme(ThemeData.light())
        .build()
        .run(
            Scaffold()
                .appBar(AppBar("Hello"))
                .body(Center(
                    Column(
                        Text("Welcome to JavaUI").bold().fontSize(20),
                        SizedBox(0, 12),
                        Button("Click me", () -> Snackbar.show("Hi!"))
                    ).gap(8)
                ))
        );
```

## Highlights

- **Declarative widget tree** — describe the UI as nested function calls; the framework rebuilds Swing components for you.
- **Reactive state** — `State<T>` with multiple listeners; `StatefulWidget#setState` triggers a coalesced rebuild on the EDT.
- **Flexible router** — pattern segments (`/users/:id`), back/forward history, route guards.
- **Theming** — light/dark `ThemeData`, hot-swappable at runtime; `Theme.toggleDark()` updates the active tree.
- **Material-flavored widgets** — buttons (filled/outlined/text), cards with elevation, FAB, app bar, dialogs, snackbars, tabs, badges, chips, avatars, progress, sliders, switches, checkboxes, radios, dropdowns, date pickers, and more.
- **Cross-platform fonts** — picks the best installed sans-serif (`Inter` → `SF Pro` → `Segoe UI` → ...) so apps look consistent on macOS / Windows / Linux.
- **Pluggable image loading** — file system, classpath (`classpath:foo.png`) and HTTP(S) with timeouts; SVG via SVG Salamander.
- **Maven-publishable** — `gradle publishToMavenLocal` produces a clean `io.github._3xhaust:javaUI:0.2.0` artifact with sources and Javadoc jars.

## Getting started

### Prerequisites

- JDK 17 or newer
- Gradle 8+ (the Gradle wrapper is bundled)

### Build & test

```sh
./gradlew build      # compiles + runs JUnit 5 tests + builds the jar
./gradlew test       # tests only
```

### Try the bundled examples

```sh
./gradlew runHello       # minimal Hello World
./gradlew runCounter     # classic counter, demonstrates auto-rebuild
./gradlew runTodo        # list state, checkboxes, snackbar
./gradlew runGallery     # widget catalog
./gradlew runSettings    # live theme switching (dark/light)
./gradlew runLogin       # PostgreSQL-backed login (needs DB; see below)
```

The login example reads `PG_URL` / `PG_USER` / `PG_PASS` env vars (defaults to
`jdbc:postgresql://localhost:5432/javaui`, `postgres`, `postgres`).

## Concepts

### Stateful widgets

Subclass `StatefulWidget` and call `setState(...)` (or mutate a `State` you previously `watch`-ed) to rebuild:

```java
class CounterPage extends StatefulWidget {
    final State<Integer> count = State.of(0);

    @Override public void initState() { watch(count); }

    @Override public View build() {
        return Center(
            Column(
                Text(String.valueOf(count.get())).fontSize(64).bold(),
                Button("+1", () -> count.update(v -> v + 1))
            )
        );
    }
}
```

`watch(state)` subscribes the widget so any future `state.set(...)` schedules a single
rebuild on the next EDT frame — no manual repaint needed.

### Routing

```java
new JavaUIApp.Builder()
    .route("/", () -> new HomePage())
    .route("/users/:id", params -> new UserPage(params.getInt("id", 0)))
    .route("/*", () -> new NotFoundPage())
    .build()
    .run();

Navigator.to("/users/42");
Navigator.back();          // history is preserved
```

### Theming

```java
Theme.set(ThemeData.dark());
// or build a custom theme
ThemeData custom = ThemeData.light().toBuilder()
        .primary(ColorUtils.fromHex("#6750A4"))
        .radius(12)
        .build();
Theme.set(custom);
```

The framework rebuilds the active route automatically when the theme changes.

### Async dialogs and toasts

```java
DialogWidget.showAlert("Oops", "Something went wrong");
boolean ok = DialogWidget.showConfirm("Sure?", "This cannot be undone.");
SnackbarWidget.show("Saved!", SnackbarWidget.Type.SUCCESS);
```

## Widget catalog (highlights)

| Category | Widgets |
| --- | --- |
| Layout | `Scaffold`, `AppBar`, `Row`, `Column`, `Stack`, `Center`, `Align`, `Padding`, `Wrap`, `Expanded`, `Flexible`, `Spacer`, `SizedBox`, `Container`, `Card`, `ListView`, `GridView`, `SingleChildScrollView`, `Divider`, `Tabs` |
| Inputs | `Button`, `IconButton`, `FloatingActionButton`, `Input`, `TextArea`, `Checkbox`, `Switch`, `Radio`, `Slider`, `Dropdown`, `DatePicker`, `FormField` |
| Display | `Text`, `Icon`, `Image`, `Avatar`, `Badge`, `Chip`, `Tooltip`, `ProgressBar`, `CircularProgress` |
| Feedback | `Dialog`, `Snackbar`, `ErrorBoundary` |
| Animation | `Animation.tween(...)` with easing curves |

## Using JavaUI as a library

```sh
./gradlew publishToMavenLocal
```

Then in another Gradle project:

```groovy
repositories { mavenLocal() }
dependencies { implementation 'io.github._3xhaust:javaUI:0.2.0' }
```

## Caveats

- A whole-route rebuild is used for state changes — focus and scroll are not preserved across rebuilds. If that matters for a particular widget, manage focus explicitly.
- Swing is the only renderer for now; the `Renderer` interface is platform-neutral so a JavaFX or Compose-Multiplatform backend can be added later.

## License

MIT.

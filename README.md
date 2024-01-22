# FlutterEngineGroupCompat

Use Flutter Engine Group quickly.

## How Do Use

In Android replace super class.

| Class                              | Compat                                   |
|------------------------------------|------------------------------------------|
| FlutterActivityAndFragmentDelegate | FlutterActivityAndFragmentDelegateCompat |
| FlutterActivity                    | FlutterActivityCompat                    |
| FlutterFragmentActivityCompat      | FlutterFragmentActivityCompat            |
| FlutterFragment                    | FlutterFragmentCompat                    |

In Flutter `main.dart` file if the defaultRoute is `_init` using the Placeholder widget.  

```dart
void main() {
  final binding = WidgetsFlutterBinding.ensureInitialized();
  final defaultRoute = binding.platformDispatcher.defaultRouteName;
  final app = defaultRoute == '_init' ? const Placeholder() : const MyApp();
  runApp(app);
}
```

## Version

| Flutter Version | Use Version |
|-----------------|-------------|
| 2.8.1 ~ 3.13.9  | 3.13.9      |
| 3.16.0 ~        | main        |
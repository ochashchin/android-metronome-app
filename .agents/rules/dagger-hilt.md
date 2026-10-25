# Dagger Hilt Dependency Injection Rules

These rules enforce modern Dependency Injection best practices using Dagger Hilt across this Android project.

---

## 1. Prioritize Constructor Injection (`@Inject`)
* **Classes You Own:** Always annotate the class constructor with `@Inject`.
  ```java
  @Singleton
  public class FlashController {
      @Inject
      public FlashController(@ApplicationContext Context context) { ... }
  }
  ```
* **No Redundant Modules:** Never write `@Provides` methods inside a `@Module` for classes that already have an `@Inject` constructor. Hilt automatically discovers and injects them.

---

## 2. Prefer `@Binds` over `@Provides` for Interface Bindings
* When mapping an interface to its concrete implementation, always use an `abstract` module installed in the appropriate component with `@Binds`:
  ```java
  @Module
  @InstallIn(SingletonComponent.class)
  public abstract class EngineModule {
      @Binds
      @Singleton
      public abstract HomeEngine bindHomeEngine(MetronomeEngine engine);
  }
  ```
* Do not write concrete `@Provides` methods that simply return `new ConcreteClass(...)` to satisfy an interface.

---

## 3. Reserve `@Provides` Exclusively for External Dependencies
Use `@Provides` in a module **only** when:
1. The class belongs to an external library or Android SDK that you cannot edit (e.g. `RoomDatabase`, `Retrofit`, `OkHttpClient`).
2. The instance requires complex builder/factory initialization.
3. Supplying system services (e.g. `VibratorManager`, `CameraManager`).

---

## 4. Restrict Scoping (`@Singleton` is not default)
* Only apply `@Singleton` when:
  * The object holds state that must be shared across the entire application lifetime (e.g., `MetronomeEngine`).
  * Creating the object is demonstrably expensive.
* For all other helper objects, formatters, and stateless utilities, **leave them unscoped** (transient factory). Over-scoping keeps unnecessary objects in memory indefinitely.

---

## 5. Proper Context Qualification
* **Always qualify `Context` parameters:**
  * Use `@ApplicationContext Context context` for singletons, engines, and background services.
  * Use `@ActivityContext Context context` for Activity-scoped objects.
* Never inject raw, unqualified `Context` to prevent ambiguous binding errors and memory leaks.

---

## 6. Android Framework Entry Points
* Application: Annotate with `@HiltAndroidApp`.
* Android Components (Activities, Fragments, Services, Views): Annotate with `@AndroidEntryPoint`.
* ViewModels: Annotate with `@HiltViewModel` and inject via constructor:
  ```java
  @HiltViewModel
  public class HomeViewModel extends ViewModel {
      @Inject
      public HomeViewModel(HomeEngine engine) { ... }
  }
  ```

---

## 7. Component Lifecycle Matching (`@InstallIn`)
Always match the module's `@InstallIn` component to the scope of its bindings:
* `SingletonComponent` $\leftrightarrow$ `@Singleton`
* `ActivityComponent` $\leftrightarrow$ `@ActivityScoped`
* `ViewModelComponent` $\leftrightarrow$ `@ViewModelScoped`
* `FragmentComponent` $\leftrightarrow$ `@FragmentScoped`

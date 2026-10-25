# Android MVVM Architecture Rules

These rules enforce modern Android App Architecture guidelines (MVVM + Unidirectional Data Flow) across the codebase.

---

## 1. Unidirectional Data Flow (UDF)
* **State flows DOWN:** The `ViewModel` exposes immutable state snapshots (`LiveData<UiState>` or `StateFlow<UiState>`).
  * Always hide mutable state holders (`private final MutableLiveData<State> _state` / `public final LiveData<State> state = _state`).
* **Events flow UP:** The View captures user gestures and notifies the `ViewModel` via explicit actions/methods. The View must **never** mutate state or call business engines directly.

---

## 2. Passive Views (Fragments / Activities)
* **Zero Business Logic:** Views must be purely declarative and passive. Never perform tempo math, unit conversion, or string formatting directly inside the Fragment.
* **No Direct Diffing Caches in Fragments:** Avoid maintaining loose primitive cache variables (e.g. `lastBpm`, `lastMs`, `lastIsPlaying`) in Fragment fields.
  * Delegate change detection to custom view setters (making them idempotent).
  * Or extract complex multi-view updates into a dedicated **UI Logic State Holder** (e.g., `PlaybackCoordinator`, `HomeViewBinder`).
* **Lifecycle Cleanup:** Always clean up UI resources, animations, and ViewBinder references in `onDestroyView()`.

---

## 3. Dual State Holders (Google Architecture Guidelines)
Separate state management into two clear categories:
1. **Business Logic State Holders (`ViewModel`):**
   * Survives configuration changes (screen rotations).
   * Interacts with repositories, use cases, or core audio engines.
   * Exposes screen-level UI state.
2. **UI Logic State Holders (Plain Java/Kotlin Classes):**
   * Owns view-bound UI logic (e.g., coordinating animations across multiple views, visual diffing, scroll/touch behaviors).
   * Tied to the View lifecycle (`onViewCreated` -> `onDestroyView`).
   * Keeps Fragments thin and testable.

---

## 4. ViewModel Boundaries
* **No Android View References:** The ViewModel must never reference `View`, `TextView`, `Context`, `Fragment`, or `Activity`.
* **No Direct System API / Service Execution:** Do not call Android framework services directly inside the ViewModel (e.g., `application.startForegroundService(intent)`).
  * Encapsulate service starts, notifications, and audio playback inside a repository or service controller (e.g., `MetronomeServiceController` or `MetronomeRepository`) injected into the ViewModel.

---

## 5. UI State vs. Transient Events
* **Persistent UI State:** Represents the current visual state of the screen (e.g. `bpm`, `mode`, `isPlaying`). Model as an immutable class.
* **One-Shot Events:** Transient actions (e.g. `flashBlink`, `pulseBounce`, navigation, snackbars) must not be stored as persistent booleans in the main state object to avoid state pollution and redundant state copies.
  * Use dedicated event wrappers, channels/flows, or observer callbacks for one-time triggers.

---

## 6. Custom View Idempotence
* All custom view setters (e.g. `setProgress(float)`, `setMode(Mode)`, `setPlaying(boolean)`) must verify if the incoming value equals the current value before running layout passes, animations, or matrix transforms:
  ```java
  public void setProgress(float progress) {
      if (Float.compare(this.currentProgress, progress) == 0) return;
      this.currentProgress = progress;
      // apply changes...
  }
  ```


Alerts
----

> A tiny library in Respo for showing alerts.

### Usage

[![Clojars Project](https://img.shields.io/clojars/v/respo/alerts.svg)](https://clojars.org/respo/alerts)

```edn
[respo/alerts "0.5.0-a2"]
```

This library provides several UI components, so you need to control their visibilities with your own states, for example: `{:show-alert? true}`.

```clojure
[respo-alerts.core :refer [comp-alert comp-prompt comp-confirm]]
```

Since every component has its own internal states, I use `>>` in all examples:

`comp-alert` is like `alert("message")` but with a callback function:

```clojure
(comp-alert (>> states :alerts)
          {:trigger (comp-buttom "trigger"),
           :text "message text",
           :style {}}
           (fn [e dispatch!]
               (dispatch! :some/action "data")))
```

`comp-alert` is like `confirm("message")` but with a callback function returning `result`:

```clojure
(comp-confirm (>> states :confirm)
          {:trigger (comp-button "trigger"),
           :text "message text"
           :style {}}
          (fn [e dispatch!]
              (dispatch! :some/action "data")
              (println "confirmed!")))
```

`comp-prompt` is like `prompt("message", "default")` but with a callback function returning `result`:

```clojure
(comp-prompt (>> states :prompt)
          {:trigger (comp-button "trigger"),
           :text "message text",
           :style {}
           :input-style {}
           :multiline? false
           :initial "default text"
           :placeholder "input"
           :button-text "Submit"
           :validator (fn [x] (if (string/blank? x) "Blank failed" nil))}
          (fn [result dispatch! mutate!]
              (dispatch! :some/action "data")
              (dispatch! cursor (assoc state :show-prompt? false))
              (println "finish editing!" result)))
```

`comp-select` pops up a select menu and returns a `result` in callback function:

```clojure
(def candidates [{:value "haskell", :display "Haskell"}
                 {:value "clojure", :display "Clojure"}
                 {:value "elixir", :display "Elixir"}])

(comp-select (>> states :select) (:selected state) candidates
          {:style-trigger {},
           :text "Select a item from:"}
          (fn [result d!]
              (println "finish selecting!" result)
              (d! cursor (assoc state :selected result))))
```

`comp-modal` for rendering modal without child:

```clojure
(let [on-close (fn [d!] (d! cursor (assoc state :show? false)))]
 (comp-modal
  (:show? state)
  {:title "Demo", :style {:width 400}, :container-style {}}
  on-close
  (fn [] (div {} (<> "Place for child content")))))
```

```clojure
(comp-modal-menu
  (:show-modal-menu? state)
  {:title "Demo", :style {:width 300}}
  [{:value "a", :display "A"} {:value "b", :display (div {} (<> "B"))}]
  (fn [d!] (d! cursor (assoc state :show-modal-menu? false)))
  (fn [result d!]
    (println "result" result)
    (d! cursor (assoc state :show-modal-menu? false)))))
```

### Hooks usages

Hooks style API is provided, very briefly:

* `use-alert`
* `use-confirm`
* `use-select`
* `use-modal`
* `use-modal-menu`

```clojure
(let [prompt-plugin (use-prompt (>> states :prompt) {:title "demo"})]


 (button
  {:inner-text "show prompt",
   :style ui/button,
   :on-click (fn [e d!]
     ((:show prompt-plugin) d! (fn [text] (println "read from prompt" (pr-str text)))))})

  (:ui prompt-plugin))
```

### Workflow

Workflow https://github.com/mvc-works/calcit-workflow

### License

MIT


Alerts
----

> A tiny library in Respo for showing alerts.

### Usage

[![Clojars Project](https://img.shields.io/clojars/v/respo/alerts.svg)](https://clojars.org/respo/alerts)

```edn
[respo/alerts "0.1.3"]
```

This library provides several UI components, so you need to control their visibilities with your own states, for example: `{:show-alert? true}`.

```clojure
[respo-alerts.comp.alerts :refer [comp-alert comp-prompt comp-confirm]]
```

`comp-alert` is like `alert("message")` but with a callback function:

```clojure
(comp-alert "message text"
            (fn [e dispatch! mutate!]
                (dispatch! :some/action "data")
                (mutate! %cursor (assoc state :show-alert? false))))
```

`comp-alert` is like `confirm("message")` but with a callback function returning `result`:

```clojure
(comp-confirm "message text"
              (fn [result dispatch! mutate!]
                  (dispatch! :some/action "data")
                  (mutate! %cursor (assoc state :show-confirm? false))
                  (println "confirm in boolean!" result)))
```

`comp-prompt` is like `prompt("message", "default")` but with a callback function returning `result`. Also notice that `comp-prompt` has internal states, which is why I use `cursor->` in the example:

```clojure
(cursor-> :prompt
          comp-prompt states "message text" "default text"
                      (fn [result dispatch! mutate!]
                          (dispatch! :some/action "data")
                          (mutate! %cursor (assoc state :show-prompt? false))
                          (println "finish editing!" result)))
```

### Workflow

Workflow https://github.com/mvc-works/calcit-workflow

### License

MIT


Alerts
----

> A tiny library in Respo for showing alerts.

### Usage

[![Clojars Project](https://img.shields.io/clojars/v/respo/alerts.svg)](https://clojars.org/respo/alerts)

```edn
[respo/alerts "0.2.1"]
```

This library provides several UI components, so you need to control their visibilities with your own states, for example: `{:show-alert? true}`.

```clojure
[respo-alerts.comp.alerts :refer [comp-alert comp-prompt comp-confirm]]
```

Since every component has its own internal states, I use `cursor->` in all examples:

`comp-alert` is like `alert("message")` but with a callback function:

```clojure
(cursor-> :alert
          comp-alert states (comp-buttom "trigger") "message text"
                     (fn [e dispatch! mutate!]
                         (dispatch! :some/action "data")))
```

`comp-alert` is like `confirm("message")` but with a callback function returning `result`:

```clojure
(cursor-> :confirm comp-confirm states (comp-button "trigger") "message text"
                                (fn [result dispatch! mutate!]
                                    (dispatch! :some/action "data")
                                    (println "confirm in boolean!" result)))
```

`comp-prompt` is like `prompt("message", "default")` but with a callback function returning `result`:

```clojure
(cursor-> :prompt
          comp-prompt states (comp-button "trigger") "message text" "default text"
                      (fn [result dispatch! mutate!]
                          (dispatch! :some/action "data")
                          (mutate! %cursor (assoc state :show-prompt? false))
                          (println "finish editing!" result)))
```

### Workflow

Workflow https://github.com/mvc-works/calcit-workflow

### License

MIT

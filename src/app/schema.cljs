
(ns app.schema )

(def config
  {:storage "Alerts",
   :dev-ui "http://localhost:8100/main.css",
   :release-ui "http://cdn.tiye.me/favored-fonts/main.css",
   :cdn "http://cdn.tiye.me/respo-alerts/"})

(def dev? (do ^boolean js/goog.DEBUG))

(def store {:states {}, :content ""})

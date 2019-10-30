
(ns respo-alerts.util )

(defn focus-element! [query]
  (let [target (.querySelector js/document query)] (if (some? target) (.focus target))))

(defn select-element! [query]
  (let [target (.querySelector js/document query)] (if (some? target) (.select target))))

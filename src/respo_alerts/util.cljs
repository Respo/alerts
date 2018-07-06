
(ns respo-alerts.util )

(defn do-later! [f] (js/setTimeout f 50))

(defn focus-later! [query]
  (do-later!
   (fn []
     (let [target (.querySelector js/document query)] (if (some? target) (.focus target))))))

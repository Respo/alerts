
(ns respo-alerts.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.macros
             :refer
             [defcomp cursor-> action-> mutation-> <> div button textarea span]]
            [verbosely.core :refer [verbosely!]]
            [respo.comp.space :refer [=<]]
            [reel.comp.reel :refer [comp-reel]]
            [respo-md.comp.md :refer [comp-md]]
            [respo-alerts.config :refer [dev?]]
            [respo-alerts.comp.alerts :refer [comp-alert comp-confirm comp-prompt]]
            [respo.comp.inspect :refer [comp-inspect]]))

(defcomp
 comp-container
 (reel)
 (let [store (:store reel), states (:states store)]
   (div
    {:style (merge ui/global ui/row)}
    (div
     {:style {:padding 16}}
     (cursor->
      :alert
      comp-alert
      states
      (button {:style ui/button} (<> "Alert"))
      "This would be a very long content of alerts, like some alerts..."
      (fn [e d! m!] (println "message has been read.")))
     (=< 8 nil)
     (cursor->
      :confirm
      comp-confirm
      states
      (button {:style ui/button} (<> "Confirm"))
      "This would be a very long content of alerts, like some confirmation..."
      (fn [result d! m!] (println "confirm!" result)))
     (=< 8 nil)
     (cursor->
      :prompt
      comp-prompt
      states
      (button {:style ui/button} (<> "Prompt"))
      "This would be a very long content of alerts, like some prompt... pick number:"
      (str (rand-int 100))
      (fn [result d! m!] (println "finish editing!" result))))
    (comment when dev? (comp-inspect "states" states nil))
    (when dev? (cursor-> :reel comp-reel states reel {})))))

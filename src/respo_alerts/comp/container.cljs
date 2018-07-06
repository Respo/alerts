
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

(defcomp comp-button (text) (button {:style ui/button} (<> text)))

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
      {:trigger (comp-button "Alert"),
       :text "This would be a very long content of alerts, like some alerts...",
       :style {}}
      (fn [e d! m!] (println "message has been read.")))
     (=< 8 nil)
     (cursor->
      :confirm
      comp-confirm
      states
      {:style {},
       :trigger (comp-button "Confirm"),
       :text "This would be a very long content of alerts, like some confirmation..."}
      (fn [e d! m!] (println "confirmed!")))
     (=< 8 nil)
     (cursor->
      :prompt
      comp-prompt
      states
      {:trigger (comp-button "Prompt"),
       :text "This would be a very long content of alerts, like some prompt... pick number:",
       :initial (str (rand-int 100)),
       :style {}}
      (fn [result d! m!] (println "finish editing!" result))))
    (when dev? (comp-inspect "states" states {:bottom 0}))
    (when dev? (cursor-> :reel comp-reel states reel {})))))

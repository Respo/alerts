
(ns respo-alerts.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.core
             :refer
             [defcomp cursor-> action-> mutation-> <> div button textarea span]]
            [respo.comp.space :refer [=<]]
            [reel.comp.reel :refer [comp-reel]]
            [respo-md.comp.md :refer [comp-md]]
            [respo-alerts.config :refer [dev?]]
            [respo-alerts.comp.alerts
             :refer
             [comp-alert comp-confirm comp-prompt comp-select]]
            [respo.comp.inspect :refer [comp-inspect]]
            [respo-alerts.style :as style]))

(defcomp comp-button (text) (button {:style style/button} (<> text)))

(defcomp
 comp-container
 (reel)
 (let [store (:store reel)
       states (:states store)
       state (or (:data states) {:selected ""})]
   (div
    {:style (merge ui/global ui/row ui/fullscreen {:overflow :auto})}
    (div
     {:style (merge ui/row {:padding 16, :align-items :flex-start})}
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
      (fn [result d! m!] (println "finish editing!" result)))
     (=< 8 nil)
     (cursor->
      :prompt-multiline
      comp-prompt
      states
      {:trigger (comp-button "Prompt multiline"),
       :text "This would be a very long content of alerts, like some prompt... write multiple lines:",
       :initial (str (rand-int 100)),
       :style {},
       :input-style {:font-family ui/font-code},
       :multiline? true}
      (fn [result d! m!] (println "finish editing!" result)))
     (=< 8 nil)
     (cursor->
      :select
      comp-select
      states
      (:selected state)
      [{:value "haskell", :display "Haskell"}
       {:value "clojure", :display "Clojure"}
       {:value "elixir", :display "Elixir"}]
      {:style-trigger {:border "1px solid #ddd", :padding "0 8px", :line-height "32px"},
       :text "Select a item from:"}
      (fn [result d! m!]
        (println "finish selecting!" result)
        (m! %cursor (assoc state :selected result)))))
    (when dev? (comp-inspect "states" states {:bottom 0}))
    (when dev? (cursor-> :reel comp-reel states reel {})))))

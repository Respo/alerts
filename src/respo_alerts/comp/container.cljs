
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
            [respo-alerts.core
             :refer
             [comp-alert comp-confirm comp-prompt comp-select comp-modal comp-modal-menu]]
            [respo.comp.inspect :refer [comp-inspect]]
            [respo-alerts.style :as style]
            [clojure.string :as string]
            [cljs.reader :refer [read-string]]))

(defcomp comp-button (text) (button {:style style/button} (<> text)))

(defcomp
 comp-controlled-modals
 (states)
 (let [state (or (:data states) {:selected "", :show-modal? false, :show-modal-menu? false})]
   (div
    {:style (merge ui/row {:padding 16})}
    (button
     {:style ui/button,
      :inner-text "Modal",
      :on-click (fn [e d! m!] (m! (assoc state :show-modal? true)))})
    (let [on-close (fn [m!] (m! %cursor (assoc state :show-modal? false)))]
      (comp-modal
       (:show-modal? state)
       {:title "Demo", :style {:width 400}, :container-style {}}
       on-close
       (fn [] (div {} (<> "Place for child content")))))
    (=< 8 nil)
    (button
     {:style ui/button,
      :inner-text "Modal Menu",
      :on-click (fn [e d! m!] (m! (assoc state :show-modal-menu? true)))})
    (comp-modal-menu
     (:show-modal-menu? state)
     {:title "Demo", :style {:width 300}}
     [{:value "a", :display "A"} {:value "b", :display (div {} (<> "B"))}]
     (fn [m!] (m! %cursor (assoc state :show-modal-menu? false)))
     (fn [result d! m!]
       (println "result" result)
       (m! %cursor (assoc state :show-modal-menu? false)))))))

(defcomp
 comp-select-actions
 (states)
 (let [state (or (:data states) {:selected ""})]
   (div
    {:style {:padding 16}}
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
       (m! %cursor (assoc state :selected result)))))))

(defcomp
 comp-stateful-actions
 (states)
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
    :style {},
    :placeholder "input demo",
    :button-text "Finish and submit"}
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
   :prompt-validator
   comp-prompt
   states
   {:trigger (comp-button "Prompt validator"),
    :text "This would be a very long content of alerts, like some prompt... write multiple lines:",
    :initial (str (rand-int 100)),
    :style {},
    :input-style {:font-family ui/font-code},
    :multiline? true,
    :validator (fn [x] (try (do (read-string x) nil) (catch js/Error e (str e))))}
   (fn [result d! m!] (println "finish editing!" result)))))

(defcomp
 comp-container
 (reel)
 (let [store (:store reel)
       states (:states store)
       state (or (:data states) {:selected "", :show-modal? false, :show-modal-menu? false})]
   (div
    {:style (merge ui/global ui/fullscreen ui/column {:overflow :auto})}
    (cursor-> :stateful comp-stateful-actions states)
    (cursor-> :select comp-select-actions states)
    (cursor-> :controlled comp-controlled-modals states)
    (when dev? (comp-inspect "states" states {:bottom 0}))
    (when dev? (cursor-> :reel comp-reel states reel {})))))

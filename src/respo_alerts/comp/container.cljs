
(ns respo-alerts.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.core :refer [defcomp >> <> div button textarea span]]
            [respo.comp.space :refer [=<]]
            [reel.comp.reel :refer [comp-reel]]
            [respo-md.comp.md :refer [comp-md]]
            [respo-alerts.config :refer [dev?]]
            [respo-alerts.core
             :refer
             [comp-alert
              comp-confirm
              comp-prompt
              comp-select
              comp-modal
              comp-modal-menu
              use-alert
              use-confirm
              use-prompt]]
            [respo.comp.inspect :refer [comp-inspect]]
            [respo-alerts.style :as style]
            [clojure.string :as string]
            [cljs.reader :refer [read-string]]))

(defcomp comp-button (text) (button {:style style/button} (<> text)))

(defn use-modal [states options]
  (let [cursor (:cursor states), state (or (:data states) {:show? false})]
    {:ui (comp-modal options (:show? state) (fn [d!] (d! cursor (assoc state :show? false)))),
     :show (fn [d!] (d! cursor (assoc state :show? true)))}))

(defn use-modal-menu [states options]
  (let [cursor (:cursor states), state (or (:data states) {:show? false})]
    {:ui (comp-modal-menu
          options
          (:show? state)
          (fn [d!] (d! cursor (assoc state :show? false)))
          (fn [result d!]
            ((:on-result options) result d!)
            (d! cursor (assoc state :show? false)))),
     :show (fn [d!] (d! cursor (assoc state :show? true)))}))

(defcomp
 comp-controlled-modals
 (states)
 (let [demo-modal (use-modal
                   (>> states :modal)
                   {:title "demo",
                    :style {:width 400},
                    :container-style {},
                    :render-body (fn [] (div {} (<> "Place for child content")))})
       demo-modal-menu (use-modal-menu
                        (>> states :modal-menu)
                        {:title "Demo",
                         :style {:width 300},
                         :items [{:value "a", :display "A"}
                                 {:value "b", :display (div {} (<> "B"))}],
                         :on-result (fn [result d!] (println "got result" result))})]
   (div
    {}
    (div {} (<> "Hooks Modal usage"))
    (div
     {:style {:padding "8px 16px"}}
     (button
      {:inner-text "show modal",
       :style ui/button,
       :on-click (fn [e d!] ((:show demo-modal) d!))})
     (=< 8 nil)
     (button
      {:inner-text "show modal menu",
       :style ui/button,
       :on-click (fn [e d!] ((:show demo-modal-menu) d!))})
     (:ui demo-modal)
     (:ui demo-modal-menu)))))

(defcomp
 comp-hooks-usages
 (states)
 (let [alert-plugin (use-alert (>> states :alert) {:title "demo"})
       confirm-plugin (use-confirm (>> states :alert) {:title "demo"})
       prompt-plugin (use-prompt (>> states :prompt) {:title "demo"})]
   (div
    {}
    (div {} (<> "Hooks"))
    (div
     {}
     (button
      {:inner-text "show alert",
       :style ui/button,
       :on-click (fn [e d!] ((:show alert-plugin) d!))})
     (=< 8 nil)
     (button
      {:inner-text "show confirm",
       :style ui/button,
       :on-click (fn [e d!] ((:show confirm-plugin) d! (fn [] (println "after confirmed"))))})
     (=< 8 nil)
     (button
      {:inner-text "show prompt",
       :style ui/button,
       :on-click (fn [e d!]
         ((:show prompt-plugin) d! (fn [text] (println "read from prompt" (pr-str text)))))}))
    (:ui alert-plugin)
    (:ui confirm-plugin)
    (:ui prompt-plugin))))

(defcomp
 comp-select-actions
 (states)
 (let [cursor (:cursor states), state (or (:data states) {:selected ""})]
   (div
    {}
    (div {} (<> "Select"))
    (div
     {:style {:padding 16}}
     (comp-select
      (>> states :select)
      (:selected state)
      [{:value "haskell", :display "Haskell"}
       {:value "clojure", :display "Clojure"}
       {:value "elixir", :display "Elixir"}]
      {:style-trigger {:border "1px solid #ddd", :padding "0 8px", :line-height "32px"},
       :text "Select a item from:"}
      (fn [result d!]
        (println "finish selecting!" result)
        (d! cursor (assoc state :selected result))))))))

(defcomp
 comp-stateful-actions
 (states)
 (div
  {}
  (div {} (<> "Components"))
  (div
   {:style (merge ui/row {:padding 16, :align-items :flex-start})}
   (comp-alert
    (>> states :alert)
    {:trigger (comp-button "Alert"),
     :text "This would be a very long content of alerts, like some alerts...",
     :style {}}
    (fn [e d!] (println "message has been read.")))
   (=< 8 nil)
   (comp-confirm
    (>> states :confirm)
    {:style {},
     :trigger (comp-button "Confirm"),
     :text "This would be a very long content of alerts, like some confirmation..."}
    (fn [e d!] (println "confirmed!")))
   (=< 8 nil)
   (comp-prompt
    (>> states :prompt)
    {:trigger (comp-button "Prompt"),
     :text "This would be a very long content of alerts, like some prompt... pick number:",
     :initial (str (rand-int 100)),
     :style {},
     :placeholder "input demo",
     :button-text "Finish and submit"}
    (fn [result d!] (println "finish editing!" result)))
   (=< 8 nil)
   (comp-prompt
    (>> states :prompt-multiline)
    {:trigger (comp-button "Prompt multiline"),
     :text "This would be a very long content of alerts, like some prompt... write multiple lines:",
     :initial (str (rand-int 100)),
     :style {},
     :input-style {:font-family ui/font-code},
     :multiline? true}
    (fn [result d!] (println "finish editing!" result)))
   (=< 8 nil)
   (comp-prompt
    (>> states :prompt-validator)
    {:trigger (comp-button "Prompt validator"),
     :text "This would be a very long content of alerts, like some prompt... write multiple lines:",
     :initial (str (rand-int 100)),
     :style {},
     :input-style {:font-family ui/font-code},
     :multiline? true,
     :validator (fn [x] (try (do (read-string x) nil) (catch js/Error e (str e))))}
    (fn [result d!] (println "finish editing!" result))))))

(defcomp
 comp-container
 (reel)
 (let [store (:store reel)
       states (:states store)
       state (or (:data states) {:selected "", :show-modal? false, :show-modal-menu? false})]
   (div
    {:style (merge ui/global ui/fullscreen ui/column {:padding 20})}
    (comp-stateful-actions (>> states :stateful))
    (comp-hooks-usages (>> states :hooks))
    (comp-select-actions (>> states :select))
    (comp-controlled-modals (>> states :controlled))
    (when dev? (comp-inspect "states" states {:bottom 0}))
    (when dev? (comp-reel (>> states :reel) reel {})))))

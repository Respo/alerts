
(ns app.comp.alerts
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.macros
             :refer
             [defcomp cursor-> action-> mutation-> <> div button textarea span input]]
            [verbosely.core :refer [verbosely!]]
            [respo.comp.space :refer [=<]]
            [app.config :refer [dev?]]))

(defcomp
 comp-alert
 (content on-read!)
 (div
  {:style (merge ui/fullscreen ui/center {:background-color (hsl 0 0 0 0.2)}),
   :on-click (fn [e d! m!] (on-read! e d! m!))}
  (div
   {:style (merge
            ui/column
            {:background-color (hsl 0 0 100),
             :min-width 320,
             :max-width "80vw",
             :padding 16,
             :border-radius "16px",
             :color (hsl 0 0 0)}),
    :on-click (fn [e d! m!] )}
   (div {} (<> content))
   (=< nil 8)
   (div
    {:style ui/row-parted}
    (span nil)
    (button
     {:style ui/button, :auto-focus true, :on-click (fn [e d! m!] (on-read! e d! m!))}
     (<> "Read"))))))

(defcomp
 comp-confirm
 (content on-confirm!)
 (div
  {:style (merge ui/fullscreen ui/center {:background-color (hsl 0 0 0 0.2)})}
  (div
   {:style (merge
            ui/column
            {:background-color (hsl 0 0 100),
             :min-width 320,
             :max-width "80vw",
             :padding 16,
             :border-radius "16px",
             :color (hsl 0 0 0)}),
    :on-click (fn [e d! m!] )}
   (div {} (<> content))
   (=< nil 8)
   (div
    {:style ui/row-parted}
    (span nil)
    (div
     {}
     (button
      {:style (merge ui/button {:border :none}),
       :auto-focus true,
       :on-click (fn [e d! m!] (on-confirm! false d! m!))}
      (<> "Cancel"))
     (=< 8 nil)
     (button
      {:style ui/button,
       :auto-focus true,
       :on-click (fn [e d! m!] (on-confirm! true d! m!))}
      (<> "Confirm")))))))

(defcomp
 comp-prompt
 (states content initial-text on-finish!)
 (let [state (or (:data states) {:text initial-text})]
   (div
    {:style (merge ui/fullscreen ui/center {:background-color (hsl 0 0 0 0.2)})}
    (div
     {:style (merge
              ui/column
              {:background-color (hsl 0 0 100),
               :min-width 320,
               :max-width "80vw",
               :padding 16,
               :border-radius "16px",
               :color (hsl 0 0 0)}),
      :on-click (fn [e d! m!] )}
     (div {} (<> content))
     (=< nil 8)
     (div
      {}
      (input
       {:style ui/input,
        :placeholder initial-text,
        :value (:text state),
        :on-input (fn [e d! m!] (m! (assoc state :text (:value e))))}))
     (=< nil 8)
     (div
      {:style ui/row-parted}
      (span nil)
      (button
       {:style ui/button,
        :auto-focus true,
        :on-click (fn [e d! m!]
          (m! (assoc state :text nil))
          (on-finish! (:text state) d! m!))}
       (<> "Read")))))))

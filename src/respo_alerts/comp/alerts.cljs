
(ns respo-alerts.comp.alerts
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.macros
             :refer
             [defcomp cursor-> action-> mutation-> <> div button textarea span input]]
            [verbosely.core :refer [verbosely!]]
            [respo.comp.space :refer [=<]]
            [respo-alerts.config :refer [dev?]]
            [respo-alerts.style :as style]
            [keycode.core :as keycode]))

(defcomp
 comp-alert
 (content on-read!)
 (assert (string? content) "content should be a string")
 (assert (fn? on-read!) "require a callback function")
 (div
  {:style (merge ui/fullscreen ui/center style/backdrop),
   :on-click (fn [e d! m!] (on-read! e d! m!))}
  (div
   {:style (merge ui/column style/card), :on-click (fn [e d! m!] )}
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
 (assert (string? content) "content should be a string")
 (assert (fn? on-confirm!) "require a callback function")
 (div
  {:style (merge ui/fullscreen ui/center style/backdrop)}
  (div
   {:style (merge ui/column style/card), :on-click (fn [e d! m!] )}
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
 (assert (map? states) "should take states in the first argument")
 (assert (string? content) "content should be a string")
 (assert (string? initial-text) "initial-text should be a string")
 (assert (fn? on-finish!) "on-finish! a callback function")
 (let [state (or (:data states) {:text initial-text})]
   (div
    {:style (merge ui/fullscreen ui/center style/backdrop)}
    (div
     {:style (merge ui/column style/card), :on-click (fn [e d! m!] )}
     (div {} (<> content))
     (=< nil 8)
     (div
      {}
      (input
       {:style (merge ui/input {:width "100%"}),
        :placeholder "",
        :autofocus true,
        :value (:text state),
        :on-input (fn [e d! m!] (m! (assoc state :text (:value e)))),
        :on-keydown (fn [e d! m!]
          (when (= (:key-code e) keycode/return) (m! nil) (on-finish! (:text state) d! m!)))}))
     (=< nil 16)
     (div
      {:style ui/row-parted}
      (span nil)
      (button
       {:style ui/button,
        :on-click (fn [e d! m!] (m! nil) (on-finish! (:text state) d! m!))}
       (<> "Finish")))))))

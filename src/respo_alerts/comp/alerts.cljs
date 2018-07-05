
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
            [keycode.core :as keycode]
            [respo-alerts.schema :as schema]
            [respo-alerts.util :refer [focus-later!]]))

(defcomp
 comp-alert
 (states options on-read!)
 (assert (fn? on-read!) "require a callback function")
 (let [trigger (:trigger options)
       content (or (:text options) "Alert!")
       state (or (:data states) {:show? false})]
   (assert (map? trigger) "need to use an element as trigger")
   (span
    {:style {:cursor :pointer},
     :on-click (fn [e d! m!]
       (m! (assoc state :show? true))
       (focus-later! (str "." schema/confirm-button-name)))}
    trigger
    (when (:show? state)
      (div
       {:style (merge ui/fullscreen ui/center style/backdrop),
        :on-click (fn [e d! m!]
          (let [event (:event e)]
            (.stopPropagation event)
            (on-read! e d! m!)
            (m! (assoc state :show? false))))}
       (div
        {:style (merge ui/column style/card), :on-click (fn [e d! m!] )}
        (div {} (<> content))
        (=< nil 8)
        (div
         {:style ui/row-parted}
         (span nil)
         (button
          {:style ui/button,
           :class-name schema/confirm-button-name,
           :auto-focus true,
           :on-click (fn [e d! m!] (on-read! e d! m!) (m! (assoc state :show? false)))}
          (<> "Read")))))))))

(defcomp
 comp-confirm
 (states trigger content on-confirm!)
 (assert (map? trigger) "need to use an element as trigger")
 (assert (string? content) "content should be a string")
 (assert (fn? on-confirm!) "require a callback function")
 (let [state (or (:data states) {:show? false})]
   (span
    {:style {:cursor :pointer},
     :on-click (fn [e d! m!]
       (m! (assoc state :show? true))
       (js/setTimeout
        (fn []
          (let [target (.querySelector js/document (str "." schema/confirm-button-name))]
            (if (some? target) (.focus target))))
        50))}
    trigger
    (when (:show? state)
      (div
       {:style (merge ui/fullscreen ui/center style/backdrop), :on-click (fn [e d! m!] )}
       (div
        {:style (merge ui/column style/card)}
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
            :on-click (fn [e d! m!]
              (on-confirm! false d! m!)
              (m! (assoc state :show? false)))}
           (<> "Cancel"))
          (=< 8 nil)
          (button
           {:style ui/button,
            :auto-focus true,
            :class-name schema/confirm-button-name,
            :on-click (fn [e d! m!]
              (on-confirm! true d! m!)
              (m! (assoc state :show? false)))}
           (<> "Confirm"))))))))))

(defcomp
 comp-prompt
 (states trigger content initial-text on-finish!)
 (assert (map? trigger) "need to use an element as trigger")
 (assert (string? content) "content should be a string")
 (assert (string? initial-text) "initial-text should be a string")
 (assert (fn? on-finish!) "on-finish! a callback function")
 (let [state (or (:data states) {:text initial-text, :show? false})
       text (or (:text state) initial-text)]
   (span
    {:style {:cursor :pointer},
     :on-click (fn [e d! m!]
       (m! (assoc state :show? true))
       (js/setTimeout
        (fn []
          (let [target (.querySelector js/document (str "." schema/input-box-name))]
            (if (some? target) (.select target))))
        50))}
    trigger
    (if (:show? state)
      (div
       {:style (merge ui/fullscreen ui/center style/backdrop),
        :on-click (fn [e d! m!] (m! (assoc state :show? false)))}
       (div
        {:style (merge ui/column style/card), :on-click (fn [e d! m!] )}
        (div {} (<> content))
        (=< nil 8)
        (div
         {}
         (input
          {:style (merge ui/input {:width "100%"}),
           :class-name schema/input-box-name,
           :placeholder "",
           :value text,
           :on-input (fn [e d! m!] (m! (assoc state :text (:value e)))),
           :on-keydown (fn [e d! m!]
             (when (= (:key-code e) keycode/return)
               (on-finish! text d! m!)
               (m! (assoc state :show? false :text nil))))}))
        (=< nil 16)
        (div
         {:style ui/row-parted}
         (span nil)
         (button
          {:style ui/button,
           :on-click (fn [e d! m!]
             (on-finish! text d! m!)
             (m! (assoc state :show? false :text nil)))}
          (<> "Finish")))))))))

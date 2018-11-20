
(ns respo-alerts.comp.alerts
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.core
             :refer
             [defcomp
              cursor->
              action->
              list->
              mutation->
              <>
              div
              button
              textarea
              span
              input
              a]]
            [respo.comp.space :refer [=<]]
            [respo-alerts.config :refer [dev?]]
            [respo-alerts.style :as style]
            [respo-alerts.schema :as schema]
            [respo-alerts.util :refer [focus-later! select-later!]]
            [respo-alerts.style :as style]))

(defcomp
 comp-alert
 (states options on-read!)
 (assert (fn? on-read!) "require a callback function")
 (let [trigger (:trigger options)
       content (or (:text options) "Alert!")
       state (or (:data states) {:show? false})]
   (assert (map? trigger) "need to use an element as trigger")
   (span
    {:style (merge {:cursor :pointer, :display :inline-block} (:style options)),
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
          {:style style/button,
           :class-name schema/confirm-button-name,
           :on-click (fn [e d! m!] (on-read! e d! m!) (m! (assoc state :show? false)))}
          (<> "Read")))))))))

(defcomp
 comp-confirm
 (states options on-confirm!)
 (assert (fn? on-confirm!) "require a callback function")
 (let [trigger (:trigger options)
       content (or (:text options) "Confirm?")
       state (or (:data states) {:show? false})]
   (assert (map? trigger) "need to use an element as trigger")
   (span
    {:style (merge {:cursor :pointer, :display :inline-block} (:style options)),
     :on-click (fn [e d! m!]
       (m! (assoc state :show? true))
       (focus-later! (str "." schema/confirm-button-name)))}
    trigger
    (when (:show? state)
      (div
       {:style (merge ui/fullscreen ui/center style/backdrop),
        :on-click (fn [e d! m!] (m! (assoc state :show? false)))}
       (div
        {:style (merge ui/column style/card), :on-click (fn [e d! m!] )}
        (div {} (<> content))
        (=< nil 8)
        (div
         {:style ui/row-parted}
         (span nil)
         (button
          {:style style/button,
           :class-name schema/confirm-button-name,
           :on-click (fn [e d! m!] (on-confirm! e d! m!) (m! (assoc state :show? false)))}
          (<> "Confirm")))))))))

(defcomp
 comp-prompt
 (states options on-finish!)
 (assert (fn? on-finish!) "on-finish! a callback function")
 (let [trigger (:trigger options)
       content (or (:text options) "Type in text")
       initial-text (or (:initial options) "")
       state (or (:data states) {:text initial-text, :show? false})
       text (or (:text state) initial-text)]
   (assert (map? trigger) "need to use an element as trigger")
   (span
    {:style (merge {:cursor :pointer, :display :inline-block} (:style options)),
     :on-click (fn [e d! m!]
       (m! (assoc state :show? true))
       (select-later! (str "." schema/input-box-name)))}
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
         (let [props {:class-name schema/input-box-name,
                      :placeholder "",
                      :value text,
                      :on-input (fn [e d! m!] (m! (assoc state :text (:value e)))),
                      :on-keydown (fn [e d! m!]
                        (when (and (not= 229 (:keycode e)) (= (:key e) "Enter"))
                          (if (:multiline? options)
                            (when (.-metaKey (:event e))
                              (do
                               (on-finish! text d! m!)
                               (m! (assoc state :show? false :text nil))))
                            (do
                             (on-finish! text d! m!)
                             (m! (assoc state :show? false :text nil))))))}]
           (if (:multiline? options)
             (textarea
              (merge
               props
               {:style (merge
                        ui/textarea
                        {:width "100%", :min-height 120}
                        (:input-style options))}))
             (input
              (merge props {:style (merge ui/input {:width "100%"} (:input-style options))})))))
        (=< nil 16)
        (div
         {:style ui/row-parted}
         (span nil)
         (button
          {:style style/button,
           :on-click (fn [e d! m!]
             (on-finish! text d! m!)
             (m! (assoc state :show? false :text nil)))}
          (<> "Finish")))))))))

(defcomp
 comp-select
 (states selected-value candidates options on-read!)
 (assert (fn? on-read!) "require a callback function")
 (assert (sequential? candidates) "candidates should be a list")
 (let [content (or (:text options) "Select from list:")
       state (or (:data states) {:show? false})]
   (span
    {:style (merge {:cursor :pointer, :display :inline-block} (:style options)),
     :on-click (fn [e d! m!] (m! (assoc state :show? true)))}
    (let [selected (first
                    (filter (fn [option] (= selected-value (:value option))) candidates))]
      (if (some? selected)
        (<> (:display selected) (merge {:display :inline-block} (:style-trigger options)))
        (<>
         (or (:placeholder options) "Nothing")
         (merge
          {:font-family ui/font-fancy, :color (hsl 0 0 60), :display :inline-block}
          (:style-trigger options)))))
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
        (div
         {:style ui/row-parted}
         (<> content {:font-family ui/font-fancy, :color (hsl 0 0 60)})
         (a
          {:style (merge ui/link {:font-family ui/font-fancy}),
           :on-click (fn [e d! m!] (on-read! nil d! m!) (m! (assoc state :show? false)))}
          (<> "Clear")))
        (=< nil 8)
        (if (empty? candidates)
          (<>
           "No item to select"
           {:font-family ui/font-fancy, :color (hsl 0 0 70), :font-size 14})
          (list->
           {}
           (->> candidates
                (map-indexed
                 (fn [idx candidate]
                   (let [value (:value candidate), display (:display candidate)]
                     [(or value idx)
                      (div
                       {:style (merge
                                {:border-bottom (str "1px solid " (hsl 0 0 90)),
                                 :line-height "32px",
                                 :padding "0 8px"}
                                (when (= selected-value (:value candidate))
                                  {:background-color (hsl 0 0 96)})),
                        :on-click (fn [e d! m!]
                          (on-read! value d! m!)
                          (m! (assoc state :show? false)))}
                       (<> (or display "<default display>")))]))))))
        (=< nil 8)))))))

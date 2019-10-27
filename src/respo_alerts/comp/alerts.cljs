
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
              a
              defeffect]]
            [respo.comp.space :refer [=<]]
            [respo-alerts.config :refer [dev?]]
            [respo-alerts.style :as style]
            [respo-alerts.schema :as schema]
            [respo-alerts.util :refer [focus-element! select-element!]]
            [respo-alerts.style :as style]
            [clojure.string :as string]))

(defeffect
 effect-fade
 (show?)
 (action el *local)
 (case action
   :before-update
     (if show?
       (do)
       (let [target (.-firstElementChild el)
             cloned (.cloneNode target true)
             style (.-style cloned)]
         (.appendChild el cloned)
         (js/setTimeout (fn [] (set! (.-opacity style) 0)) 10)
         (js/setTimeout (fn [] (.remove cloned)) 300)))
   :update
     (if show?
       (let [target (.-firstElementChild el), style (.-style target)]
         (set! (.-opacity style) 0)
         (js/setTimeout
          (fn [] (set! (.-transitionDuration style) "300ms") (set! (.-opacity style) 1))
          100))
       (do))
   (do)))

(defeffect
 effect-focus
 (query show?)
 (action el *local)
 (case (:update (when show? (focus-element! query))) (do)))

(defcomp
 comp-alert-modal
 (options show? on-read! on-close!)
 [(effect-focus (str "." schema/confirm-button-name) show?)
  (effect-fade show?)
  (div
   {}
   (if show?
     (div
      {:style (merge ui/fullscreen ui/center style/backdrop),
       :on-click (fn [e d! m!]
         (let [event (:event e)] (.stopPropagation event) (on-read! e d! m!) (on-close! m!)))}
      (div
       {:style (merge ui/column style/card), :on-click (fn [e d! m!] )}
       (div {} (<> (or (:text options) "Alert!")))
       (=< nil 8)
       (div
        {:style ui/row-parted}
        (span nil)
        (button
         {:style style/button,
          :class-name schema/confirm-button-name,
          :on-click (fn [e d! m!] (on-read! e d! m!) (on-close! m!))}
         (<> (or (:button-text options) "Read"))))))))])

(defcomp
 comp-alert
 (states options on-read!)
 (assert (fn? on-read!) "require a callback function")
 (let [trigger (:trigger options), state (or (:data states) {:show? false})]
   (assert (map? trigger) "need to use an element as trigger")
   (span
    {:style (merge {:cursor :pointer, :display :inline-block} (:style options)),
     :on-click (fn [e d! m!] (m! (assoc state :show? true)))}
    trigger
    (comp-alert-modal
     options
     (:show? state)
     on-read!
     (fn [m!] (m! %cursor (assoc state :show? false)))))))

(defcomp
 comp-confirm-modal
 (options show? on-confirm! on-close!)
 [(effect-focus (str "." schema/confirm-button-name) show?)
  (effect-fade show?)
  (div
   {}
   (if show?
     (div
      {:style (merge ui/fullscreen ui/center style/backdrop),
       :on-click (fn [e d! m!] (on-close! m!))}
      (div
       {:style (merge ui/column style/card), :on-click (fn [e d! m!] )}
       (div {} (<> (or (:text options) "Confirm?")))
       (=< nil 8)
       (div
        {:style ui/row-parted}
        (span nil)
        (button
         {:style style/button,
          :class-name schema/confirm-button-name,
          :on-click (fn [e d! m!] (on-confirm! e d! m!) (on-close! m!))}
         (<> (or (:button-text options) "Confirm"))))))))])

(defcomp
 comp-confirm
 (states options on-confirm!)
 (assert (fn? on-confirm!) "require a callback function")
 (let [trigger (:trigger options), state (or (:data states) {:show? false})]
   (assert (map? trigger) "need to use an element as trigger")
   (span
    {:style (merge {:cursor :pointer, :display :inline-block} (:style options)),
     :on-click (fn [e d! m!] (m! (assoc state :show? true)))}
    trigger
    (comp-confirm-modal
     options
     (:show? state)
     on-confirm!
     (fn [m!] (m! %cursor (assoc state :show? false)))))))

(defeffect
 effect-select
 (query show?)
 (action el *local)
 (case (:update (when show? (select-element! query))) (do)))

(defcomp
 comp-prompt-modal
 (states options show? on-finish! on-close!)
 (let [initial-text (or (:initial options) "")
       state (or (:data states) {:text initial-text, :failure nil})
       text (or (:text state) initial-text)
       check-submit! (fn [d! m!]
                       (let [validator (:validator options)
                             result (if (fn? validator) (validator text) nil)]
                         (println "Validate res" result)
                         (if (some? result)
                           (m! (assoc state :failure result))
                           (do
                            (on-finish! text d! m!)
                            (on-close! m!)
                            (m! (assoc state :text nil :failure nil))))))]
   [(effect-select (str "." schema/input-box-name) show?)
    (effect-fade show?)
    (div
     {}
     (if show?
       (div
        {:style (merge ui/fullscreen ui/center style/backdrop),
         :on-click (fn [e d! m!] (on-close! m!) (m! (assoc state :text nil :failure nil)))}
        (div
         {:style (merge ui/column style/card), :on-click (fn [e d! m!] )}
         (div {} (<> (or (:text options) "Type in text")))
         (=< nil 8)
         (div
          {}
          (let [props {:class-name schema/input-box-name,
                       :value text,
                       :on-input (fn [e d! m!] (m! (assoc state :text (:value e)))),
                       :on-keydown (fn [e d! m!]
                         (when (and (not= 229 (:keycode e)) (= (:key e) "Enter"))
                           (if (:multiline? options)
                             (when (.-metaKey (:event e)) (check-submit! d! m!))
                             (check-submit! d! m!)))),
                       :placeholder (or (:placeholder options) "")}]
            (if (:multiline? options)
              (textarea
               (merge
                props
                {:style (merge
                         ui/textarea
                         {:width "100%", :min-height 120}
                         (:input-style options))}))
              (input
               (merge
                props
                {:style (merge ui/input {:width "100%"} (:input-style options))})))))
         (=< nil 16)
         (div
          {:style ui/row-parted}
          (if-let [failure (:failure state)]
            (span
             {:style (merge ui/flex {:color :red, :line-height "20px"}),
              :inner-text failure})
            (span nil))
          (button
           {:style style/button, :on-click (fn [e d! m!] (check-submit! d! m!))}
           (<> (or (:button-text options) "Finish"))))))))]))

(defcomp
 comp-prompt
 (states options on-finish!)
 (assert (fn? on-finish!) "on-finish! a callback function")
 (let [trigger (:trigger options), state (or (:data states) {:show? false, :failure nil})]
   (assert (map? trigger) "need to use an element as trigger")
   (span
    {:style (merge {:cursor :pointer, :display :inline-block} (:style options)),
     :on-click (fn [e d! m!] (m! (assoc state :show? true)))}
    trigger
    (cursor->
     :modal
     comp-prompt-modal
     states
     options
     (:show? state)
     on-finish!
     (fn [m!] (m! %cursor (assoc state :show? false)))))))

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
            (on-read! nil d! m!)
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

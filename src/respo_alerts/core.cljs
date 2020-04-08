
(ns respo-alerts.core
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.core
             :refer
             [defcomp list-> <> >> div button textarea span input a defeffect]]
            [respo.comp.space :refer [=<]]
            [respo-alerts.config :refer [dev?]]
            [respo-alerts.style :as style]
            [respo-alerts.schema :as schema]
            [respo-alerts.util :refer [focus-element! select-element!]]
            [respo-alerts.style :as style]
            [clojure.string :as string]
            [cumulo-util.core :refer [delay!]]))

(defonce *next-confirm-task (atom nil))

(defonce *next-prompt-task (atom nil))

(defeffect
 effect-fade
 (show?)
 (action el *local)
 (case action
   :before-update
     (if show?
       (do)
       (if (some? (.-firstElementChild el))
         (let [target (.-firstElementChild el)
               cloned (.cloneNode target true)
               style (.-style cloned)
               card-style (-> cloned .-firstElementChild .-style)]
           (.appendChild js/document.body cloned)
           (delay!
            0.01
            (fn []
              (set! (.-opacity style) 0)
              (set! (.-transitionDuration card-style) "240ms")
              (set! (.-transform card-style) "scale(0.94) translate(0px,-20px)")))
           (delay! 0.24 (fn [] (.remove cloned))))))
   :update
     (if show?
       (let [target (.-firstElementChild el)
             card-style (-> target .-firstElementChild .-style)
             style (.-style target)]
         (set! (.-opacity style) 0)
         (set! (.-transform card-style) "scale(0.94) translate(0px,-20px)")
         (delay!
          0.01
          (fn []
            (set! (.-transitionDuration style) "240ms")
            (set! (.-transitionDuration card-style) "240ms")
            (set! (.-opacity style) 1)
            (set! (.-transform card-style) "scale(1) translate(0px,0px)"))))
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
   {:style {:position :absolute}}
   (if show?
     (div
      {:style (merge ui/fullscreen ui/center style/backdrop),
       :on-click (fn [e d!]
         (let [event (:event e)] (.stopPropagation event) (on-read! e d!) (on-close! d!)))}
      (div
       {:style (merge ui/column style/card ui/global {:line-height "32px"}),
        :on-click (fn [e d!] )}
       (div {} (<> (or (:text options) "Alert!")))
       (=< nil 8)
       (div
        {:style ui/row-parted}
        (span nil)
        (button
         {:style style/button,
          :class-name schema/confirm-button-name,
          :on-click (fn [e d!] (on-read! e d!) (on-close! d!))}
         (<> (or (:button-text options) "Read"))))))))])

(defcomp
 comp-alert
 (states options on-read!)
 (assert (fn? on-read!) "require a callback function")
 (let [trigger (:trigger options)
       cursor (:cursor states)
       state (or (:data states) {:show? false})]
   (assert (map? trigger) "need to use an element as trigger")
   (span
    {:style (merge {:cursor :pointer, :display :inline-block} (:style options)),
     :on-click (fn [e d!] (d! cursor (assoc state :show? true)))}
    trigger
    (comp-alert-modal
     options
     (:show? state)
     on-read!
     (fn [d!] (d! cursor (assoc state :show? false)))))))

(defcomp
 comp-confirm-modal
 (options show? on-confirm! on-close!)
 [(effect-focus (str "." schema/confirm-button-name) show?)
  (effect-fade show?)
  (div
   {:style {:position :absolute}}
   (if show?
     (div
      {:style (merge ui/fullscreen ui/center style/backdrop),
       :on-click (fn [e d!] (on-close! d!))}
      (div
       {:style (merge ui/column ui/global style/card {:line-height "32px"}),
        :on-click (fn [e d!] )}
       (div {} (<> (or (:text options) "Confirm?")))
       (=< nil 8)
       (div
        {:style ui/row-parted}
        (span nil)
        (button
         {:style style/button,
          :class-name schema/confirm-button-name,
          :on-click (fn [e d!] (on-confirm! e d!) (on-close! d!))}
         (<> (or (:button-text options) "Confirm"))))))))])

(defcomp
 comp-confirm
 (states options on-confirm!)
 (assert (fn? on-confirm!) "require a callback function")
 (let [trigger (:trigger options)
       cursor (:cursor states)
       state (or (:data states) {:show? false})]
   (assert (map? trigger) "need to use an element as trigger")
   (span
    {:style (merge {:cursor :pointer, :display :inline-block} (:style options)),
     :on-click (fn [e d!] (d! cursor (assoc state :show? true)))}
    trigger
    (comp-confirm-modal
     options
     (:show? state)
     on-confirm!
     (fn [d!] (d! cursor (assoc state :show? false)))))))

(defcomp
 comp-modal
 (options show? on-close)
 [(effect-fade show?)
  (div
   {:style (merge {:position :absolute} (:container-style options))}
   (if show?
     (div
      {:style (merge ui/fullscreen ui/center style/backdrop),
       :on-click (fn [e d!] (let [event (:event e)] (.stopPropagation event) (on-close d!)))}
      (div
       {:style (merge
                ui/global
                ui/column
                style/card
                {:padding 0, :line-height "32px"}
                (:style options)),
        :on-click (fn [e d!] )}
       (let [title (:title options)]
         (if (some? title) (div {:style (merge ui/center {:padding "8px"})} (<> title))))
       ((:render-body options))))))])

(def style-menu-item
  {:border-top (str "1px solid " (hsl 0 0 90)),
   :padding "0 16px",
   :cursor :pointer,
   :white-space :nowrap,
   :line-height "40px"})

(defcomp
 comp-modal-menu
 (options show? on-close! on-select!)
 [(effect-fade show?)
  (div
   {}
   (if show?
     (div
      {:style (merge ui/fullscreen ui/center style/backdrop),
       :on-click (fn [e d!]
         (let [event (:event e)] (.stopPropagation event) (on-close! d!)))}
      (div
       {:style (merge
                ui/column
                ui/global
                style/card
                {:padding 0, :line-height "32px"}
                (:style options)),
        :on-click (fn [e d!] )}
       (let [title (:title options)]
         (if (some? title)
           (div
            {:style (merge
                     ui/center
                     {:padding "4px", :font-family ui/font-fancy, :color (hsl 0 0 70)})}
            (<> title))))
       (list->
        {}
        (->> (:items options)
             (map
              (fn [item]
                [(:value item)
                 (div
                  {:style style-menu-item, :on-click (fn [e d!] (on-select! item d!))}
                  (let [display (:display item)]
                    (if (string? display) (<> display) display)))]))))))))])

(defeffect
 effect-select
 (query show?)
 (action el *local)
 (case (:update (when show? (select-element! query))) (do)))

(defcomp
 comp-prompt-modal
 (states options show? on-finish! on-close!)
 (let [initial-text (or (:initial options) "")
       cursor (:cursor states)
       state (or (:data states) {:text initial-text, :failure nil})
       text (or (:text state) initial-text)
       check-submit! (fn [d!]
                       (let [validator (:validator options)
                             result (if (fn? validator) (validator text) nil)]
                         (if (some? result)
                           (d! cursor (assoc state :failure result))
                           (do
                            (on-finish! text d!)
                            (on-close! d!)
                            (d! cursor (assoc state :text nil :failure nil))))))]
   [(effect-select (str "." schema/input-box-name) show?)
    (effect-fade show?)
    (div
     {:style {:position :absolute}}
     (if show?
       (div
        {:style (merge
                 ui/fullscreen
                 ui/global
                 ui/center
                 style/backdrop
                 {:line-height "32px"}),
         :on-click (fn [e d!]
           (on-close! d!)
           (d! cursor (assoc state :text nil :failure nil)))}
        (div
         {:style (merge ui/column ui/global style/card {:line-height "32px"}),
          :on-click (fn [e d!] )}
         (div {} (<> (or (:text options) "Type in text")))
         (=< nil 8)
         (div
          {}
          (let [props {:class-name schema/input-box-name,
                       :value text,
                       :on-input (fn [e d!] (d! cursor (assoc state :text (:value e)))),
                       :on-keydown (fn [e d!]
                         (when (and (not= 229 (:keycode e)) (= (:key e) "Enter"))
                           (if (:multiline? options)
                             (when (.-metaKey (:event e)) (check-submit! d!))
                             (check-submit! d!)))),
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
           {:style style/button, :on-click (fn [e d!] (check-submit! d!))}
           (<> (or (:button-text options) "Finish"))))))))]))

(defcomp
 comp-prompt
 (states options on-finish!)
 (assert (fn? on-finish!) "on-finish! a callback function")
 (let [trigger (:trigger options)
       cursor (:cursor states)
       state (or (:data states) {:show? false, :failure nil})]
   (assert (map? trigger) "need to use an element as trigger")
   (span
    {:style (merge {:cursor :pointer, :display :inline-block} (:style options)),
     :on-click (fn [e d!] (d! cursor (assoc state :show? true)))}
    trigger
    (comp-prompt-modal
     (>> states :modal)
     options
     (:show? state)
     on-finish!
     (fn [d!] (d! cursor (assoc state :show? false)))))))

(defcomp
 comp-select-modal
 (candidates selected-value options show? on-read! on-close)
 [(effect-fade show?)
  (div
   {}
   (if show?
     (div
      {:style (merge ui/fullscreen ui/center style/backdrop),
       :on-click (fn [e d!]
         (let [event (:event e)] (.stopPropagation event) (on-read! nil d!) (on-close d!)))}
      (div
       {:style (merge ui/column ui/global style/card {:line-height "32px"}),
        :on-click (fn [e d!] )}
       (div
        {:style ui/row-parted}
        (<>
         (or (:text options) "Select from list:")
         {:font-family ui/font-fancy, :color (hsl 0 0 60)})
        (a
         {:style (merge ui/link {:font-family ui/font-fancy}),
          :on-click (fn [e d!] (on-read! nil d!) (on-close d!))}
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
                                :line-height "40px",
                                :padding "0 8px"}
                               (when (= selected-value (:value candidate))
                                 {:background-color (hsl 0 0 96)})),
                       :on-click (fn [e d!] (on-read! value d!) (on-close d!))}
                      (<> (or display "<default display>")))]))))))
       (=< nil 8)))))])

(defcomp
 comp-select
 (states selected-value candidates options on-read!)
 (assert (fn? on-read!) "require a callback function")
 (assert (sequential? candidates) "candidates should be a list")
 (let [cursor (:cursor states), state (or (:data states) {:show? false})]
   (span
    {:style (merge {:cursor :pointer, :display :inline-block} (:style options)),
     :on-click (fn [e d!] (d! cursor (assoc state :show? true)))}
    (let [selected (first
                    (filter (fn [option] (= selected-value (:value option))) candidates))]
      (if (some? selected)
        (<> (:display selected) (merge {:display :inline-block} (:style-trigger options)))
        (<>
         (or (:placeholder options) "Nothing")
         (merge
          {:font-family ui/font-fancy, :color (hsl 0 0 60), :display :inline-block}
          (:style-trigger options)))))
    (comp-select-modal
     candidates
     selected-value
     options
     (:show? state)
     on-read!
     (fn [d!] (d! cursor (assoc state :show? false)))))))

(defn use-alert [states options]
  (let [cursor (:cursor states)
        state (or (:data states) {:show? false})
        on-read (or (:on-read options) (fn [d!] (d! cursor (assoc state :show? false))))]
    {:ui (comp-alert-modal
          options
          (:show? state)
          on-read
          (fn [d!] (d! cursor (assoc state :show? false)))),
     :show (fn [d!] (d! cursor (assoc state :show? true)))}))

(defn use-confirm [states options]
  (let [cursor (:cursor states), state (or (:data states) {:show? false})]
    {:ui (comp-confirm-modal
          options
          (:show? state)
          (fn [e d!]
            (if (some? @*next-confirm-task) (@*next-confirm-task))
            (reset! *next-confirm-task nil))
          (fn [d!] (d! cursor (assoc state :show? false)) (reset! *next-confirm-task nil))),
     :show (fn [d! next-task]
       (reset! *next-confirm-task next-task)
       (d! cursor (assoc state :show? true)))}))

(defn use-prompt [states options]
  (let [cursor (:cursor states), state (or (:data states) {:show? false, :failure nil})]
    {:ui (comp-prompt-modal
          (>> states :modal)
          options
          (:show? state)
          (fn [text d!]
            (if (some? @*next-prompt-task) (@*next-prompt-task text))
            (reset! *next-prompt-task nil)
            (d! cursor (assoc state :show? false)))
          (fn [d!] (d! cursor (assoc state :show? false)) (reset! *next-prompt-task nil))),
     :show (fn [d! next-task]
       (reset! *next-prompt-task next-task)
       (d! cursor (assoc state :show? true)))}))

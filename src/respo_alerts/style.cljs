
(ns respo-alerts.style (:require [hsl.core :refer [hsl]]))

(def backdrop {:background-color (hsl 0 30 10 0.6), :position :fixed})

(def card
  {:background-color (hsl 0 0 100),
   :min-width 320,
   :max-width "80vw",
   :padding 16,
   :border-radius "16px",
   :color (hsl 0 0 0)})

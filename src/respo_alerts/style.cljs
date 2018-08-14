
(ns respo-alerts.style (:require [hsl.core :refer [hsl]] [respo-ui.core :as ui]))

(def backdrop {:background-color (hsl 0 30 10 0.6), :position :fixed, :z-index 999})

(def button (merge ui/button {:border-radius "4px", :border-color (hsl 240 60 90)}))

(def card
  {:background-color (hsl 0 0 100),
   :min-width 480,
   :max-width "80vw",
   :max-height "80vh",
   :overflow :auto,
   :padding 16,
   :border-radius "4px",
   :color (hsl 0 0 0)})

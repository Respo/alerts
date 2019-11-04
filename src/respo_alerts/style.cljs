
(ns respo-alerts.style (:require [hsl.core :refer [hsl]] [respo-ui.core :as ui]))

(def backdrop
  {:background-color (hsl 0 30 10 0.6), :position :fixed, :z-index 999, :padding 16})

(def button
  (merge
   ui/button
   {:border-radius "4px", :background-color :white, :border-color (hsl 240 60 90)}))

(def card
  {:background-color (hsl 0 0 100),
   :max-width "600px",
   :width "100%",
   :max-height "80vh",
   :overflow :auto,
   :border-radius "3px",
   :color (hsl 0 0 0),
   :margin :auto,
   :padding 16})
